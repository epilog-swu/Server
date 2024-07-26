package com.epi.epilog.app.service;

import com.epi.epilog.app.dto.SensorData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FallDetectionService {
    private static final double THRESHOLD_ASVM = 4.9;
    private static final double THRESHOLD_GSVM = 0.0;
    private static final double THRESHOLD_ANGLE_X = 0.0;
    private static final double THRESHOLD_ANGLE_Y = 0.0;
    private static final double THRESHOLD_ANGLE_Z = 60.0;
    private static final int ASVM_THRESHOLD_COUNT = 60;
    private static final int GSVM_THRESHOLD_COUNT = 20;
    private static final int ANGLE_THRESHOLD_COUNT = 0;
    private static final int BASELINE_WINDOW_SIZE = 10;
    private static final double DELTA_TIME = 0.01;

    public boolean isFallDetected(List<SensorData> data) {
        if (data.size() < BASELINE_WINDOW_SIZE) {
            log.warn("Not enough sensor data: required {}, but got {}", BASELINE_WINDOW_SIZE, data.size());
            return false;
        }

        int aSvmThresholdExceedCount = 0;
        int gSvmThresholdExceedCount = 0;
        int angleXExceedCount = 0;
        int angleYExceedCount = 0;
        double baseX = 0;
        double baseY = 0;
        double baseZ = 0;
        double baseGyroX = 0;
        double baseGyroY = 0;
        double baseGyroZ = 0;
        double pitch = 0.0;
        double roll = 0.0;

        for (int i = 0; i < BASELINE_WINDOW_SIZE; i++) {
            SensorData entry = data.get(i);
            baseX += entry.getAccX();
            baseY += entry.getAccY();
            baseZ += entry.getAccZ();
            baseGyroX += entry.getGyroX();
            baseGyroY += entry.getGyroY();
            baseGyroZ += entry.getGyroZ();
        }

        baseX /= BASELINE_WINDOW_SIZE;
        baseY /= BASELINE_WINDOW_SIZE;
        baseZ /= BASELINE_WINDOW_SIZE;
        baseGyroX /= BASELINE_WINDOW_SIZE;
        baseGyroY /= BASELINE_WINDOW_SIZE;
        baseGyroZ /= BASELINE_WINDOW_SIZE;

        for (int i = 0; i < data.size(); i++) {
            SensorData entry = data.get(i);
            double deltaX = entry.getAccX() - baseX;
            double deltaY = entry.getAccY() - baseY;
            double deltaZ = entry.getAccZ() - baseZ;
            double deltaGyroX = entry.getGyroX() - baseGyroX;
            double deltaGyroY = entry.getGyroY() - baseGyroY;
            double deltaGyroZ = entry.getGyroZ() - baseGyroZ;

            double aSvm = calculateASVM(deltaX, deltaY, deltaZ);
            pitch = calculatePitch(deltaGyroY, DELTA_TIME, pitch);
            roll = calculateRoll(deltaGyroX, DELTA_TIME, roll);
            double gSvm = calculateGSVM(pitch, roll);

            double angleY = calculateThetaY(deltaX, deltaY, deltaZ);
            double angleX = calculateThetaX(deltaX, deltaY, deltaZ);

            if (aSvm > THRESHOLD_ASVM) {
                aSvmThresholdExceedCount++;
            }
            if (gSvm > THRESHOLD_GSVM) {
                gSvmThresholdExceedCount++;
            }
            if (angleX > THRESHOLD_ANGLE_X) {
                angleXExceedCount++;
            }
            if (angleY > THRESHOLD_ANGLE_Y) {
                angleYExceedCount++;
            }

            if (aSvmThresholdExceedCount > ASVM_THRESHOLD_COUNT
                    && gSvmThresholdExceedCount > GSVM_THRESHOLD_COUNT
                    && (angleYExceedCount > ANGLE_THRESHOLD_COUNT && angleXExceedCount > ANGLE_THRESHOLD_COUNT)
            ) {
                log.info("(True result) Exceed count - ASVM: " + aSvmThresholdExceedCount + ", GSVM: " + gSvmThresholdExceedCount);
                return true;
            }
        }
        log.info("(False result) Exceed count - ASVM: " + aSvmThresholdExceedCount + ", GSVM: " + gSvmThresholdExceedCount);
        return false;
    }

    private Double calculateASVM(Double x, Double y, Double z) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    private Double calculateGSVM(Double P, Double R) {
        return Math.sqrt(Math.pow(P, 2) + Math.pow(R, 2));
    }

    private Double calculateThetaX(Double x, Double y, Double z) {
        return Math.toDegrees(Math.acos(x / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2))));
    }

    private Double calculateThetaY(Double x, Double y, Double z) {
        return Math.toDegrees(Math.acos(y / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2))));
    }

    public static double calculatePitch(Double gyroY, Double deltaTime, Double previousPitch) {
        return previousPitch + gyroY * deltaTime;
    }

    public static double calculateRoll(Double gyroX, Double deltaTime, Double previousRoll) {
        return previousRoll + gyroX * deltaTime;
    }
}
