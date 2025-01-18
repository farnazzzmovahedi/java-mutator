package ISIRefrencedCode;

public class Duck extends Animal {

    String Sound = "Quack Quack";

    String Walk = "Duck Walks";

    String Eat = "Duck Eats";

    String Name = "Duck";

    @Override
    public String Walk() {
        return Walk;
    }

    @Override
    public String Eat() {
        return Eat;
    }

    @Override
    public String Name() {
        return super.Name();
    }

    public String Sound() {
        return super.Sound();
    }
}
