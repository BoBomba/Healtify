package com.healtify.healtify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

/**
 * Profil lekarza. Powstaje dopiero w momencie nadania konta roli ROLE_DOCTOR przez admina
 * (patrz AdminController#changeUserRole) - jedno konto = najwyzej jeden profil lekarza.
 */
@Entity
@Table(name = "doctors")
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_id")
    private Long doctorId;

    /** Konto, do ktorego nalezy profil. Nigdy nie serializujemy go w calosci - patrz DoctorDTO. */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
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

    // constructors

    // JPA wymaga konstruktora bezargumentowego - bez niego Hibernate nie potrafi
    // zmaterializowac encji i kazde zapytanie o lekarza konczy sie bledem.
    public Doctor() {
    }

    public Doctor(UserAccount userAccount, String doctorName, String specialization) {
        this.userAccount = userAccount;
        this.doctorName = doctorName;
        this.specialization = specialization;
    }

    public Doctor(Long doctorId, UserAccount userAccount, String doctorName, String specialization) {
        this.doctorId = doctorId;
        this.userAccount = userAccount;
        this.doctorName = doctorName;
        this.specialization = specialization;
    }

}
