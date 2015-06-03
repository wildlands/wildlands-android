package nl.wildlands.wildlandseducation.quiz;

/**
 * Created by stefan on 6/2/2015.
 */
public class QuestionImage {
    private String imagePath;
    private String imageName;
    private long questionid;
    private long id;

    public QuestionImage(long id, String imagePath, String imageName, long questionid )
    {
        this.id = id;
        this.imagePath = imagePath;
        this.imageName = imageName;
        this.questionid = questionid;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public long getQuestionid() {

        return questionid;
    }

    public void setQuestionid(long questionid) {
        this.questionid = questionid;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
