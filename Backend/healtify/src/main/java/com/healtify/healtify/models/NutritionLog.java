package com.healtify.healtify.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "nutrition_logs")
public class NutritionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount userAccount;

    @Column(name = "meal_name")
    private String mealName;

    @Column(name = "food_name")
    private String foodName;

    @Column(name = "quantity")
    private double quantity;

    @Column(name = "calories")
    private int calories;

    @Column(name = "protein")
    private double protein;

    @Column(name = "carbohydrates")
    private double carbohydrates;

    @Column(name = "fat")
    private double fat;

    @Column(name = "meal_time")
    private LocalDateTime mealTime;

    // Getters and setters

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public String getMealName() {
        return mealName;
    }

    public void setMealName(String mealName) {
        this.mealName = mealName;
    }

    public String getFoodName() {
        return foodName;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getProtein() {
        return protein;
    }

    public void setProtein(double protein) {
        this.protein = protein;
    }

    public double getCarbohydrates() {
        return carbohydrates;
    }

    public void setCarbohydrates(double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public double getFat() {
        return fat;
    }

    public void setFat(double fat) {
        this.fat = fat;
    }

    public LocalDateTime getMealTime() {
        return mealTime;
    }

    public void setMealTime(LocalDateTime mealTime) {
        this.mealTime = mealTime;
    }

    // constructors

    public NutritionLog(
        Long logId, 
        UserAccount userAccount, 
        String mealName, 
        String foodName, 
        double quantity, 
        int calories, 
        double protein, 
        double carbohydrates, 
        double fat, 
        LocalDateTime mealTime) 
        {
        this.logId = logId;
        this.userAccount = userAccount;
        this.mealName = mealName;
        this.foodName = foodName;
        this.quantity = quantity;
        this.calories = calories;
        this.protein = protein;
        this.carbohydrates = carbohydrates;
        this.fat = fat;
        this.mealTime = mealTime;
        }
}

