package nl.wildlands.wildlandseducation;

public final class Application extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/text.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/text.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/text.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/text.ttf");
    }
}