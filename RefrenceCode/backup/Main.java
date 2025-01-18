package org.example;

public class Main {
    public static void main(String[] args) {
        University university = new University("XYZ University");

        // Creating students
        Student student1 = new Student(1, "John Doe", "johndoe@example.com", "Computer Science");
        Student student2 = new Student(2, "Jane Smith", "janesmith@example.com", "Mathematics");
        Student student3 = new Student(3, "Michael Brown", "michael.brown@example.com", "Physics");

        // Creating professors
        Professor professor1 = new Professor(101, "Dr. Alice Johnson", "alice.johnson@example.com", "Computer Science");
        Professor professor2 = new Professor(102, "Dr. Bob Williams", "bob.williams@example.com", "Mathematics");
        Professor professor3 = new Professor(103, "Dr. Carla Green", "carla.green@example.com", "Physics");

        // Adding new types of people (administrative staff, researcher)
        Person staff1 = new Person(201, "Mrs. Sarah Lee", "sarah.lee@example.com");
        Person researcher1 = new Person(202, "Dr. David Turner", "david.turner@example.com");

        // Creating courses
        Course course1 = new Course(201, "Computer Science 101", 3, professor1);
        Course course2 = new Course(202, "Mathematics 101", 4, professor2);
        Course course3 = new Course(203, "Physics 101", 3, professor3);

        // Adding people and courses to the university
        university.addPerson(student1);
        university.addPerson(student2);
        university.addPerson(student3);
        university.addPerson(professor1);
        university.addPerson(professor2);
        university.addPerson(professor3);
        university.addPerson(staff1);  // Administrative staff added
        university.addPerson(researcher1);  // Researcher added

        university.addCourse(course1);
        university.addCourse(course2);
        university.addCourse(course3);

        // Displaying university information
        university.displayUniversityInfo();
    }
}
