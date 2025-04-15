import joblib
from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

# Load the model and scaler
model = joblib.load('rice_price_forecast_xgb_model_v3.pkl')
scaler = joblib.load('scaler_xgb_v3.pkl')


# Define the input data model
class PredictionRequest(BaseModel):
    exchange_rate: float
    fuel_price: float
    month: int
    exchange_rate_lag1: float
    fuel_price_lag1: float
    exchange_rate_rolling_avg: float
    fuel_price_rolling_avg: float


@app.post("/predict")
def predict(request: PredictionRequest):
    # Prepare the input features
    features = [
        request.exchange_rate, request.fuel_price, request.month,
        request.exchange_rate_lag1, request.fuel_price_lag1,
        request.exchange_rate_rolling_avg, request.fuel_price_rolling_avg
    ]

    # Scale the features
    scaled_features = scaler.transform([features])[0]

    # Make predictions
    retail_price, producer_price = model.predict([scaled_features])[0]

    # Return the predictions
    return {
        "retail_price": float(retail_price),
        "producer_price": float(producer_price)
    }


if __name__ == '__main__':
    import uvicorn

    uvicorn.run(app, host='0.0.0.0', port=5000)

#%%
