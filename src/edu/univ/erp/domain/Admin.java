package edu.univ.erp.domain;

public class Admin extends User {

    public Admin() {
    }

    public Admin(int userId, String username, String status) {
        super(userId, username, "ADMIN", status);
    }

    @Override
    public String toString() {
        return "Admin{" + "userId=" + getUserId() + ", username='" + getUsername() + '\'' + ", role='" + getRole() + '\'' + ", status='" + getStatus() + '\'' + '}';
    }
}
