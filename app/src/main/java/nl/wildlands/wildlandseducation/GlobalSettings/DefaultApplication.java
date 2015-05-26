package nl.wildlands.wildlandseducation.GlobalSettings;

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
    public int socketcode;
    public String socketnaam;
    public ArrayList<Question> questions;
    public boolean loaded;
    public int duration;
    public int level;
    public HashMap<String, HashMap<Integer, Integer>> themaScores;

    public HashMap<String, HashMap<Integer, Integer>> getThemaScores() {
        return themaScores;
    }

    public void setThemaScores(HashMap<String, HashMap<Integer, Integer>> themaScores) {
        this.themaScores = themaScores;
    }

    public void addThemaScores(String key, HashMap<Integer, Integer> values)
    {
        themaScores.put(key, values);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/text.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/text.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/text.ttf");

        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/text.ttf");
        questions = new ArrayList<Question>();
        themaScores = new HashMap<String, HashMap<Integer, Integer>>();
        loaded = false;

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
}