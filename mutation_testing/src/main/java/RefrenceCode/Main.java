package RefrenceCode;

// Main.java
public class Main {
    public static void main(String[] args) {
        // PNC Example
        P parentRef = new P(); // Parent reference to a Child object
        parentRef.display(); // Polymorphic behavior

        // PMD Example
        P parentObj = new C();
        C childObj = new C();
        S siblingObj = new S();

        // PRV Example
        parentRef = siblingObj; // Reassigning Parent reference to Sibling object

        // PPD Example
        someMethod(parentObj);

        // OAC Example
        childObj.show(42); // Calling overloaded method in Child class
        parentObj.show("Hello"); // Calling method in Parent class
    }

    public static void someMethod(P parent) {
        parent.display();
    }
}