package com.healtify.healtify.models;

import java.time.LocalTime;

import javax.persistence.*;

@Entity
@Table(name = "medication_intake_scheduled")
public class MedicationIntakeScheduled {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "intake_id")
    private Long intakeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @Column(name = "scheduled_intake")
    private LocalTime scheduledIntake;

    // Getters and setters
}
