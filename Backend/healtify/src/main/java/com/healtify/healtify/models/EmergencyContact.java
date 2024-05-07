package com.healtify.healtify.models;

import jakarta.persistence.*;

@Entity
@Table(name = "emergency_contacts")
public class EmergencyContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long contactId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "contact_name")
    private String contactName;

    @Column(name = "phone_number")
    private String phoneNumber;

    // Getters and setters

    public Long getContactId() {
        return contactId;
    }

    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // constructors

    public EmergencyContact(Long contactId, UserAccount userAccount, String contactName, String phoneNumber) {
        this.contactId = contactId;
        this.userAccount = userAccount;
        this.contactName = contactName;
        this.phoneNumber = phoneNumber;
    }
}
