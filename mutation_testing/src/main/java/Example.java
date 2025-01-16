public class Example {

    public int number; // Public field to be mutated
    public String name; // Another public field to be mutated

    // Public constructor
    public Example(int number, String name) {
        this.number = number;
        this.name = name;
    }

    // Public method to greet
    public void greet() {
        System.out.println("Hello, " + name + "!");
    }

    // Public method to add a number
    public int addNumber(int value) {
        return this.number + value;
    }
}
