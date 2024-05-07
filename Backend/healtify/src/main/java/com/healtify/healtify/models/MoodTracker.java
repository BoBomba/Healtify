package com.healtify.healtify.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "mood_tracker")
public class MoodTracker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tracker_id")
    private Long trackerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "mood_date")
    private LocalDate moodDate;

    @Column(name = "mood_rating")
    private int moodRating;

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

    public LocalDate getMoodDate() {
        return moodDate;
    }

    public void setMoodDate(LocalDate moodDate) {
        this.moodDate = moodDate;
    }

    public int getMoodRating() {
        return moodRating;
    }

    public void setMoodRating(int moodRating) {
        this.moodRating = moodRating;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    // constructors

    public MoodTracker(
        Long trackerId, 
        UserAccount userAccount, 
        LocalDate moodDate, 
        int moodRating, 
        String comments
    ) {
        this.trackerId = trackerId;
        this.userAccount = userAccount;
        this.moodDate = moodDate;
        this.moodRating = moodRating;
        this.comments = comments;
    }
}
