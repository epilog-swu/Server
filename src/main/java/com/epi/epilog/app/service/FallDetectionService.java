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
    private static final double THRESHOLD_ASVM = 5.5;
    private static final double THRESHOLD_GSVM = 150;
    private static final double THRESHOLD_ANGLE_X = 60.0;
    private static final double THRESHOLD_ANGLE_Y = 60.0;
    private static final double THRESHOLD_ANGLE_Z = 30.0;
    private static final int ASVM_THRESHOLD_COUNT = 60;
    private static final int GSVM_THRESHOLD_COUNT = 20;
    private static final int ANGLE_THRESHOLD_COUNT = 20;
    private static final int BASELINE_WINDOW_SIZE = 10;
    private static final double ALPHA = 0.98;

    public boolean isFallDetected(List<SensorData> data) {
        if (data.size() < BASELINE_WINDOW_SIZE) {
            log.warn("Not enough sensor data: required {}, but got {}", BASELINE_WINDOW_SIZE, data.size());
            return false;
        }

        int aSvmThresholdExceedCount = 0;
        int gSvmThresholdExceedCount = 0;
        int angleXExceedCount = 0;
        int angleYExceedCount = 0;
        int angleZExceedCount = 0;
        double baseX = 0;
        double baseY = 0;
        double baseZ = 0;
        double baseGyroX = 0;
        double baseGyroY = 0;
        double baseGyroZ = 0;

        double prevPitch = 0.0;
        double prevRoll = 0.0;
        double pitch = 0.0;
        double roll = 0.0;

//        double beforeAngleX = 0;
//        double beforeAngleY = 0;
//        double beforeAngleZ = 0;

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
            double pitchAcc = calculatePitch(deltaX, deltaY, deltaZ);
            double rollAcc = calculateRoll(deltaX, deltaZ);

            double deltaPitch = calculateDeltaPitch(deltaGyroX, .01);
            double deltaRoll = calculateDeltaRoll(deltaGyroY, .01);

            double gyroPitch = prevPitch + deltaPitch;
            double gyroRoll = prevRoll + deltaRoll;

            pitch = ALPHA * gyroPitch + (1 - ALPHA) * pitchAcc;
            roll = ALPHA * gyroRoll + (1 - ALPHA) * rollAcc;

            double gSvm = calculateGSVM(pitch, roll);

            prevPitch = pitch;
            prevRoll = roll;

            double angleX = calculateThetaX(deltaX, deltaY, deltaZ);
            double angleY = calculateThetaY(deltaX, deltaY, deltaZ);
            double angleZ = calculateThetaZ(deltaX, deltaY, deltaZ);

//            if (i % 10 == 0) {
//                log.info("Sensor values at index {}: accX = {}, accY = {}, accZ = {}, aSvm = {}, gSvm = {}", //, angleX = {}, angleY = {}, angleZ = {}",
//                        i, deltaX, deltaY, deltaZ, aSvm, gSvm); //, angleX, angleY, angleZ);
//            }
            //
            //

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
            if (angleZ > THRESHOLD_ANGLE_Z) {
                angleZExceedCount++;
            }

//            if (i % 10 == 0) {
//                log.info("gSvm: " + gSvm + " angleX: " + abs(abs(angleX) - abs(beforeAngleX)) + " angleY: " + abs(abs(angleY) - abs(beforeAngleY)) + " angleZ: " + abs(abs(angleZ) - abs(beforeAngleZ)));
//            }

            if (aSvmThresholdExceedCount > ASVM_THRESHOLD_COUNT
                    && gSvmThresholdExceedCount > GSVM_THRESHOLD_COUNT
//                    && angleXExceedCount > ANGLE_THRESHOLD_COUNT
//                    && angleYExceedCount > ANGLE_THRESHOLD_COUNT
//                    && angleZExceedCount > ANGLE_THRESHOLD_COUNT
            ) {
//                log.info("(True result) Exceed count - ASVM: " + aSvmThresholdExceedCount + ", GSVM: " + gSvmThresholdExceedCount + ", Zcount: " + angleZExceedCount + ", Xcount: " + angleXExceedCount + ", YCount: " + angleYExceedCount);
                return true;
            }
        }
//        log.info("(False result) Exceed count - ASVM: " + aSvmThresholdExceedCount + ", GSVM: " + gSvmThresholdExceedCount + ", Zcount: " + angleZExceedCount + ", Xcount: " + angleXExceedCount + ", YCount: " + angleYExceedCount);
        return false;
    }

    private Double calculateASVM(Double x, Double y, Double z) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    private Double calculateGSVM(Double P, Double R) {
        double thetaP = Math.toDegrees(P);
        double thetaR = Math.toDegrees(R);
        return Math.sqrt(Math.pow(thetaP, 2) + Math.pow(thetaR, 2));
    }

    private Double calculateThetaX(Double x, Double y, Double z) {
        return Math.toDegrees(Math.atan(Math.sqrt(Math.pow(z, 2) + Math.pow(y, 2))/x));
    }

    private Double calculateThetaY(Double x, Double y, Double z) {
        return Math.toDegrees(Math.atan(Math.sqrt(Math.pow(x, 2) + Math.pow(z, 2))/y));
    }

    private Double calculateThetaZ(Double x, Double y, Double z) {
        return Math.toDegrees(Math.atan(Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2))/z));
    }

    public static double calculatePitch(Double accX, Double accY, Double accZ) {
        return Math.atan2(accY, Math.sqrt(accX * accX + accZ * accZ));
    }

    public static double calculateRoll(Double accX, Double accZ) {
        return Math.atan2(-accX, accZ);
    }

    public static double calculateDeltaPitch(Double gyroX, Double dt){
        return gyroX * dt;
    }

    public static double calculateDeltaRoll(Double gyroY, Double dt){
        return gyroY * dt;
    }
}
