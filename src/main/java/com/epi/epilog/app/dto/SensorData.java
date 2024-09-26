package com.epi.epilog.app.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SensorData {
    private double accX;
    private double accY;
    private double accZ;
    private double gyroX;
    private double gyroY;
    private double gyroZ;
}
