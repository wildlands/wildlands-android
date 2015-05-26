package nl.wildlands.wildlandseducation;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import java.util.ArrayList;

import nl.wildlands.wildlandseducation.Pinpoint.Page;
import nl.wildlands.wildlandseducation.SQLite.PinpointsDataSource;


public class PageDisplay extends Activity {

    private PinpointsDataSource pinpointsDataSource;
    private ArrayList<Page> allPages;
    private ArrayList<Page> pages;
    WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_display);
        webView = (WebView)findViewById(R.id.webview);

        pinpointsDataSource = new PinpointsDataSource(this.getApplicationContext());
        pinpointsDataSource.open();
        pages = new ArrayList<Page>();
        allPages = pinpointsDataSource.getAllPages();

        long id = -1;
        Bundle button =getIntent().getExtras();
        if(button!=null)
        {
            id =button.getLong("BUTTON");
            Log.d("id", String.valueOf(id));
        }
        for(Page page: allPages)
        {
            if(page.getId() == id)
            {
                pages.add(page);
            }
        }
        String summary = pages.get(0).getText();
        webView.loadData(summary,"text/html",null);

    }



}
