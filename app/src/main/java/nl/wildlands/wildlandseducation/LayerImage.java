package nl.wildlands.wildlandseducation;

/**
 * LayerImage object
 * Verbindt de layer met een afbeelding
 */
public class LayerImage {
    private long id;
    private int themaId;
    private String path;
    private String name;

    public LayerImage(long id, int themaId, String path, String name)
    {
        this.id = id;
        this.themaId = themaId;
        this.path = path;
        this.name = name;
    }

    // Getters en setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
