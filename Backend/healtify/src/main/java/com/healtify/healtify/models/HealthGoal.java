package com.healtify.healtify.models;

import javax.persistence.*;
import java.time.LocalDate;

// TODO Dopracować tabelę by ją lepiej zsynchronizować z damymi których bedą ją dotyczyć

@Entity
@Table(name = "health_goals")
public class HealthGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "goal_id")
    private Long goalId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "goal_name")
    private String goalName;

    @Column(name = "target_value")
    private double targetValue;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "progress")
    private String progress;

    @Column(name = "notes")
    private String notes;

    // Getters and setters

    public Long getGoalId() {
        return goalId;
    }

    public void setGoalId(Long goalId) {
        this.goalId = goalId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public double getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(double targetValue) {
        this.targetValue = targetValue;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // constructors

    public HealthGoal(
        Long goalId, 
        UserAccount userAccount, 
        String goalName, 
        double targetValue, 
        LocalDate targetDate, 
        String progress, 
        String notes
    ) {
        this.goalId = goalId;
        this.userAccount = userAccount;
        this.goalName = goalName;
        this.targetValue = targetValue;
        this.targetDate = targetDate;
        this.progress = progress;
        this.notes = notes;
    }
}

