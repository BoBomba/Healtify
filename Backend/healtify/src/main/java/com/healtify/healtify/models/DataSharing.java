package com.healtify.healtify.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Powiazanie pacjent - lekarz, czyli zgoda na wglad w dane pacjenta.
 *
 * Zaproszenie moze wyjsc z obu stron (patrz {@link SharingInitiator}), ale zawsze
 * wymaga akceptacji drugiej strony - dopiero status ACCEPTED daje lekarzowi dostep
 * do pacjenta i prawo zakladania mu wizyt.
 */
@Entity
@Table(name = "data_sharing", uniqueConstraints = {
        @UniqueConstraint(name = "uq_sharing_user_doctor", columnNames = {"user_id", "doctor_id"})
})
public class DataSharing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sharing_id")
    private Long sharingId;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", nullable = false, length = 20)
    private SharingStatus requestStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "initiated_by", nullable = false, length = 20)
    private SharingInitiator initiatedBy;

    @Column(name = "request_sent_date")
    private LocalDateTime requestSentDate;

    @Column(name = "request_accepted_date")
    private LocalDateTime requestAcceptedDate;

    // Getters and setters

    public Long getSharingId() {
        return sharingId;
    }

    public void setSharingId(Long sharingId) {
        this.sharingId = sharingId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public SharingStatus getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(SharingStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public SharingInitiator getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(SharingInitiator initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public LocalDateTime getRequestSentDate() {
        return requestSentDate;
    }

    public void setRequestSentDate(LocalDateTime requestSentDate) {
        this.requestSentDate = requestSentDate;
    }

    public LocalDateTime getRequestAcceptedDate() {
        return requestAcceptedDate;
    }

    public void setRequestAcceptedDate(LocalDateTime requestAcceptedDate) {
        this.requestAcceptedDate = requestAcceptedDate;
    }

    // constructors

    public DataSharing() {
    }

    public DataSharing(UserAccount userAccount, Doctor doctor, SharingInitiator initiatedBy) {
        this.userAccount = userAccount;
        this.doctor = doctor;
        this.initiatedBy = initiatedBy;
        this.requestStatus = SharingStatus.PENDING;
        this.requestSentDate = LocalDateTime.now();
    }
}
