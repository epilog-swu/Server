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
    private static final double THRESHOLD_GSVM = 3.0;
    private static final double THRESHOLD_ANGLE_X = 60.0;
    private static final double THRESHOLD_ANGLE_Y = 60.0;
    private static final double THRESHOLD_ANGLE_Z = 60.0;
    private static final int ASVM_THRESHOLD_COUNT = 60;
    private static final int GSVM_THRESHOLD_COUNT = 20;
    private static final int BASELINE_WINDOW_SIZE = 10;
    private static final double DELTA_TIME = 0.01;

    /**
     * 낙상 감지 api
     * @param data 가속도 데이터 배열
     * @return 낙상 감지 시 true, 일상 생활 false
     */
    public boolean isFallDetected(List<SensorData> data) {
        int aSvmThresholdExceedCount = 0;
        int gSvmThresholdExceedCount = 0;
        double baseX = 0;
        double baseY = 0;
        double baseZ = 0;
        double baseGyroX = 0;
        double baseGyroY = 0;
        double baseGyroZ = 0;
        double pitch = 0.0;
        double roll = 0.0;

        // Calculate baseline for accelerometer and gyroscope
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

            log.info("A_SVM: "+aSvm+", G_SVM: "+gSvm + ", thetaY, thetaX: " + angleY + ", " + angleX);

            if (aSvm > THRESHOLD_ASVM) {
                aSvmThresholdExceedCount++;
            }
            if (gSvm > THRESHOLD_GSVM) {
                gSvmThresholdExceedCount++;
            }

            if (aSvmThresholdExceedCount > ASVM_THRESHOLD_COUNT
                    && gSvmThresholdExceedCount > GSVM_THRESHOLD_COUNT
                    && (angleY > THRESHOLD_ANGLE_Y || angleX > THRESHOLD_ANGLE_X)
            ) {
                log.info("(True result) Exceed count - ASVM: " + aSvmThresholdExceedCount + ", GSVM: " + gSvmThresholdExceedCount);
                return true;
            }

//            if (i >= BASELINE_WINDOW_SIZE + ASVM_THRESHOLD_COUNT && aSvmThresholdExceedCount > 0) {
//                aSvmThresholdExceedCount--;
//            }
//            if (i >= BASELINE_WINDOW_SIZE + GSVM_THRESHOLD_COUNT && gSvmThresholdExceedCount > 0) {
//                gSvmThresholdExceedCount--;
//            }
        }
        log.info("(False result) Exceed count - ASVM: " + aSvmThresholdExceedCount + ", GSVM: " + gSvmThresholdExceedCount);
        return false;
    }

    /**
     * 가속도 신호 SVM(sum vector magnitude) 계산
     * 기준값 대비 각 가속도 센서의 축 변화량
     * @return
     */
    private Double calculateASVM(Double x, Double y, Double z) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    /**
     * 각속도 신호 SVM 계산
     * @param P
     * @param R
     * @return
     */
    private Double calculateGSVM(Double P, Double R) {
        return Math.sqrt(Math.pow(P, 2) + Math.pow(R, 2));
    }

    /**
     * x축 각도 계산
     * @param x
     * @param y
     * @param z
     * @return
     */
    private Double calculateThetaX(Double x, Double y, Double z) {
        return Math.toDegrees(Math.acos(x / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2))));
    }

    /**
     * y축 각도 계산
     * @return 계산된 Y축 각도
     */
    private Double calculateThetaY(Double x, Double y, Double z) {
        return Math.toDegrees(Math.acos(y / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2))));
    }

    /**
     * z축 각도 계산
     * @param x
     * @param y
     * @param z
     * @return
     */
    private Double calculateThetaZ(Double x, Double y, Double z) {
        return Math.toDegrees(Math.acos(z / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2))));
    }

    /**
     * gyro sensor 이용해서 pitch 계산 (y방향)
     * @return
     */
    public static double calculatePitch(Double gyroY, Double deltaTime, Double previousPitch) {
        return previousPitch + gyroY * deltaTime;
    }

    /**
     * gyro sensor 이용해서 roll 계산 (x방향)
     * @return
     */
    public static double calculateRoll(Double gyroX, Double deltaTime, Double previousRoll) {
        return previousRoll + gyroX * deltaTime;
    }
}
