package com.epi.epilog.app.service;

import com.epi.epilog.app.domain.log.Log;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.EmerData;
import com.epi.epilog.app.repository.LogRepository;
import com.epi.epilog.global.utils.DateTimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyService {
    private final MapService googleMapService;
    private final LogRepository logRepository;

    /**
     * WebSocket emer 이벤트 처리
     * @param form
     * @return
     * @throws Exception
     */
    public String emerEvent(EmerData form) throws Exception {
        try {
            String mapImage = googleMapService.getMapImageUrl(form.getLatitude(), form.getLongitude());
            String mapImageUrl = googleMapService.createShortURL(mapImage);
            String address = googleMapService.getAddress(form.getLatitude(), form.getLongitude());
            return " " + address + " " + mapImageUrl;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * WebSocket 낙상 시 자동 일지 기록
     * @param member
     * @param emerData
     * @throws Exception
     */
    @Transactional
    public void createLog(Member member, EmerData emerData) throws Exception {
        LocalDateTime now = LocalDateTime.now();
        String address = null;
        String mapImageUrl = null;

        try {
            address = googleMapService.getAddress(
                    Double.valueOf(emerData.getLatitude()),
                    Double.valueOf(emerData.getLongitude()));

            mapImageUrl = googleMapService.getMapImageUrl(
                    Double.valueOf(emerData.getLatitude()),
                    Double.valueOf(emerData.getLongitude()));
        } catch (Exception e) {
            log.warn("created automatic fall log" + e);
        } finally {
            Log logs = Log.builder()
                    .member(member)
                    .date(now.toLocalDate())
                    .title(now.getMinute()==0?DateTimeConverter.krShortTimeFormatter.format(now):DateTimeConverter.krTimeFormatter.format(now))
                    .occurrenceType(DateTimeConverter.convertLocalDateTimeToString(now))
                    .isFall(true)
                    .isMood(false)
                    .isExercise(false)
                    .isBloodSugar(false)
                    .isWeight(false)
                    .isBloodPressure(false)
                    .fallAddress(address)
                    .fallLongitude(emerData.getLongitude() != null ? emerData.getLongitude() : null)
                    .fallLatitude(emerData.getLatitude() != null ? emerData.getLatitude() : null)
                    .fallAddressImage(mapImageUrl)
                    .build();

            logRepository.save(logs);
        }
    }
}
