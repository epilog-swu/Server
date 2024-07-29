package com.epi.epilog.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorData {
    private Double accX;
    private Double accY;
    private Double accZ;
    private Double gyroX;
    private Double gyroY;
    private Double gyroZ;
}
