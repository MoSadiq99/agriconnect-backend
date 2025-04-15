import json
import logging
import pandas as pd
import requests
from datetime import timedelta

# Configure logging
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# API endpoints
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
            continue
        data.append({
            'crop_type': crop_type,
            'expected_yield': float(cult.get('expectedYield', 0)),
            'harvest_date': harvest_date
        })
    return pd.DataFrame(data)

def preprocess_buyer_requests(requests, forecast_start, forecast_end):
    """Convert buyer requests into a DataFrame, projecting demand based on RequestType."""
    data = []
    for req in requests:
        crop_type = req.get('cropType', '').lower()
        start_date = pd.to_datetime(req.get('startDate'))
        deadline = pd.to_datetime(req.get('deadline'))
        quantity = float(req.get('quantity', 0))
        request_type = req.get('requestType', 'ONE_TIME')  # Default to ONE_TIME if missing

        # Calculate overlap with forecast window
        overlap_start = max(start_date, forecast_start)
        overlap_end = min(deadline, forecast_end)
        overlap_days = max(0, (overlap_end - overlap_start).days + 1)

        if overlap_days <= 0:
            continue  # No overlap with forecast window

        # Calculate total demand based on RequestType
        if request_type == 'DAILY_AVERAGE':
            total_demand = quantity * overlap_days
        elif request_type == 'WEEKLY_AVERAGE':
            weeks = overlap_days / 7
            total_demand = quantity * max(1, weeks)  # At least one week’s worth
        elif request_type == 'MONTHLY_AVERAGE':
            months = overlap_days / 30
            total_demand = quantity * max(1, months)  # At least one month’s worth
        elif request_type == 'ONE_TIME':
            total_demand = quantity if overlap_days > 0 else 0
        elif request_type == 'URGENT':
            total_demand = quantity * 1.5 if overlap_days > 0 else 0  # Weight urgent requests higher
        else:
            total_demand = quantity  # Fallback for unknown types

        data.append({
            'crop_type': crop_type,
            'total_demand': total_demand,
            'start_date': start_date,
            'deadline': deadline,
            'request_type': request_type
        })
    return pd.DataFrame(data)

def aggregate_supply_demand(cultivations_df, requests_df, crop_type, harvest_date):
    """Aggregate supply and demand for the specified crop within the harvest window."""
    forecast_start = harvest_date - timedelta(days=FORECAST_WINDOW_DAYS)
    forecast_end = harvest_date + timedelta(days=FORECAST_WINDOW_DAYS)

    supply_df = cultivations_df[
        (cultivations_df['crop_type'] == crop_type) &
        (cultivations_df['harvest_date'] >= forecast_start) &
        (cultivations_df['harvest_date'] <= forecast_end)
        ].agg({'expected_yield': 'sum'}).rename({'expected_yield': 'total_supply'})

    demand_df = requests_df[
        (requests_df['crop_type'] == crop_type)
    ].agg({'total_demand': 'sum'})

    combined_df = pd.DataFrame({
        'total_supply': [supply_df.get('total_supply', 0)],
        'total_demand': [demand_df.get('total_demand', 0)]
    }, index=[crop_type])
    combined_df['demand_supply_gap'] = combined_df['total_demand'] - combined_df['total_supply']
    return combined_df

def generate_recommendation(combined_df, crop_type, farmer_yield, has_cultivations, has_requests):
    """Generate a nuanced recommendation based on data availability and demand-supply gap."""
    row = combined_df.iloc[0]
    total_supply = row['total_supply'] + farmer_yield
    total_demand = row['total_demand']
    gap = total_demand - total_supply

    if not has_cultivations and not has_requests:
        return {
            'crop_type': crop_type,
            'recommendation': 'Proceed with caution',
            'reason': f"No cultivation or buyer request data available for {crop_type} around this period. Consider local market trends or consult other farmers.",
            'confidence': 0.1,  # Low confidence due to no data
            'data_availability': 'none'
        }
    elif not has_requests:
        return {
            'crop_type': crop_type,
            'recommendation': 'Reconsider or explore demand',
            'reason': f"Supply from other farmers ({row['total_supply']:.2f} kg) exists, but no buyer requests found for {crop_type} in this period. Your {farmer_yield:.2f} kg may risk wastage unless demand emerges.",
            'confidence': 0.3,  # Low confidence without demand data
            'data_availability': 'supply_only'
        }
    elif not has_cultivations and total_demand > 0:
        return {
            'crop_type': crop_type,
            'recommendation': 'Proceed with cultivation',
            'reason': f"No other farmers are cultivating {crop_type} in this period, but demand exists ({total_demand:.2f} kg). Your {farmer_yield:.2f} kg could meet this need.",
            'confidence': 0.7,  # Moderate confidence with demand but no supply
            'data_availability': 'demand_only'
        }
    else:
        if gap > 0:
            return {
                'crop_type': crop_type,
                'recommendation': 'Proceed with cultivation',
                'reason': f"Demand ({total_demand:.2f} kg) exceeds total supply ({total_supply:.2f} kg) by {gap:.2f} kg in the harvest window.",
                'confidence': min(total_demand / max(total_supply, 1), 1.0),
                'data_availability': 'both'
            }
        elif gap < 0:
            return {
                'crop_type': crop_type,
                'recommendation': 'Reconsider or reduce quantity',
                'reason': f"Total supply ({total_supply:.2f} kg) exceeds demand ({total_demand:.2f} kg) by {-gap:.2f} kg; potential wastage risk.",
                'confidence': min(total_supply / max(total_demand, 1), 1.0),
                'data_availability': 'both'
            }
        else:
            return {
                'crop_type': crop_type,
                'recommendation': 'Proceed with caution',
                'reason': f"Supply ({total_supply:.2f} kg) matches demand ({total_demand:.2f} kg) exactly; monitor market trends.",
                'confidence': 1.0,
                'data_availability': 'both'
            }

def forecast_demand_for_crop(crop_type, expected_harvest_date, farmer_yield=0):
    """Forecast demand for a specific crop and harvest date with enhanced response."""
    crop_type = crop_type.lower()
    harvest_date = pd.to_datetime(expected_harvest_date)
    forecast_start = harvest_date - timedelta(days=FORECAST_WINDOW_DAYS)
    forecast_end = harvest_date + timedelta(days=FORECAST_WINDOW_DAYS)

    # Fetch data
    cultivations = fetch_data(FARMER_CULTIVATIONS_URL, "farmer cultivations")
    requests = fetch_data(BUYER_REQUESTS_URL, "buyer requests")

    # Preprocess data
    cultivations_df = preprocess_farmer_cultivations(cultivations, crop_type, harvest_date)
    requests_df = preprocess_buyer_requests(requests, forecast_start, forecast_end) if requests else pd.DataFrame()

    # Aggregate supply and demand
    combined_df = aggregate_supply_demand(cultivations_df, requests_df, crop_type, harvest_date)

    # Check data availability
    has_cultivations = not cultivations_df[cultivations_df['crop_type'] == crop_type].empty
    has_requests = not requests_df[requests_df['crop_type'] == crop_type].empty

    # Generate recommendation
    recommendation = generate_recommendation(combined_df, crop_type, farmer_yield, has_cultivations, has_requests)

    # Response
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
    import sys
    if len(sys.argv) < 3:
        print(json.dumps({'status': 'error', 'message': 'Missing arguments: crop_type, expected_harvest_date'}, indent=2))
        sys.exit(1)

    crop_type = sys.argv[1]
    expected_harvest_date = sys.argv[2]
    farmer_yield = float(sys.argv[3]) if len(sys.argv) > 3 else 0

    result = forecast_demand_for_crop(crop_type, expected_harvest_date, farmer_yield)
    print(json.dumps(result, indent=2, default=str))
    with open('forecast_result.json', 'w') as f:
        json.dump(result, f, indent=2, default=str)

if __name__ == "__main__":
    main()