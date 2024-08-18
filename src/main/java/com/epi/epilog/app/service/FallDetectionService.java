package com.epi.epilog.app.service;

import com.epi.epilog.app.dto.SensorData;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FallDetectionService {
    private static final double THRESHOLD_ASVM = 5.5;
    private static final double THRESHOLD_GSVM = 100;
    private static final double THRESHOLD_ANGLE_X = 60.0;
    private static final double THRESHOLD_ANGLE_Y = 60.0;
    private static final double THRESHOLD_ANGLE_Z = 30.0;
    private static final int ASVM_THRESHOLD_COUNT = 60;
    private static final int GSVM_THRESHOLD_COUNT = 20;
    private static final int ANGLE_THRESHOLD_COUNT = 20;
    private static final int BASELINE_WINDOW_SIZE = 10;
    private static final double ALPHA = 0.98;
    private final RestTemplate restTemplate;
    @Value("${fall.domain}")
    private String ai_domain;

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

            if (aSvm > THRESHOLD_ASVM) {
                aSvmThresholdExceedCount++;
            }
            if (gSvm > THRESHOLD_GSVM) {
                gSvmThresholdExceedCount++;
            }
//            if (angleX > THRESHOLD_ANGLE_X) {
//                angleXExceedCount++;
//            }
//            if (angleY > THRESHOLD_ANGLE_Y) {
//                angleYExceedCount++;
//            }
//            if (angleZ > THRESHOLD_ANGLE_Z) {
//                angleZExceedCount++;
//            }

            if (aSvmThresholdExceedCount < ASVM_THRESHOLD_COUNT) {
                return false;
            }
            if (gSvmThresholdExceedCount < GSVM_THRESHOLD_COUNT) {
                return false;
            }
//                    && gSvmThresholdExceedCount > GSVM_THRESHOLD_COUNT
//                    && angleXExceedCount > ANGLE_THRESHOLD_COUNT
//                    && angleYExceedCount > ANGLE_THRESHOLD_COUNT
//                    && angleZExceedCount > ANGLE_THRESHOLD_COUNT
//            ) {
//                return true;
//            }
            return true;
        }
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

    public boolean isAIFallDetected(List<SensorData> fallData) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            FallData fallDataList = new FallData(fallData);

            String jsonString = objectMapper.writeValueAsString(fallDataList);

            String url = ai_domain + "api/ai/predict";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonString, headers);

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

            if (HttpStatus.OK != response.getStatusCode()){
                throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            String responseBody = response.getBody();
            JSONObject jsonResponse = new JSONObject(responseBody);

            return jsonResponse.getBoolean("result");
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INVALID_FORMAT_ERROR);
        }
    }

    public static class FallData {
        public List<SensorData> fall;

        public FallData(List<SensorData> fall) {
            this.fall = fall;
        }
    }
}
