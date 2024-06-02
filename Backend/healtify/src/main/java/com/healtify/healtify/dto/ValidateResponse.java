package com.healtify.healtify.dto;

public class ValidateResponse {
    private boolean isValid;
    private String username;

    public ValidateResponse(boolean isValid, String username) {
        this.isValid = isValid;
        this.username = username;
    }

    // Getters and setters

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}