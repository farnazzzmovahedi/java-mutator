package org.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UniversityTests {

    private University university;
    private Student student1;
    private Student student2;
    private Professor professor1;
    private Professor professor2;
    private Course course1;
    private Course course2;

    @BeforeEach
    void setUp() {
        university = new University("Test University");
        student1 = new Student(1, "John Doe", "john@example.com", "Computer Science");
        student2 = new Student(2, "Jane Doe", "jane@example.com", "Mathematics");
        professor1 = new Professor(101, "Dr. Alice", "alice@example.com", "Computer Science");
        professor2 = new Professor(102, "Dr. Bob", "bob@example.com", "Physics");
        course1 = new Course(201, "CS101", 3, professor1);
        course2 = new Course(202, "Physics101", 4, professor2);
    }

    @Test
    void testAddPerson() {
        university.addPerson(student1);
        List<Person> people = getPeopleFromUniversity(university);

        assertEquals(1, people.size());
        assertTrue(people.contains(student1));
    }

    @Test
    void testAddCourse() {
        university.addCourse(course1);
        List<Course> courses = getCoursesFromUniversity(university);

        assertEquals(1, courses.size());
        assertTrue(courses.contains(course1));
    }

    @Test
    void testDisplayUniversityInfo() {
        university.addPerson(student1);
        university.addCourse(course1);

        assertDoesNotThrow(university::displayUniversityInfo);
    }

    @Test
    void testStudentGetters() {
        assertEquals(1, student1.getId());
        assertEquals("John Doe", student1.getName());
        assertEquals("john@example.com", student1.getEmail());
        assertEquals("Computer Science", student1.getMajor());
    }

    @Test
    void testProfessorGetters() {
        assertEquals(101, professor1.getId());
        assertEquals("Dr. Alice", professor1.getName());
        assertEquals("alice@example.com", professor1.getEmail());
        assertEquals("Computer Science", professor1.getDepartment());
    }

    @Test
    void testCourseGetters() {
        assertEquals(201, course1.getCourseId());
        assertEquals("CS101", course1.getCourseName());
        assertEquals(3, course1.getCredits());
        assertEquals(professor1, course1.getProfessor());
    }

    @Test
    void testPersonDisplayInfo() {
        assertDoesNotThrow(student1::displayInfo);
        assertDoesNotThrow(professor1::displayInfo);
    }

    @Test
    void testCourseDisplayCourseInfo() {
        assertDoesNotThrow(course1::displayCourseInfo);
    }

    @Test
    void testAddMultiplePeople() {
        university.addPerson(student1);
        university.addPerson(student2);
        university.addPerson(professor1);
        university.addPerson(professor2);

        List<Person> people = getPeopleFromUniversity(university);

        assertEquals(4, people.size());
        assertTrue(people.contains(student1));
        assertTrue(people.contains(student2));
        assertTrue(people.contains(professor1));
        assertTrue(people.contains(professor2));
    }

    @Test
    void testAddMultipleCourses() {
        university.addCourse(course1);
        university.addCourse(course2);

        List<Course> courses = getCoursesFromUniversity(university);

        assertEquals(2, courses.size());
        assertTrue(courses.contains(course1));
        assertTrue(courses.contains(course2));
    }

    @Test
    void testEmptyUniversityInfo() {
        // Ensure the display methods handle an empty university gracefully.
        assertDoesNotThrow(university::displayUniversityInfo);
    }

    @Test
    void testNullCourseProfessor() {
        Course nullProfessorCourse = new Course(203, "Independent Study", 2, null);
        university.addCourse(nullProfessorCourse);

        List<Course> courses = getCoursesFromUniversity(university);

        assertTrue(courses.contains(nullProfessorCourse));
        assertNull(nullProfessorCourse.getProfessor());
    }

    @Test
    void testDuplicatePersonAddition() {
        university.addPerson(student1);
        university.addPerson(student1);

        List<Person> people = getPeopleFromUniversity(university);

        assertEquals(2, people.size()); // Verify duplicates are not prevented in the current implementation.
        assertSame(people.get(0), people.get(1)); // Both references point to the same object.
    }

    @Test
    void testDuplicateCourseAddition() {
        university.addCourse(course1);
        university.addCourse(course1);

        List<Course> courses = getCoursesFromUniversity(university);

        assertEquals(2, courses.size());
        assertSame(courses.get(0), courses.get(1));
    }

    @Test
    void testPersonWithoutEmail() {
        Person personWithoutEmail = new Person(303, "Unknown User", null);
        university.addPerson(personWithoutEmail);

        List<Person> people = getPeopleFromUniversity(university);

        assertTrue(people.contains(personWithoutEmail));
        assertNull(personWithoutEmail.getEmail());
    }

    @Test
    void testDisplayInfoForAllPeople() {
        university.addPerson(student1);
        university.addPerson(professor1);

        List<Person> people = getPeopleFromUniversity(university);

        for (Person person : people) {
            assertDoesNotThrow(person::displayInfo);
        }
    }

    @Test
    void testMixedPersonTypes() {
        Person researcher = new Person(301, "Dr. Researcher", "research@example.com");
        university.addPerson(student1);
        university.addPerson(professor1);
        university.addPerson(researcher);

        List<Person> people = getPeopleFromUniversity(university);

        assertEquals(3, people.size());
        assertTrue(people.contains(researcher));
        assertTrue(people.contains(student1));
        assertTrue(people.contains(professor1));
    }

    // Helper methods for reflection
    private List<Person> getPeopleFromUniversity(University university) {
        try {
            var field = University.class.getDeclaredField("people");
            field.setAccessible(true);
            return (List<Person>) field.get(university);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Unable to access private field 'people': " + e.getMessage());
            return null;
        }
    }

    private List<Course> getCoursesFromUniversity(University university) {
        try {
            var field = University.class.getDeclaredField("courses");
            field.setAccessible(true);
            return (List<Course>) field.get(university);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Unable to access private field 'courses': " + e.getMessage());
            return null;
        }
    }
}
