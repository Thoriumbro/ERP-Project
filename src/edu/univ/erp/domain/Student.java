package edu.univ.erp.domain;

public class Student extends User{
    private String rollNo;
    private String program;   
    private int year;         

    public Student() {
        // no-arg constructor
    }

    public Student(int userId, String username, String status, String rollNo, String program, int year) {
        super(userId, username, "STUDENT", status);
        this.rollNo = rollNo;
        this.program = program;
        this.year = year;
    }

    public String getRollNo() {
        return rollNo;
    }

    public String getProgram() {
        return program;
    }

    public int getYear() {
        return year;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public String toString() {
        return "Student{" + "userId=" + getUserId() + ", username='" + getUsername() + '\'' + ", rollNo='" + rollNo + '\'' + ", program='" + program + '\'' + ", year=" + year + '}';
    }
}
