package org.example;
import java.util.ArrayList;
import java.util.List;

import java.util.ArrayList;
import java.util.List;

public class University {
    private static int staticId = 12;
    private String universityName;
    private List<Person> people;  // List can now store both students and professors
    private List<Course> courses;

    public University(String universityName) {
        this.universityName = universityName;
        people = new ArrayList<>();
        courses = new ArrayList<>();
    }

    public void addPerson(Person person) {
        people.add(person);
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public void displayUniversityInfo() {
        System.out.println("University: " + universityName);
        System.out.println("People:");
        for (Person person : people) {
            person.displayInfo();
        }
        System.out.println("Courses:");
        for (Course course : courses) {
            course.displayCourseInfo();
        }
    }

    public static int getStaticId() {
        return staticId;
    }
}
