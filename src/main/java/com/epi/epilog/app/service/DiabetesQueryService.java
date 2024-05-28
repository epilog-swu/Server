package com.epi.epilog.app.service;

import com.epi.epilog.app.repository.DiabetesRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DiabetesQueryService {
    private final DiabetesRepository diabetesRepository;
}