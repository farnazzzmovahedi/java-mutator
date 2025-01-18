package org.example;

public class Course {

    private int courseId;

    private String courseName;

    private int credits;

    private Professor professor;

    public Course(int courseId, String courseName, int credits, Professor professor) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credits = credits;
        this.professor = professor;
    }

    public int getCourseId() {
        return courseId;
    }

    public String getCourseName() {
        return courseName;
    }

    public int getCredits() {
        return credits;
    }

    public Professor getProfessor() {
        return this.professor;
    }

    public void displayCourseInfo() {
        System.out.println("Course ID: " + courseId);
        System.out.println("Course Name: " + courseName);
        System.out.println("Credits: " + credits);
        System.out.println("Professor: " + professor.getName());
    }
}
