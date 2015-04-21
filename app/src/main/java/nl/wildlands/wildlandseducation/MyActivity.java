package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by stefan on 3/30/2015.
 */
public class MyActivity extends Activity
{
    public static Activity instance;

    public MyActivity()
    {
        instance=this;
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

    }
}