package com.epi.epilog.app.service;

import com.epi.epilog.app.dto.AccelerometerData;
import com.epi.epilog.app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FallDetectionService {
    private static final double THRESHOLD_SVM = 2.0;
    private static final double THRESHOLD_ANGLE = 40.0;
    private static final int SVM_THRESHOLD_COUNT = 65;
    private static final int BASELINE_WINDOW_SIZE = 10;

    /**
     * 낙상 감지 api
     * @param data 가속도 데이터 배열
     * @return 낙상 감지 시 true, 일상 생활 false
     */
    public boolean isFallDetected(List<AccelerometerData> data) {
        int svmThresholdExceedCount = 0;
        double baseX = 0;
        double baseY = 0;
        double baseZ = 0;

        for (int i = 0; i < BASELINE_WINDOW_SIZE; i++) {
            AccelerometerData entry = data.get(i);
            baseX += entry.getX();
            baseY += entry.getY();
            baseZ += entry.getZ();
        }

        baseX /= BASELINE_WINDOW_SIZE;
        baseY /= BASELINE_WINDOW_SIZE;
        baseZ /= BASELINE_WINDOW_SIZE;

        for (int i = BASELINE_WINDOW_SIZE; i < data.size(); i++) {
            AccelerometerData entry = data.get(i);
            double deltaX = entry.getX() - baseX;
            double deltaY = entry.getY() - baseY;
            double deltaZ = entry.getZ() - baseZ;

            double svm = calculateSVM(deltaX, deltaY, deltaZ);
            double angleY = calculateAngleY(deltaX, deltaY, deltaZ);

            if (svm > THRESHOLD_SVM) {
                svmThresholdExceedCount++;
            }

            if (svmThresholdExceedCount > SVM_THRESHOLD_COUNT && angleY > THRESHOLD_ANGLE) {
                return true;
            }

            if (i >= BASELINE_WINDOW_SIZE + SVM_THRESHOLD_COUNT && svmThresholdExceedCount > 0) {
                svmThresholdExceedCount--;
            }
        }

        return false;
    }

    /**
     * SVM(sum vector magnitude) 계산
     * 기준값 대비 각 가속도 센서의 축 변화량
     * @return
     */
    private double calculateSVM(Double x, Double y, Double z) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    /**
     * y축 각도 계산
     * @return 계산된 Y축 각도
     */
    private double calculateAngleY(Double x, Double y, Double z) {
        return Math.toDegrees(Math.acos(y / Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2))));
    }
}
