package com.example.photometal1.Models;

import java.time.LocalDate;

public class UserSettings {

    private int userId;          // id από τον πίνακα users
    private String email;
    private String fullName;     // από τον πίνακα PHOTOGRAPHER
    private LocalDate hireDate;  // από τον πίνακα PHOTOGRAPHER

    public UserSettings() {}

    public UserSettings(int userId, String email,
                        String fullName, LocalDate hireDate) {
        this.userId = userId;
        this.email = email;
        this.fullName = fullName;
        this.hireDate = hireDate;
    }

    // --- getters / setters ---

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }
}
