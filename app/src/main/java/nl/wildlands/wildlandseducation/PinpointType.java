package nl.wildlands.wildlandseducation;

/**
 * Pinpointytpe is de class om het soort pinpoint weer te geven
 * Dit aan de hand van de naam, eenheid en de afbeelding vd pinpoint
 */
public class PinpointType {
    private int id;
    private String name;
    private String unit;
    private String image;

    public PinpointType(int id, String name, String unit, String image)
    {
        this.id = id;
        this.name = name;
        this.unit = unit;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
