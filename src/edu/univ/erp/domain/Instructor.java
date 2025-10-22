package edu.univ.erp.domain;

public class Instructor extends User {
    private String department;  

    public Instructor() {
        // no-arg constructor
    }

    public Instructor(int userId, String username, String status, String department) {
        super(userId, username, "INSTRUCTOR", status);
        this.department = department;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Instructor{" + "userId=" + getUserId() + ", username='" + getUsername() + '\'' + ", department='" + department + '\'' + '}';
    }
}
