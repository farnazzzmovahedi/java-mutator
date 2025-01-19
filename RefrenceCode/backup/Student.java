package org.example;

public class Student extends Person {
    private String major;
    private int id = 10;

    public Student(int id, String name, String email, String major) {
        super(id, name, email);
        this.major = major;
    }

    public String getMajor() {
        return major;
    }

    @Override
    public void displayInfo() {
        super.displayInfo();
        System.out.println("Major: " + major);
    }
}
