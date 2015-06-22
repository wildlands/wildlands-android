package nl.wildlands.wildlandseducation.Pinpoint;

import java.util.ArrayList;

/**
 * Pinpoint heeft een id, een x en y waarde voor weergave
 * een type pinpoint en een x aantal pagina's met content
 *
 */
public class Pinpoint {
    private String name;
    private String description;
    private int pinpointId;
    private long id;
    private int xPos;
    private int yPos;
    private String type;
    private ArrayList<Page> pages;



    public Pinpoint(long id,int pinpointId, String name, String description, String type, int xPos, int yPos){
        this.id = id;
        this.pinpointId = pinpointId;
        this.name = name;
        this.description = description;

        this.xPos = xPos;
        this.yPos = yPos;
        this.type = type;
    }

    // Getters en setters
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public int getPinpointId() {
        return pinpointId;
    }

    public void setPinpointId(int pinpointId) {
        this.pinpointId = pinpointId;
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
