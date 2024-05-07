package com.healtify.healtify.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vital_signs")
public class VitalSigns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "signs_id")
    private Long signsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "measurement_date")
    private LocalDate measurementDate;

    @Column(name = "heart_rate")
    private int heartRate;

    @Column(name = "blood_pressure")
    private String bloodPressure;

    @Column(name = "temperature")
    private double temperature;

    @Column(name = "blood_sugar_level")
    private double bloodSugarLevel;

    @Column(name = "cholesterol_level")
    private double cholesterolLevel;

    @Column(name = "oxygen_saturation")
    private double oxygenSaturation;

    // Getters and setters

    public Long getSignsId() {
        return signsId;
    }

    public void setSignsId(Long signsId) {
        this.signsId = signsId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public LocalDate getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(LocalDate measurementDate) {
        this.measurementDate = measurementDate;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getBloodSugarLevel() {
        return bloodSugarLevel;
    }

    public void setBloodSugarLevel(double bloodSugarLevel) {
        this.bloodSugarLevel = bloodSugarLevel;
    }

    public double getCholesterolLevel() {
        return cholesterolLevel;
    }

    public void setCholesterolLevel(double cholesterolLevel) {
        this.cholesterolLevel = cholesterolLevel;
    }

    public double getOxygenSaturation() {
        return oxygenSaturation;
    }

    public void setOxygenSaturation(double oxygenSaturation) {
        this.oxygenSaturation = oxygenSaturation;
    }

    // constructors

    public VitalSigns(
        Long signsId, 
        UserAccount userAccount, 
        LocalDate measurementDate, 
        int heartRate, 
        String bloodPressure, 
        double temperature, 
        double bloodSugarLevel, 
        double cholesterolLevel, 
        double oxygenSaturation) 
        {
        this.signsId = signsId;
        this.userAccount = userAccount;
        this.measurementDate = measurementDate;
        this.heartRate = heartRate;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.bloodSugarLevel = bloodSugarLevel;
        this.cholesterolLevel = cholesterolLevel;
        this.oxygenSaturation = oxygenSaturation;
        }
}
