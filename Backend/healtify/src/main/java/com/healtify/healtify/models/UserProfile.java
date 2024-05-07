package com.healtify.healtify.models;

import jakarta.persistence.*;
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

    @Column(name = "age")
    private int age;

    @Column(name = "gender")
    private String gender;

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    // constructors

    public UserProfile(
        Long profileId, 
        UserAccount userAccount, 
        String fullName, 
        int age,
        String gender
    ) {
        this.profileId = profileId;
        this.userAccount = userAccount;
        this.fullName = fullName;
        this.age = age;
        this.gender = gender;
    }
}

