package nl.wildlands.wildlandseducation;

/**
 * Layer object
 */
public class Layer {
    private long id;
    private int themaId;
    private String image;

    public Layer(long id, int themaId, String image)
    {
        this.id = id;
        this.themaId = themaId;
        this.image = image;
    }

    // Getters en setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getThemaId() {
        return themaId;
    }

    public void setThemaId(int themaId) {
        this.themaId = themaId;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
