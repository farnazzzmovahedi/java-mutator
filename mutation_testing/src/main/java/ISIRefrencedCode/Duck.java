package ISIRefrencedCode;

public class Duck extends Animal {
    String Sound = "Quack Quack";
    String Walk = "Duck Walks";
    String Eat = "Duck Eats";
    @Override
    public String Sound(){
        return Sound;
    }
    @Override
    public String Walk(){
        return Walk;
    }
    @Override
    public String Eat(){
        return Eat;
    }
}
