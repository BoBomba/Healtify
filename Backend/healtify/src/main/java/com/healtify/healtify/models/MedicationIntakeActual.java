package com.healtify.healtify.models;

import java.time.LocalDateTime;

import javax.persistence.*;

@Entity
@Table(name = "medication_intake_actual")
public class MedicationIntakeActual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "intake_id")
    private Long intakeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @Column(name = "actual_intake_date_time")
    private LocalDateTime actualIntakeDateTime;

    @Column(name = "missed")
    private boolean missed;

    // Getters and setters

    public Long getIntakeId() {
        return intakeId;
    }

    public void setIntakeId(Long intakeId) {
        this.intakeId = intakeId;
    }

    public Medication getMedication() {
        return medication;
    }

    public void setMedication(Medication medication) {
        this.medication = medication;
    }

    public LocalDateTime getActualIntakeDateTime() {
        return actualIntakeDateTime;
    }

    public void setActualIntakeDateTime(LocalDateTime actualIntakeDateTime) {
        this.actualIntakeDateTime = actualIntakeDateTime;
    }

    public boolean isMissed() {
        return missed;
    }

    public void setMissed(boolean missed) {
        this.missed = missed;
    }

    // constructors

    public MedicationIntakeActual(
        Long intakeId, 
        Medication medication, 
        LocalDateTime actualIntakeDateTime, 
        boolean missed) 
        {
        this.intakeId = intakeId;
        this.medication = medication;
        this.actualIntakeDateTime = actualIntakeDateTime;
        this.missed = missed;
        }

}
