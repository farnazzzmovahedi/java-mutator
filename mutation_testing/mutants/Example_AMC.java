public class Example {

    // Public field to be mutated
    public int number;

    // Another public field to be mutated
    public String name;

    // Public constructor
    public Example(int number, String name) {
        this.number = number;
        this.name = name;
    }

    // Public method to greet
    private void greet() {
        System.out.println("Hello, " + name + "!");
    }

    // Public method to add a number
    public int addNumber(int value) {
        return this.number + value;
    }
}
