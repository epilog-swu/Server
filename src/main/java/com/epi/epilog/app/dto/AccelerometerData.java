package com.epi.epilog.app.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "sensor_data")
public class AccelerometerData {
    private double x;
    private double y;
    private double z;
}
