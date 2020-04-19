package com.techelevator.user.model;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;


/**
 * User
 */
public class User {
    @NotBlank(message = "Username is required")
    private String username;

//    @NotBlank(message = "Role is required")
    private String role;
    private long id;

    @NotBlank(message = "Password is required")
    @Length(max = 32, min = 8, message = "Password is required")
    private String password;
    
    @Length(max = 32, min = 8, message = "Password is required")
    private String confirmPassword;

//    private boolean passwordMatching;
    
    @NotBlank(message = "Email is required")
    private String email;
    private String confirmEmail;

//    private boolean emailMatching;

    @AssertTrue(message = "Passwords must match")
    public boolean isPasswordMatching() {
        if (password != null) {
            return password.equals(confirmPassword);
        }
        return true;
    }

    @AssertTrue(message = "Emails must match")
    public boolean isEmailMatching() {
        if (email != null) {
            return email.equals(confirmEmail);
        }
        return true;
    }
    
    public String getPassword() {
        return password;
    }
    
    public String getEmail() {
        return email;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return the id
     */
    public long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

    public void setConfirmEmail(String confirmEmail) {
        this.confirmEmail = confirmEmail;
    }
}