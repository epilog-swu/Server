package com.epi.epilog.app.service.diabetes;

import com.epi.epilog.app.domain.Diabetes;
import com.epi.epilog.app.domain.Member;
import com.epi.epilog.app.domain.annotations.ValidOccurenceType;
import com.epi.epilog.app.domain.enums.OccurrenceType;
import com.epi.epilog.app.domain.validators.OccurenceTypeValidator;
import com.epi.epilog.app.dto.CommonResponseDto;
import com.epi.epilog.app.dto.CustomUserInfoDto;
import com.epi.epilog.app.dto.DiabetesRequestDto;
import com.epi.epilog.app.repository.DiabetesRepository;
import com.epi.epilog.app.repository.MemberRepository;
import com.epi.epilog.global.exception.ApiException;
import com.epi.epilog.global.exception.ErrorCode;
import com.epi.epilog.global.utils.DateTimeConverter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiabetesCommandService {
    private final DiabetesRepository diabetesRepository;
    private final MemberRepository memberRepository;

    public CommonResponseDto.CommonResponse createBloodSugar(
            DiabetesRequestDto.BloodSugarRequest form, CustomUserInfoDto member) {
        Member mem = memberRepository.findById(member.getId()).orElseThrow(()->
                new ApiException(ErrorCode.USER_NOT_FOUND));

        // create diabetes
        Diabetes diabet = Diabetes.builder()
                .member(mem)
                .date(form.getDate()!=null?form.getDate():LocalDate.now())
                .occurrenceType(form.getOccurrenceType())
                .title(createTitle(mem, form.getDate(), form.getOccurrenceType()))
                .bloodSugar(form.getBloodSugar())
                .build();
        diabetesRepository.save(diabet);

        // return form
        return CommonResponseDto.CommonResponse.builder()
                .message("일지가 추가됐습니다. - "+diabet.getTitle())
                .success(true)
                .build();
    }

    /**
     * 특정 날짜의 occurrenceType을 이용해서 title 생성
     * @param member
     * @param date
     * @param occurrenceType
     * @return
     */
    private String createTitle(Member member, LocalDate date, String occurrenceType) {
        Pattern TIME_PATTERN = Pattern.compile("^\\d{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01]) (0[0-9]|1[0-9]|2[0-3]):(0[1-9]|[0-5][0-9]):(0[1-9]|[0-5][0-9])$");

        if (OccurrenceType.isValid(occurrenceType)) {
            List<Diabetes> diabetes = diabetesRepository.findAllByDateAndMember(date, member);
            Map<String, Integer> titleCount = diabetes.stream()
                    .collect(Collectors.toMap(
                            Diabetes::getOccurrenceType,
                            data -> 1,
                            Integer::sum
                    ));

            titleCount.put(occurrenceType, titleCount.getOrDefault(occurrenceType, 0) + 1);
            int count = titleCount.get(occurrenceType);
            if (count == 1) {
                return occurrenceType;
            } else {
                return (occurrenceType + "(" + count + ")");
            }
        } else {
            if (TIME_PATTERN.matcher(occurrenceType).matches())
                return occurrenceType;
            else
                throw new ApiException(ErrorCode.INVALID_DATETIME_ERROR);
        }
    }
}
