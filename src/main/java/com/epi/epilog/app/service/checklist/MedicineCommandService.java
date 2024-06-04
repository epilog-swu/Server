package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.Medicine;
import com.epi.epilog.app.domain.MedicineLog;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.MedicineResponseDto;
import com.epi.epilog.app.repository.MedicineLogRepository;
import com.epi.epilog.app.repository.MedicineRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.DateTimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineCommandService {
    private final MedicineLogRepository medicineLogRepository;

    @Transactional
    public CommonResponseDto.CommonResponse medicineCheck(Long id, MedicineResponseDto.MedicineChecklistUpdateDto form) {
        MedicineLog medicineLog = medicineLogRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        medicineLog.updateMedicationStatus(form.getStatus());
        medicineLog.updateActualTime(DateTimeConverter.convertToLocalDateTime(form.getTime()));
        medicineLogRepository.save(medicineLog);

        return CommonResponseDto.CommonResponse.builder()
                .success(true)
                .message("수정되었습니다.")
                .build();
    }
}
