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

    /** Tytul zawodowy, np. "lek." albo "dr n. med.". */
    @Column(name = "title")
    private String title;

    /** Numer prawa wykonywania zawodu. */
    @Column(name = "license_number")
    private String licenseNumber;

    @Column(name = "workplace")
    private String workplace;

    @Column(name = "work_address")
    private String workAddress;

    @Column(name = "phone")
    private String phone;

    /**
     * Czy lekarz przeszedl juz przez formularz uzupelniania danych.
     *
     * U pacjenta wystarcza samo istnienie wiersza w user_profile,
     * profil lekarza zaklada admin przy nadaniu roli (AdminController#grantDoctor), 
     * wiec wiersz istnieje, zanim lekarz cokolwiek zobaczy - stad ta flaga.
     *
     * Typ obiektowy, nie boolean: ddl-auto=update dokłada kolumne jako NULL-owalna,
     * a wiersze lekarzy sprzed tej zmiany maja w niej null.
     */
    @Column(name = "profile_completed")
    private Boolean profileCompleted;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public String getWorkplace() {
        return workplace;
    }

    public void setWorkplace(String workplace) {
        this.workplace = workplace;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    /** Null (profil zalozony przez admina, lekarz go jeszcze nie widzial) liczy sie jako "nie". */
    public boolean isProfileCompleted() {
        return profileCompleted != null && profileCompleted;
    }

    public void setProfileCompleted(boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    // constructors

    // JPA wymaga konstruktora bezargumentowego - bez niego Hibernate kazde zapytanie o lekarza konczy sie bledem.
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
