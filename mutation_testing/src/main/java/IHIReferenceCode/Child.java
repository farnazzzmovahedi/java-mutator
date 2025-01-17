package IHIReferenceCode;

public class Child extends Parent {
    public String name = "Child";
    public int age = 20;

    @Override
    public void greet() {
        System.out.println("Hello from Child!");
    }

    public void displayInfo() {
        System.out.println("Child name: " + name);
        displayAge();
    }
}
