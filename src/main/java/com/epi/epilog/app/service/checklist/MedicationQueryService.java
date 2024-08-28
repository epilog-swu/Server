package com.epi.epilog.app.service.checklist;

import com.epi.epilog.app.domain.medication.Medication;
import com.epi.epilog.app.domain.member.Member;
import com.epi.epilog.app.dto.MedicationResponseDto;
import com.epi.epilog.app.repository.MedicationRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicationQueryService {
    private final MedicationRepository medicationRepository;
    private final MemberRepository memberRepository;

    /**
     * 복용약 상세정보 조회
     * @param medicationId
     * @param userInfo
     * @return
     */
    public MedicationResponseDto.GetMedicationForm getMedicationDetails(Long medicationId, CustomUserDetails userInfo) {

        Member member = memberRepository.findById(userInfo.getMember().getId())
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));

        List<Medication> medicationList = medicationRepository.findAllByMemberOrderByCreatedAt(member);

        Integer index = 0;
        Boolean check = false;

        for (Medication medication : medicationList) {
            if (medication.getId() == medicationId) {
                check = true;
                break;
            }
            index++;
        }

        if (medicationList.isEmpty() || check == false) {
            throw new ApiException(ErrorCode.MEDICATION_NOT_FOUND);
        }

        return MedicationResponseDto.GetMedicationForm
                .builder()
                .id(medicationList.get(index).getId())
                .nextId(index < medicationList.size() - 1 ? medicationList.get(index + 1).getId() : null)
                .prevId(index > 0 ? medicationList.get(index - 1).getId() : null)
                .medicationName(medicationList.get(index).getMedicationName())
                .times(medicationList.get(index).getTimes())
                .isAlarm(medicationList.get(index).getIsAlarm())
                .startDate(medicationList.get(index).getStartDate())
                .endDate(medicationList.get(index).getEndDate() != null? medicationList.get(index).getEndDate() : null)
                .weeks(medicationList.get(index).getWeeks().stream().map(weekType -> weekType.toString()).collect(Collectors.toList()))
                .effectiveness(medicationList.get(index).getEffectiveness())
                .precautions(medicationList.get(index).getPrecautions())
                .storageMethod(medicationList.get(index).getStorageMethod())
                .build();
    }
}
