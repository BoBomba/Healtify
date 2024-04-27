package com.healtify.healtify.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "medications")
public class Medication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "medication_id")
    private Long medicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "medication_name")
    private String medicationName;

    @Column(name = "dosage_amount")
    private double dosageAmount;

    @Column(name = "dosage_unit")
    private String dosageUnit;

    @Column(name = "frequency")
    private String frequency;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    // constructors

    public Medication( 
        Long medicationId, 
        UserAccount userAccount, 
        String medicationName, 
        double dosageAmount, 
        String dosageUnit, 
        String frequency, 
        LocalDateTime startDate, 
        LocalDateTime endDate) 
        {
        this.medicationId = medicationId;
        this.userAccount = userAccount;
        this.medicationName = medicationName;
        this.dosageAmount = dosageAmount;
        this.dosageUnit = dosageUnit;
        this.frequency = frequency;
        this.startDate = startDate;
        this.endDate = endDate;
        }

    // Getters and setters

    public Long getMedicationId() {
        return medicationId;
    }

    public void setMedicationId(Long medicationId) {
        this.medicationId = medicationId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public double getDosageAmount() {
        return dosageAmount;
    }

    public void setDosageAmount(double dosageAmount) {
        this.dosageAmount = dosageAmount;
    }

    public String getDosageUnit() {
        return dosageUnit;
    }

    public void setDosageUnit(String dosageUnit) {
        this.dosageUnit = dosageUnit;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

}


