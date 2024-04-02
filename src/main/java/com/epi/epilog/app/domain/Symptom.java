package com.epi.epilog.app.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class Symptom {
    @Id
    @Column(name="symptom_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="seizure_id")
    private Seizure seizure;
    private String part;
    @ElementCollection
    @CollectionTable(name="SymptomOptions", joinColumns = @JoinColumn(name="symptom_id"))
    private List<String> options = new ArrayList<>();
}
