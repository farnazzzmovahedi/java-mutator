public class Child extends Parent {

    public String name = "Child";

    @Override
    public void greet() {
        System.out.println("Hello from Child!");
    }

    public void displayInfo() {
        System.out.println("Child name: " + name);
        displayAge();
    }
}