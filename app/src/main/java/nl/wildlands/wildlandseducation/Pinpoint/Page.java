package nl.wildlands.wildlandseducation.Pinpoint;

import android.widget.ImageView;

/**
 * Class om een pagina content van een pinpoint te laten zien
 * Deze heeft een paginanummer
 * Een titel
 * Een centrale afbeelding
 * Een beschrijving
 */
public class Page {

    // private fields
    private long id;
    private long pinpointid;
    private int level;
    private String image;
    private String text;
    private String title;

    public Page(long id, long pinpointid, int level, String title, String image, String text)
    {
        this.id = id;
        this.pinpointid = pinpointid;
        this.level = level;
        this.image = image;
        this.text = text;
        this.title = title;
    }

// Getters en setters
    public String getText() {
        return text;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPinpointid() {
        return pinpointid;
    }

    public void setPinpointid(long pinpointid) {
        this.pinpointid = pinpointid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
