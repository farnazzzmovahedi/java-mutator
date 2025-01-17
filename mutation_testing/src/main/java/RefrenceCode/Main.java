package RefrenceCode;

// Main.java
public class Main {
    S siblingObj;
    public static void main(String[] args) {
        // PNC Example
        P parentRef = new P(); // Parent reference to a Child object
        parentRef.display(); // Polymorphic behavior

        // PMD Example
        Main instance = new Main();
        instance.siblingObj = new S();
        P parentObj = new P();
        C childObj = new C();

        // PRV Example
        parentRef = instance.siblingObj; // Reassigning Parent reference to Sibling object

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