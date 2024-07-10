package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.medication.MedicationCheckList;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.MedicineResponseDto;
import com.epi.epilog.app.repository.MedicationCheckListRepository;
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
    private final MedicationCheckListRepository medicationCheckListRepository;

    @Transactional
    public CommonResponseDto.CommonResponse medicineCheck(Long id, MedicineResponseDto.MedicineChecklistUpdateDto form) {
        MedicationCheckList medicationCheckList = medicationCheckListRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        medicationCheckList.updateMedicationStatus(form.getStatus());
        medicationCheckList.updateActualTime(DateTimeConverter.convertToLocalDateTime(form.getTime()));
        medicationCheckListRepository.save(medicationCheckList);

        return CommonResponseDto.CommonResponse.builder()
                .success(true)
                .message("수정되었습니다.")
                .build();
    }
}
