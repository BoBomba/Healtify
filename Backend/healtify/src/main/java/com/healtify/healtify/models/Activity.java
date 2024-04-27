package com.healtify.healtify.models;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.Duration;

@Entity
@Table(name = "activity")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "activity_type")
    private String activityType;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Transient
    private Duration duration; // Pominięte, ponieważ jest obliczane dynamicznie

    @Column(name = "distance")
    private double distance;

    @Column(name = "calories_burned")
    private int caloriesBurned;

    @Column(name = "heart_rate")
    private int heartRate;

    // Getters and setters

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
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

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getCaloriesBurned() {
        return caloriesBurned;
    }

    public void setCaloriesBurned(int caloriesBurned) {
        this.caloriesBurned = caloriesBurned;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    // constructors

    public Activity(
        Long activityId, 
        UserAccount userAccount, 
        String activityType, 
        LocalDateTime startTime, 
        LocalDateTime endTime, 
        double distance, 
        int caloriesBurned, 
        int heartRate
    ) {
        this.activityId = activityId;
        this.userAccount = userAccount;
        this.activityType = activityType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distance = distance;
        this.caloriesBurned = caloriesBurned;
        this.heartRate = heartRate;
    }
}

