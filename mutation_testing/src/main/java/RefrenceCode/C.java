package RefrenceCode;

// Child.java
public class C extends P {
    @Override
    public void display() {
        System.out.println("Display from Child");
    }

    public void show(int number) {
        System.out.println("Child show: " + number);
    }
}
