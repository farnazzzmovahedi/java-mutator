package ISIRefrencedCode;

public class Dog extends Animal {
    String Sound = "Bark";
    String Walk = "Dog Walks";
    String Eat = "Dog Eats";
    String Name = "Dog";

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
    @Override
    public String Name(){
        return super.Name();
    }
}
