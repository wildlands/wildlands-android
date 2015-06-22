package nl.wildlands.wildlandseducation;

/**
 * Created by stefan on 6/12/2015.
 */
public class Level {
    private long id;
    private int levelId;
    private String name;

    public Level(long id, int levelId, String name)
    {
        this.id = id;
        this.levelId = levelId;
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getLevelId() {
        return levelId;
    }

    public void setLevelId(int levelId) {
        this.levelId = levelId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
