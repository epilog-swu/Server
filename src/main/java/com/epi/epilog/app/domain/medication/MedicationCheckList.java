package com.epi.epilog.app.domain.medication;

import com.epi.epilog.app.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PRIVATE;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PRIVATE)
public class MedicationCheckList extends BaseEntity {
    @Id
    @Column(name="medication_checklist_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="medication_id")
    private Medication medication;
    private String title;
    private LocalDateTime goalTime;
    private LocalDateTime actualTime;
    private Boolean isComplete;
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private MedicationStatus medicationStatus = MedicationStatus.상태없음;

    public void updateMedicationStatus(MedicationStatus status){
        this.medicationStatus = status;
        if (status == MedicationStatus.상태없음){
            this.isComplete = false;
        } else {
            this.isComplete = true;
        }
    }
    public void updateActualTime(LocalDateTime time){
        this.actualTime = time;
    }
    public void deleteMedication() {
        this.medication = null;
    }
 }
