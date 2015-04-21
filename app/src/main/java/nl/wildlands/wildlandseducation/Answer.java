package nl.wildlands.wildlandseducation;

/**
 * Write a description of class Answer here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Answer
{
    private String answer;
    private boolean good;
    /**
     * Constructor for objects of class Answer
     */
    public Answer(String answer, boolean good)
    {
        this.answer = answer;
        this.good = good;
    }

    public String getAnswer()
    {
        return answer;
    }
    
    public boolean isGood()
    {
        return good;
    }
}
