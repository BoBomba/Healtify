package com.healtify.healtify.models;

import jakarta.persistence.*;

@Entity
@Table(name = "reminder_settings")
public class ReminderSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setting_id")
    private Long settingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "reminder_type")
    private String reminderType;

    @Column(name = "active")
    private boolean active;

    // Getters and setters

    public Long getSettingId() {
        return settingId;
    }

    public void setSettingId(Long settingId) {
        this.settingId = settingId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    // constructors

    public ReminderSettings(Long settingId, UserAccount userAccount, String reminderType, boolean active) {
        this.settingId = settingId;
        this.userAccount = userAccount;
        this.reminderType = reminderType;
        this.active = active;
    }
}

