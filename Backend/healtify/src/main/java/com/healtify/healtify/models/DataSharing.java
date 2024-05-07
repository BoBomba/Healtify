package com.healtify.healtify.models;

import jakarta.persistence.*;
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

    @Column(name = "recipient_id")
    private Long recipientId;

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

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
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
        Long recipientId, 
        String requestStatus, 
        LocalDateTime requestSentDate, 
        LocalDateTime requestAcceptedDate
    ) {
        this.sharingId = sharingId;
        this.userAccount = userAccount;
        this.recipientId = recipientId;
        this.requestStatus = requestStatus;
        this.requestSentDate = requestSentDate;
        this.requestAcceptedDate = requestAcceptedDate;
    }
}
