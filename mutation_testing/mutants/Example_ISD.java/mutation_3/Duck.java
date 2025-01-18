package ISDRefrencedCode;

public class Duck extends Animal {

    String Sound = "Quack Quack";

    String Walk = "Duck Walks";

    String Eat = "Duck Eats";

    String Name = "Duck";

    @Override
    public String Sound() {
        return super.Sound();
    }

    @Override
    public String Walk() {
        return super.Walk();
    }

    @Override
    public String Eat() {
        return Eat();
    }

    @Override
    public String Name() {
        return Name;
    }
}
