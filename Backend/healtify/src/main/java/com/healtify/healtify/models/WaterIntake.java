package com.healtify.healtify.models;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "water_intake")
public class WaterIntake {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "intake_id")
    private Long intakeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "intake_date_time")
    private LocalDateTime intakeDateTime;

    @Column(name = "quantity")
    private double quantity;

    // Getters and setters

    public Long getIntakeId() {
        return intakeId;
    }

    public void setIntakeId(Long intakeId) {
        this.intakeId = intakeId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public LocalDateTime getIntakeDateTime() {
        return intakeDateTime;
    }

    public void setIntakeDateTime(LocalDateTime intakeDateTime) {
        this.intakeDateTime = intakeDateTime;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    // constructors

    public WaterIntake(
        Long intakeId, 
        UserAccount userAccount, 
        LocalDateTime intakeDateTime, 
        double quantity
    ) {
        this.intakeId = intakeId;
        this.userAccount = userAccount;
        this.intakeDateTime = intakeDateTime;
        this.quantity = quantity;
    }

}

