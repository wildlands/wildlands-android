package nl.wildlands.wildlandseducation.quiz;

import android.util.Log;

import java.util.ArrayList;

import nl.wildlands.wildlandseducation.quiz.Answer;

/**
 * Question is een vraag object
 *

 * @author (your name) 
 * @version (a version number or a date)
 */
public class Question
{
    private long id;
    private ArrayList<Answer> answers;
    private String question;
    private String image;
    private String imagePath;
    private int level;
    private String type;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    /**
     * Constructor for objects of class Question
     */
    public Question(long id, String question, String image, int level, String type)
    {
        this.id = id;
        answers = new ArrayList<Answer>();
        this.question = question;
        imagePath = "";
        this.image = image;
        this.level = level;
        this.type = type;


    }

   public void addAnswer(long id, long vraagid, String answer, boolean good)
    {
        answers.add(new Answer(id, vraagid, answer, good));
    }
    
    public String getQuestion()
    {
        return question;
    }
    
    public ArrayList<Answer> getAnswers()
    {
        return answers;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCorrectAnswer()
    {

        String answer = "";
        for(Answer selectedAnswer : answers)
        {
            Log.d("GetCorrect", selectedAnswer.getAnswer() + selectedAnswer.isGood());

            if(selectedAnswer.isGood())
            {
                answer = selectedAnswer.getAnswer();

            }
        }
        return answer;
    }



    public String getImagePath()
    {
        return imagePath;
    }
    public void setImagePath(String imagePath){
        this.imagePath = imagePath;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
