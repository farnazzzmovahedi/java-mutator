public class Child extends Parent {

    @Override
    public void greet() {
        System.out.println("Hello from Child!");
    }

    public void childSpecificMethod() {
        System.out.println("This method is specific to the Child class.");
    }
}