package com.healtify.healtify.models;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "medical_history")
public class MedicalHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "condition_name")
    private String condition;

    @Column(name = "diagnosis_date")
    private LocalDate diagnosisDate;

    @Column(name = "treatment")
    private String treatment;

    @Column(name = "notes")
    private String notes;

    // Getters and setters

    public Long getHistoryId() {
        return historyId;
    }

    public void setHistoryId(Long historyId) {
        this.historyId = historyId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // constructors

    public MedicalHistory(
        Long historyId, 
        UserAccount userAccount, 
        String condition, 
        LocalDate diagnosisDate, 
        String treatment, 
        String notes
    ) {
        this.historyId = historyId;
        this.userAccount = userAccount;
        this.condition = condition;
        this.diagnosisDate = diagnosisDate;
        this.treatment = treatment;
        this.notes = notes;
    }
}
