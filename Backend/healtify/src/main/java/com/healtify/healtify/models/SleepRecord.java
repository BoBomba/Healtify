package com.healtify.healtify.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.time.Duration;

@Entity
@Table(name = "sleep_records")
public class SleepRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "record_id")
    private Long recordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Transient
    private Duration duration; // Pominięte, ponieważ jest obliczane dynamicznie

    @Column(name = "quality")
    private int quality;

    @Column(name = "notes")
    private String notes;

    // Getters and setters

    public Long getRecordId() {
        return recordId;
    }

    public void setRecordId(Long recordId) {
        this.recordId = recordId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public Duration getDuration() {
        return Duration.between(startTime, endTime);
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    // constructors

    public SleepRecord(
        Long recordId, 
        UserAccount userAccount, 
        LocalDateTime startTime, 
        LocalDateTime endTime, 
        int quality, 
        String notes) 
        {
        this.recordId = recordId;
        this.userAccount = userAccount;
        this.startTime = startTime;
        this.endTime = endTime;
        this.quality = quality;
        this.notes = notes;
        }
}
