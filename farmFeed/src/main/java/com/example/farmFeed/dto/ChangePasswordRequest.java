package com.example.farmFeed.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * ChangePasswordRequest — used by PUT /api/auth/change-password/{id}
 *                             and PUT /api/shopkeeper/change-password/{id}
 *
 * All three fields are validated as not blank.
 * newPassword has a minimum length of 8 characters.
 * The controller checks newPassword.equals(confirmPassword) before calling service.
 */
public class ChangePasswordRequest {

    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String newPassword;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    public ChangePasswordRequest() {}

    public ChangePasswordRequest(String currentPassword, String newPassword, String confirmPassword) {
        this.currentPassword = currentPassword;
        this.newPassword     = newPassword;
        this.confirmPassword = confirmPassword;
    }

    public String getCurrentPassword()                           { return currentPassword; }
    public void setCurrentPassword(String currentPassword)       { this.currentPassword = currentPassword; }

    public String getNewPassword()                       { return newPassword; }
    public void setNewPassword(String newPassword)       { this.newPassword = newPassword; }

    public String getConfirmPassword()                           { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword)       { this.confirmPassword = confirmPassword; }
}