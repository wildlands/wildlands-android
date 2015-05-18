package nl.wildlands.wildlandseducation;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;
import java.util.ArrayList;

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

    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/text.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/text.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/text.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/text.ttf");
        questions = new ArrayList<Question>();
        loaded = false;

    }

    public Socket getSocket()
    {
        return newSocket;
    }
    public void setSocketcode(int socketcode) { this.socketcode = socketcode;}
    public int getSocketcode() { return socketcode;}
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