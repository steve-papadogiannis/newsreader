package gr.papadogiannis.stefanos.newsreader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;


public class WebRadioActivity extends Activity implements View.OnClickListener {

    ImageView mPlayButton;
    private static final String LEARN_STATE = "gr.papadogiannis.stefanos.newsreader.learnstate";
    private static final String PAUSE_PLAY_INTENT2 = "gr.papadogiannis.stefanos.newsreader.playpauseintent2";
    private boolean bufferBroadcastIsRegistered = false;
    private Intent learnState;
    private ProgressBar progressBar;
    private WebViewGetter webViewGetter;
    private Intent mIntent;
    private String firstHtml = "<!doctype html><html><head>";
    private String customCSS = "<style>" +
            "html {" +
            "font-family:\"Roboto\",Verdana,Geneva,Helvetica,sans-serif;" +
            "font-size:160%;" +
            "line-height:1.5em;" +
            "}" +
            "body {" +
            "letter-spacing:normal;" +
            "color:#000000;" +
            "}" +
            ".pane-heading-underline-red {" +
            "border-top: none;" +
            "border-bottom: 2px solid #BF1E2E;" +
            "}" +
            ".pane-heading-underline-gray {" +
            "border-top: 1px solid #CCCCCC;" +
            "border-bottom: 1px solid #CCCCCC;" +
            "}" +
            ".pane-title {" +
            "text-align: center;" +
            "font-family:UbuntuCondesed;" +
            "font-family:20px;" +
            "background-color: #FFF;" +
            "color: #000000;" +
            "font-weight: normal;" +
            "line-height:28px;" +
            "margin:7px 0;" +
            "padding:5px 0;" +
            "}" +
            ".field-name-field-changed {" +
            "color:grey;" +
            "text-align:center;" +
            "}" +
            ".field-name-field-title {" +
            "text-align:center;" +
            "color:grey;" +
            "}" +
            "a {" +
            "text-decoration:none;" +
            "color:grey;" +
            "}" +
            "</style>";
    private String secondHtml = "</head><body>";
    private String thirdHtml = "</body></html>";
    private ConnectivityManager connectivityManager;
    private WebView webview;

    /**
     * Called when the activity is first created. Here, we simply set the event listeners and
     * start the background service ({@link MusicService}) that will handle the actual media
     * playback.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webradio);
        mIntent = getIntent();
        learnState = new Intent(LEARN_STATE);
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        mPlayButton = (ImageView) findViewById(R.id.playbutton);
        webview = (WebView) findViewById(R.id.webview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        mPlayButton.setOnClickListener(this);
        if (connectivityManager.getActiveNetworkInfo() == null) {
            new AlertDialog.Builder(this).setMessage("Δεν είστε συνδεμένοι στο internet").setCancelable(false).setPositiveButton("Ok", null).show();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            webViewGetter = new WebViewGetter();
            webViewGetter.execute();
        }
    }

    private class WebViewGetter extends AsyncTask<Void, Void, Void> {

        private Document document;
        private Elements elements;
        private Element element, element2, element3, element4;

        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < 10 && document == null; i++) {
                try {
                    document = Jsoup.connect("yada").get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (document != null) {
                elements = document.select("div[class=pane pane-station_schedule_current_pane pane-station_schedule_current_pane]");
                if (elements.size() > 0) {
                    renderView();
                } else {
                    elements = document.select("div[class=pane pane-station_schedule_next_pane pane-station_schedule_next_pane]");
                    renderView();
                }
            }
            return null;
        }

        private void renderView() {
            if (elements.size() > 0) {
                element = elements.get(0);
                elements = element.children();
                if (elements.size() > 0) {
                    element = elements.get(0);
                    element2 = elements.get(1);
                    elements = element2.children();
                    if (elements.size() > 0) {
                        element2 = elements.get(0);
                        elements = element2.children();
                        if (elements.size() > 0) {
                            element2 = elements.get(1);
                            element3 = elements.get(2);
                            elements = element3.children();
                            if (elements.size() > 0) {
                                element4 = elements.get(0);
                                element4.attr("href", "yada" + element4.attr("href"));
                                WebRadioActivity.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        String merged = firstHtml + customCSS + secondHtml + element.toString() + element2.toString() + element3.toString() + thirdHtml;
                                        webview.loadData(merged, "text/html;lang=el;charset=UTF-8", null);
                                        progressBar.setVisibility(View.GONE);
                                        webview.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }
    }

    public void learnState() {
        sendBroadcast(learnState);
    }

    @Override
    protected void onResume() {
        if (!bufferBroadcastIsRegistered) {
            registerReceiver(broadcastReceiver2, new IntentFilter(PAUSE_PLAY_INTENT2));
            bufferBroadcastIsRegistered = true;
        }
        learnState();
        super.onResume();
    }

    @Override
    protected void onPause() {
        if (bufferBroadcastIsRegistered) {
            unregisterReceiver(broadcastReceiver2);
            bufferBroadcastIsRegistered = false;
        }
        super.onPause();
    }

    public void onClick(View target) {
        // Send the correct intent to the MusicService, according to the button that was clicked
        if (target == mPlayButton) {
            final Intent intent = new Intent(MusicService.ACTION_TOGGLE_PLAYBACK);
//            intent.putExtra("url", mIntent.getStringExtra("url"));
            intent.setPackage(this.getPackageName());
            startService(intent);
        }
    }

    private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String isPlayingExtra = intent.getStringExtra("isPlaying");
            int isPlayingInt = Integer.parseInt(isPlayingExtra);
            switch (isPlayingInt) {
                case 0:
                    mPlayButton.setImageResource(R.drawable.btn_play);
                    break;
                case 1:
                    mPlayButton.setImageResource(R.drawable.btn_pause);
                    break;
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_HEADSETHOOK:
                final Intent intent = new Intent(MusicService.ACTION_TOGGLE_PLAYBACK);
                intent.setPackage(this.getPackageName());
                startService(intent);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.webradio, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.programa:
                if (connectivityManager.getActiveNetworkInfo() == null) {
                    new AlertDialog.Builder(this).setMessage("Δεν είστε συνδεμένοι στο internet").setCancelable(false).setPositiveButton("Ok", null).show();
                } else {
                    webview.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    webViewGetter = new WebViewGetter();
                    webViewGetter.execute();
                }
                break;
        }
        return true;
    }

}