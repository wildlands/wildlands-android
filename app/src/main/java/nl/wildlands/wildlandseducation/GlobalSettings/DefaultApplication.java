package nl.wildlands.wildlandseducation.GlobalSettings;

import android.graphics.Typeface;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;

import nl.wildlands.wildlandseducation.quiz.Question;

public final class DefaultApplication extends android.app.Application {

    // Public fields voor de hele applicatie
    // Socketverbinding
    public Socket newSocket;
    {
        try {
            newSocket = IO.socket(Values.SOCKET_URL);
        } catch (URISyntaxException e) {
        }
    }

    public static Typeface tf, tf2;                                         // Lettertypes

    public int socketcode;                                                  // Quizid

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
        tf = Typeface.createFromAsset(getAssets(),                                  // Assign lettertypes
                "fonts/text.ttf");
        tf2 = Typeface.createFromAsset(getAssets(), "fonts/thematext.ttf");

        questions = new ArrayList<Question>();
        themaScores = new HashMap<String, HashMap<Integer, Integer>>();
        loaded = false;

        allScores = new HashMap<String, String>();
    }


    // Getters en setters
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
    public void setQuestionsLoaded(boolean loaded){this.loaded = loaded;}

    public void addScore(String score, String name)
    {
        allScores.put(score,name);
    }
    public HashMap<String, String> getScores() { return allScores;}

    public int getQuizLevel() {
        return quizLevel;
    }

    public void setQuizLevel(int quizLevel) {
        this.quizLevel = quizLevel;
    }
}