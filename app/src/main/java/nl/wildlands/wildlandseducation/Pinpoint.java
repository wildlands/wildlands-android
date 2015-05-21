package nl.wildlands.wildlandseducation;

import java.util.ArrayList;

/**
 * Pinpoint heeft een id, een x en y waarde voor weergave
 * een type pinpoint en een x aantal pagina's met content
 *
 */
public class Pinpoint {
    private String name;
    private String description;
    private long id;
    private int xPos;
    private int yPos;
    private String type;
    private ArrayList<Page> pages;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Pinpoint(long id, String name, String description, String type, int xPos, int yPos){
        this.id = id;
        this.name = name;
        this.description = description;

        this.xPos = xPos;
        this.yPos = yPos;
        this.type = type;
    }

    public long getId()
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void addPage(Page page) {
         pages.add(page);
    }
}
