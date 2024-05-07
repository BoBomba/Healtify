package com.healtify.healtify.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_sharing")
public class DataSharing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sharing_id")
    private Long sharingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Column(name = "request_status")
    private String requestStatus;

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

    public Doctor getDoctorId() {
        return doctor;
    }

    public void setDoctorId(Doctor doctor) {
        this.doctor = doctor;
    }

    public String getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
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

    // TODO Sprawdzić czy konstruktor jest poprawny pod wzgledem bezpieczenstwa

    public DataSharing(
        Long sharingId, 
        UserAccount userAccount, 
        Doctor doctor, 
        String requestStatus, 
        LocalDateTime requestSentDate, 
        LocalDateTime requestAcceptedDate
    ) {
        this.sharingId = sharingId;
        this.userAccount = userAccount;
        this.doctor = doctor;
        this.requestStatus = requestStatus;
        this.requestSentDate = requestSentDate;
        this.requestAcceptedDate = requestAcceptedDate;
    }
}

