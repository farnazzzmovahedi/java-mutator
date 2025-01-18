import AMCReferenceCode.Example;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ExampleTest {

    @Test
    public void testFieldAccess() {
        Example example = new Example(10, "Alice");

        // Directly access public fields
        assertEquals(10, example.number);
        assertEquals("Alice", example.name);

        // Modify public fields and check the changes

        example.number = 20;
        example.name = "Bob";
        assertEquals(20, example.number);
        assertEquals("Bob", example.name);
    }

    @Test
    public void testGreetMethod() {
        Example example = new Example(10, "Alice");

        // Capture the output of the greet method
        example.greet(); // Should print "Hello, Alice!"
    }

    @Test
    public void testAddNumber() {
        Example example = new Example(10, "Alice");

        // Test the addNumber method
        assertEquals(15, example.addNumber(5));
        assertEquals(30, example.addNumber(20));
    }
}
