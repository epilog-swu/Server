package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.medication.MedicationCheckList;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.MedicationResponseDto;
import com.epi.epilog.app.repository.MedicationCheckListRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import com.epi.epilog.global.utils.DateTimeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicineCommandService {
    private final MemberRepository memberRepository;
    private final MedicationCheckListRepository medicationCheckListRepository;

    /**
     * 복약 체크리스트 상태 수정
     * @param id 복약 체크리스트 ID
     * @param form 수정 폼
     * @param userInfo 사용자 정보
     * @return
     */
    @Transactional
    public CommonResponseDto.CommonResponse medicineCheck(Long id, MedicationResponseDto.MedicineChecklistUpdateDto form, CustomUserDetails userInfo) {
        Member member = memberRepository.findById(userInfo.getMember().getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        MedicationCheckList medicationCheckList = medicationCheckListRepository.findById(id)
                .orElseThrow(() -> new ApiException(ErrorCode.NOT_FOUND));

        if (medicationCheckList.getMedication().getMember() != member) {
            throw new ApiException(ErrorCode.UNAUTHORIZED);
        }

        medicationCheckList.updateMedicationStatus(form.getStatus());
        medicationCheckList.updateActualTime(DateTimeConverter.convertToLocalDateTime(form.getTime()));
        medicationCheckListRepository.save(medicationCheckList);

        return CommonResponseDto.CommonResponse.builder()
                .success(true)
                .message("수정되었습니다.")
                .build();
    }
}
