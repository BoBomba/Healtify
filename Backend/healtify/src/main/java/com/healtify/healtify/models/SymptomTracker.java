package com.healtify.healtify.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "symptom_tracker")
public class SymptomTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tracker_id")
    private Long trackerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "entry_date")
    private LocalDate entryDate;

    @Column(name = "symptom_name")
    private String symptomName;

    @Column(name = "comments")
    private String comments;

    // Getters and setters

    public Long getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(Long trackerId) {
        this.trackerId = trackerId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public LocalDate getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDate entryDate) {
        this.entryDate = entryDate;
    }

    public String getSymptomName() {
        return symptomName;
    }

    public void setSymptomName(String symptomName) {
        this.symptomName = symptomName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    // constructors

    public SymptomTracker(
        Long trackerId, 
        UserAccount userAccount, 
        LocalDate entryDate, 
        String symptomName, 
        String comments
    ) {
        this.trackerId = trackerId;
        this.userAccount = userAccount;
        this.entryDate = entryDate;
        this.symptomName = symptomName;
        this.comments = comments;
    }
}

