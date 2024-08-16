import numpy as np
import tensorflow as tf
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from typing import List
import logging

from sklearn.preprocessing import StandardScaler
from tensorflow.keras.preprocessing.sequence import pad_sequences

# logging.basicConfig(level=logging.INFO, format="%(asctime)s - %(name)s - %(levelname)s - %(message)s")
# logger = logging.getLogger(__name__)

app = FastAPI()

model = tf.keras.models.load_model('./static/LSTM_model/model_5.keras')

class SensorData(BaseModel):
    accX: float
    accY: float
    accZ: float
    gyroX: float
    gyroY: float
    gyroZ: float

class RequestBody(BaseModel):
    fall: List[SensorData]

def preprocess_input(data: List[SensorData]):
    data = np.array([[item.accX, item.accY, item.accZ, item.gyroX, item.gyroY, item.gyroZ] for item in data])
    
    scaler = StandardScaler()
    data = scaler.fit_transform(data)
    
    max_sequence_length = 100 
    padded_data = pad_sequences([data], maxlen=max_sequence_length, padding='post', dtype='float32')
    
    return padded_data

@app.post("/api/ai/predict")
async def predict(item: RequestBody):
    try:
        input_data = preprocess_input(item.fall)

        prediction = model.predict(input_data)

        result = bool(prediction[0] > 0.5)
        return {"result": result}

    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8000)
