package edu.univ.erp.domain;

public class User {
    private int userId;
    private String username;
    private String role;    
    private String status;  

    public User() {
        // no-arg constructor (needed for frameworks or when loading from DB)
    }

    public User(int userId, String username, String role, String status) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }

    public String getStatus() {
        return status;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" + "userId=" + userId + ", username='" + username + '\'' + ", role='" + role + '\'' + ", status='" + status + '\'' + '}';
    }
}
