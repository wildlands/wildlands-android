package nl.wildlands.wildlandseducation.Pinpoint;

/**
 * Class om een afbeelding voor een pagina bij te houden
 * Wordt als sqlite object opgeslagen
 */
public class PageImage {
    private long id;
    private long pageId;
    private String imagePath;
    private String name;

    public PageImage(long id, long pageId, String imagePath, String name)
    {
        this.id = id;
        this.pageId = pageId;
        this.imagePath = imagePath;
        this.name = name;
    }

// Getters en setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPageId() {
        return pageId;
    }

    public void setPageId(long pageId) {
        this.pageId = pageId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
