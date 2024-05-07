package com.healtify.healtify.models;

import javax.persistence.*;

@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long doctorId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "doctor_name")
    private String doctorName;

    @Column(name = "specialization")
    private String specialization;

    // Getters and setters

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public String getDoctorName() {
        return doctorName;
    }

    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    // constructor

    public Doctor(
            Long doctorId,
            String doctorName,
            String specialization
    ) {
        this.doctorId = doctorId;
        this.doctorName = doctorName;
        this.specialization = specialization;
    }

}
