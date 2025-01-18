public class Child extends Parent {

    @Override
    public void displayInfo() {
        System.out.println("This is the Child class.");
    }

    public void childSpecificMethod() {
        System.out.println("This method is specific to the Child class.");
    }
}