package com.epi.epilog.app.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "sensor_data")
public class SensorData {
    private Double accX;
    private Double accY;
    private Double accZ;
    private Double gyroX;
    private Double gyroY;
    private Double gyroZ;
}
