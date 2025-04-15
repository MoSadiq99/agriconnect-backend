import json
import logging
import pandas as pd
import requests
from datetime import timedelta

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# API endpoints (adjust to your Spring Boot setup)
BASE_URL = "http://localhost:8080/api"
FARMER_CULTIVATIONS_URL = f"{BASE_URL}/farmer/cultivations"
BUYER_REQUESTS_URL = f"{BASE_URL}/buyer/requests"

# Forecast window around harvest date (±7 days)
FORECAST_WINDOW_DAYS = 7


def fetch_data(url, entity_name):
    """Fetch data from Spring Boot API."""
    try:
        response = requests.get(url)
        response.raise_for_status()
        return response.json()
    except requests.RequestException as e:
        logger.error(f"Failed to fetch {entity_name}: {e}")
        return []


def preprocess_farmer_cultivations(cultivations, exclude_crop=None, exclude_harvest_date=None):
    """Convert farmer cultivation data into a DataFrame, excluding the farmer's input if specified."""
    data = []
    for cult in cultivations:
        harvest_date = pd.to_datetime(cult.get('harvestDate'))
        crop_type = cult.get('cropType', '').lower()
        if (exclude_crop and exclude_harvest_date and
                crop_type == exclude_crop and harvest_date == exclude_harvest_date):
            continue  # Skip the farmer's own input
        data.append({
            'crop_type': crop_type,
            'expected_yield': float(cult.get('expectedYield', 0)),
            'harvest_date': harvest_date
        })
    df = pd.DataFrame(data)
    return df


def preprocess_buyer_requests(requests):
    """Convert buyer requests into a DataFrame, projecting demand."""
    data = []
    for req in requests:
        crop_type = req.get('cropType', '').lower()
        start_date = pd.to_datetime(req.get('startDate'))
        deadline = pd.to_datetime(req.get('deadline'))
        quantity = float(req.get('quantity', 0))
        request_type = req.get('requestType')

        # Calculate total demand based on request type
        days = (deadline - start_date).days + 1
        if request_type == 'DAILY_AVERAGE':
            total_demand = quantity * days if days > 0 else quantity
        elif request_type == 'WEEKLY':
            weeks = days // 7
            total_demand = quantity * max(weeks, 1)
        elif request_type == 'MONTHLY':
            months = days // 30
            total_demand = quantity * max(months, 1)
        else:  # ONE_TIME
            total_demand = quantity

        data.append({
            'crop_type': crop_type,
            'total_demand': total_demand,
            'start_date': start_date,
            'deadline': deadline
        })
    df = pd.DataFrame(data)
    return df


def aggregate_supply_demand(cultivations_df, requests_df, crop_type, harvest_date):
    """Aggregate supply and demand for the specified crop within the harvest window."""
    forecast_start = harvest_date - timedelta(days=FORECAST_WINDOW_DAYS)
    forecast_end = harvest_date + timedelta(days=FORECAST_WINDOW_DAYS)

    # Filter cultivations for the crop and harvest window (other farmers' supply)
    supply_df = cultivations_df[
        (cultivations_df['crop_type'] == crop_type) &
        (cultivations_df['harvest_date'] >= forecast_start) &
        (cultivations_df['harvest_date'] <= forecast_end)
        ].agg({
        'expected_yield': 'sum'
    }).rename({'expected_yield': 'total_supply'})

    # Filter buyer requests for the crop and overlapping periods
    demand_df = requests_df[
        (requests_df['crop_type'] == crop_type) &
        (requests_df['deadline'] >= forecast_start) &
        (requests_df['start_date'] <= forecast_end)
        ].agg({
        'total_demand': 'sum'
    })

    # Combine into a single-row DataFrame
    combined_df = pd.DataFrame({
        'total_supply': [supply_df.get('total_supply', 0)],
        'total_demand': [demand_df.get('total_demand', 0)]
    }, index=[crop_type])
    combined_df['demand_supply_gap'] = combined_df['total_demand'] - combined_df['total_supply']
    return combined_df


def generate_recommendation(combined_df, crop_type, farmer_yield):
    """Generate recommendation based on demand-supply gap."""
    row = combined_df.iloc[0]
    total_supply = row['total_supply'] + farmer_yield  # Include farmer's planned yield
    total_demand = row['total_demand']
    gap = total_demand - total_supply

    if gap > 0:
        return {
            'crop_type': crop_type,
            'recommendation': 'Proceed with cultivation',
            'reason': f"Demand ({total_demand:.2f} kg) exceeds supply ({total_supply:.2f} kg) by {gap:.2f} kg in the harvest window",
            'confidence': min(total_demand / max(total_supply, 1), 1.0)  # Confidence based on demand coverage
        }
    elif gap < 0:
        return {
            'crop_type': crop_type,
            'recommendation': 'Reconsider or reduce quantity',
            'reason': f"Supply ({total_supply:.2f} kg) exceeds demand ({total_demand:.2f} kg) by {-gap:.2f} kg; potential wastage risk",
            'confidence': min(total_supply / max(total_demand, 1), 1.0)
        }
    else:
        return {
            'crop_type': crop_type,
            'recommendation': 'Proceed with caution',
            'reason': 'Supply matches demand exactly; monitor market trends',
            'confidence': 1.0
        }


def forecast_demand_for_crop(crop_type, expected_harvest_date, farmer_yield=0):
    """Forecast demand for a specific crop and harvest date."""
    crop_type = crop_type.lower()
    harvest_date = pd.to_datetime(expected_harvest_date)

    # Fetch data
    cultivations = fetch_data(FARMER_CULTIVATIONS_URL, "farmer cultivations")
    requests = fetch_data(BUYER_REQUESTS_URL, "buyer requests")

    if not cultivations:
        logger.warning("No cultivation data available")
        return {'status': 'error', 'message': 'Insufficient data to forecast'}

    # Preprocess data, excluding the farmer's own input
    cultivations_df = preprocess_farmer_cultivations(cultivations, crop_type, harvest_date)
    requests_df = preprocess_buyer_requests(requests) if requests else pd.DataFrame()

    # Aggregate supply and demand
    combined_df = aggregate_supply_demand(cultivations_df, requests_df, crop_type, harvest_date)

    # Generate recommendation
    recommendation = generate_recommendation(combined_df, crop_type, farmer_yield)

    # Response
    forecast_start = harvest_date - timedelta(days=FORECAST_WINDOW_DAYS)
    forecast_end = harvest_date + timedelta(days=FORECAST_WINDOW_DAYS)
    response = {
        'status': 'success',
        'forecast_period': {
            'start': forecast_start.strftime('%Y-%m-%d'),
            'end': forecast_end.strftime('%Y-%m-%d'),
            'expected_harvest_date': harvest_date.strftime('%Y-%m-%d')
        },
        'supply_demand_summary': combined_df.to_dict(orient='index'),
        'recommendation': recommendation
    }
    return response


def main():
    # Example usage
    crop_type = "tomatoes"
    harvest_date = "2025-07-15"  # Input from frontend
    farmer_yield = 100  # Optional: farmer's planned yield
    result = forecast_demand_for_crop(crop_type, harvest_date, farmer_yield)
    print(json.dumps(result, indent=2, default=str))
    with open('forecast_result.json', 'w') as f:
        json.dump(result, f, indent=2, default=str)


if __name__ == "__main__":
    main()
