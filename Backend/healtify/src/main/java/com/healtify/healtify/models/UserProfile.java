package com.healtify.healtify.models;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Period;

/**
 * Szczegolowe dane pacjenta.
 * Istnienie wiersza oznacza, ze uzytkownik przeszedl juz przez formularz uzupelniania danych; 
 * puste pola dozwolone
 */
@Entity
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "profile_id")
    private Long profileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "full_name")
    private String fullName;

    /**
     * Trzymamy date urodzenia, a nie wiek - wiek liczony przy odczycie. 
     * Stara kolumna "age" w bazie nieuzywana (ddl-auto=update nie kasuje kolumn).
     */
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "gender")
    private String gender;

    @Column(name = "phone")
    private String phone;

    @Column(name = "height_cm")
    private Integer heightCm;

    /** Waga w kg z dokladnoscia do 0,1 - BigDecimal, a nie double. */
    @Column(name = "weight_kg", precision = 5, scale = 1)
    private BigDecimal weightKg;

    @Column(name = "blood_type")
    private String bloodType;

    @Column(name = "allergies", columnDefinition = "TEXT")
    private String allergies;

    @Column(name = "chronic_diseases", columnDefinition = "TEXT")
    private String chronicDiseases;

    @Column(name = "medications", columnDefinition = "TEXT")
    private String medications;

    public UserProfile() {

    }

    // Pola wyliczane - nie siedza w bazie.

    /** Wiek w pelnych latach albo null, gdy nie podano daty urodzenia. */
    @Transient
    public Integer getAge() {
        if (dateOfBirth == null) {
            return null;
        }
        return Period.between(dateOfBirth, LocalDate.now()).getYears();
    }

    /** BMI z dokladnoscia do 0,1 albo null, gdy brakuje wzrostu lub wagi. */
    @Transient
    public BigDecimal getBmi() {
        if (heightCm == null || heightCm <= 0 || weightKg == null || weightKg.signum() <= 0) {
            return null;
        }
        BigDecimal heightInMeters = BigDecimal.valueOf(heightCm).movePointLeft(2);
        return weightKg.divide(heightInMeters.multiply(heightInMeters), 1, RoundingMode.HALF_UP);
    }

    // Getters and setters

    public Long getProfileId() {
        return profileId;
    }

    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(Integer heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getAllergies() {
        return allergies;
    }

    public void setAllergies(String allergies) {
        this.allergies = allergies;
    }

    public String getChronicDiseases() {
        return chronicDiseases;
    }

    public void setChronicDiseases(String chronicDiseases) {
        this.chronicDiseases = chronicDiseases;
    }

    public String getMedications() {
        return medications;
    }

    public void setMedications(String medications) {
        this.medications = medications;
    }
}
