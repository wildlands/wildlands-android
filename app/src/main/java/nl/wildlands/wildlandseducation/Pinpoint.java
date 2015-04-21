package nl.wildlands.wildlandseducation;

/**
 * Created by stefan on 3/24/2015.
 */
public class Pinpoint {
    private int id;
    private String name;
    private int xPos;
    private int yPos;
    public Pinpoint(int id, String name, int xPos, int yPos){
        this.id = id;
        this.name = name;
        this.xPos = xPos;
        this.yPos = yPos;
    }

    public int getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public int getXPos()
    {
        return xPos;
    }

    public int getYPos()
    {
        return yPos;
    }
}
