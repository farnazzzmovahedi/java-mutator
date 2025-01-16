package RefrenceCode;

// Main.java
public class Main {
    public static void main(String[] args) {
        // PNC Example
        Parent parentRef = new Parent(); // Parent reference to a Child object
        parentRef.display(); // Polymorphic behavior

        // PMD Example
        Parent parentObj = new Child();
        Child childObj = new Child();
        Sibling siblingObj = new Sibling();

        // PRV Example
        parentRef = siblingObj; // Reassigning Parent reference to Sibling object

        // PPD Example
        someMethod(parentObj);

        // OAC Example
        childObj.show(42); // Calling overloaded method in Child class
        parentObj.show("Hello"); // Calling method in Parent class
    }

    public static void someMethod(Parent parent) {
        parent.display();
    }
}