package com.healtify.healtify.dto;

public class ChangePasswordRequest {


    private String password;
    private String newPassword;

    public String getPassword() {
        return password;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setPassword(String currentPassword) {
        this.password = currentPassword;
    }


}
