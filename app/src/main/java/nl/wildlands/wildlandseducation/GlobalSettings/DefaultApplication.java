package nl.wildlands.wildlandseducation.GlobalSettings;

import android.graphics.Typeface;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import nl.wildlands.wildlandseducation.quiz.Question;

public final class DefaultApplication extends android.app.Application {
    public Socket newSocket;
    {
        try {
            newSocket = IO.socket("http://doornbosagrait.tk:2345");
        } catch (URISyntaxException e) {
        }
    }

    public static Typeface tf, tf2;


    public int socketcode;

    public int getQuizLevel() {
        return quizLevel;
    }

    public void setQuizLevel(int quizLevel) {
        this.quizLevel = quizLevel;
    }

    public String socketnaam;
    public ArrayList<Question> questions;
    public boolean loaded;
    public int duration;
    public int level;
    public int quizLevel;

    public boolean homeFinished;
    public HashMap<String, HashMap<Integer, Integer>> themaScores;

    public HashMap<String, HashMap<Integer, Integer>> getThemaScores() {
        return themaScores;
    }

    public HashMap<String, String> allScores;
    public void setThemaScores(HashMap<String, HashMap<Integer, Integer>> themaScores) {
        this.themaScores = themaScores;
    }

    public void addThemaScores(String key, HashMap<Integer, Integer> values)
    {
        themaScores.put(key, values);
    }

    public boolean isHomeFinished() {
        return homeFinished;
    }

    public void setHomeFinished(boolean homeFinished) {
        this.homeFinished = homeFinished;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        tf = Typeface.createFromAsset(getAssets(),
                "fonts/text.ttf");
        tf2 = Typeface.createFromAsset(getAssets(), "fonts/thematext.ttf");
       FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/text.ttf");
       FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/text.ttf");
       FontsOverride.setDefaultFont(this, "SERIF", "fonts/text.ttf");
       FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/text.ttf");
        questions = new ArrayList<Question>();
        themaScores = new HashMap<String, HashMap<Integer, Integer>>();
        loaded = false;

        allScores = new HashMap<String, String>();
        String customFont = "text.ttf";


    }

    public Socket getSocket()
    {
        return newSocket;
    }
    public void setSocketcode(int socketcode) { this.socketcode = socketcode;}
    public int getSocketcode() { return socketcode;}

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setSocketnaam(String socketnaam){this.socketnaam = socketnaam;}
    public String getSocketnaam() {return socketnaam;}
    public boolean isQuestionsLoaded(){return loaded;}
    public void setQuestionsLoaded(boolean loaded){this.loaded = loaded;}

    public void addQuestion(Question q)
    {

        questions.add(q);
    }

    public ArrayList<Question> getQuestions()
    {
        return questions;
    }

    public void addScore(String score, String name)
    {
        allScores.put(score,name);
    }
    public HashMap<String, String> getScores() { return allScores;}
}