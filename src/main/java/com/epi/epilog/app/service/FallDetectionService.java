package com.epi.epilog.app.service;

import com.epi.epilog.app.dto.AccelerometerData;
import com.epi.epilog.app.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FallDetectionService {
    private final MemberRepository memberRepository;
    private static final double THRESHOLD_SVM = 2.5;
    private static final double THRESHOLD_ANGLE = 50.0;
    private static final int SVM_THRESHOLD_COUNT = 70;
    private static final int SVM_TIME_WINDOW = 100;

    /**
     * 낙상 감지 api
     * @param data 가속도 데이터 배열
     * @return 낙상 감지 시 true, 일상 생활 false
     */
    public boolean isFallDetected(List<AccelerometerData> data) {
        int svmThresholdExceedCount = 0;

        for (int i = 0; i < data.size(); i++) {
            AccelerometerData entry = data.get(i);
            double svm = calculateSVM(entry);
            double angleY = calculateAngleY(entry);

            // SVM 값이 임계값을 초과하면 카운트 증가
            if (svm > THRESHOLD_SVM) {
                svmThresholdExceedCount++;
            }

            // SVM 초과 횟수가 임계값을 넘고 Y축 각도도 임계값을 넘으면 낙상으로 간주
            if (svmThresholdExceedCount > SVM_THRESHOLD_COUNT && angleY > THRESHOLD_ANGLE) {
                return true;
            }

            // 시간 윈도우 내에서 SVM 초과 횟수를 조정
            // 윈도우 크기를 초과하면 초과 횟수를 감소시켜 윈도우 내에서만 계산
            if (i >= SVM_TIME_WINDOW && svmThresholdExceedCount > 0) {
                svmThresholdExceedCount--;
            }
        }

        return false;
    }

    /**
     * SVM(sum vector magnitude) 계산
     * @param data 가속도 데이터
     * @return
     */
    private double calculateSVM(AccelerometerData data) {
        return Math.sqrt(Math.pow(data.getX(), 2) + Math.pow(data.getY(), 2) + Math.pow(data.getZ(), 2));
    }

    /**
     * y축 각도 계산
     * @param data 가속도 데이터 항목
     * @return 계산된 Y축 각도
     */
    private double calculateAngleY(AccelerometerData data) {
        return Math.toDegrees(Math.acos(data.getY() / Math.sqrt(Math.pow(data.getX(), 2) + Math.pow(data.getY(), 2) + Math.pow(data.getZ(), 2))));
    }
}
