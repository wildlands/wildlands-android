package nl.wildlands.wildlandseducation;

import java.util.ArrayList;

/**
 * Pinpoint heeft een id, een x en y waarde voor weergave
 * een type pinpoint en een x aantal pagina's met content
 *
 */
public class Pinpoint {
    private String name;
    private int id;
    private int xPos;
    private int yPos;
    private PinpointType type;
    private ArrayList<Page> pages;
    public Pinpoint(int id, String name, PinpointType type, int xPos, int yPos){
        this.id = id;
        this.name = name;
        this.xPos = xPos;
        this.yPos = yPos;
        this.type = type;
    }

    public int getId()
    {
        return id;
    }

    public int getXPos()
    {
        return xPos;
    }

    public int getYPos()
    {
        return yPos;
    }

    public ArrayList<Page> getPages() {
        return pages;
    }

    public PinpointType getType() {
        return type;
    }

    public void setType(PinpointType type) {
        this.type = type;
    }

    public void addPage(Page page) {
         pages.add(page);
    }
}
