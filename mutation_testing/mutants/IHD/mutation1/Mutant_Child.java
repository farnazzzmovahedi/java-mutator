package IHIReferenceCode;

public class Child extends Parent {

    public String name = "Child";

    public String age2 = "sdfgh";

    @Override
    public void greet() {
        System.out.println("Hello from Child!");
    }

    public void displayInfo() {
        System.out.println("Child name: " + name);
        displayAge();
    }
}
