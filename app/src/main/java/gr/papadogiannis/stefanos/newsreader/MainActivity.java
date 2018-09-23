package gr.papadogiannis.stefanos.newsreader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    public static final String KEY_VIDEO_LINK = "videoLink";
    private int width;
    private int height;
    private int selectedPosition=2;
    public static final String KEY_TITLE = "title", KEY_ENCLOSURE = "enclosure",
            ACTIVITY_TITLE = "activity_title", KEY_IMAGE_LINK = "imageLink", KEY_LINK = "link",
        KEY_NEWSPAPERS = "newspapers";
    private ConnectivityManager connectivityManager;
    private mainAsync main;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout linearLayout2;
    private TextView numberOfNews;
    private String[] urls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.red);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                selectItem(selectedPosition);
            }
        });
        urls = getResources().getStringArray(R.array.urls);
        linearLayout2 = (LinearLayout) findViewById(R.id.linearlayout);
        numberOfNews = (TextView) findViewById(R.id.numberOfNews);
        initialize();
        selectItem(2);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void initialize() {
        width = getWidth() - 32;
        height = (int) (Math.round(width / 1.78));
        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    private void selectItem(int position) {
        selectedPosition = position;
        if (connectivityManager.getActiveNetworkInfo() == null) {
            new AlertDialog.Builder(this).setMessage("Δεν είστε συνδεμένοι στο internet").setCancelable(false).setPositiveButton("Ok", null).show();
        } else {
            cleanAndFetch(urls[position]);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    public void getFeeds(String string) {
        main = new mainAsync();
        main.setUrl(string);
        main.execute();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.videos :
                selectItem(0);
                break;
            case R.id.newspapers :
                selectItem(1);
                break;
            case R.id.roi_eidisewn :
                selectItem(2);
                break;
            case R.id.politiki :
                selectItem(3);
                break;
            case R.id.arthra :
                selectItem(4);
                break;
            case R.id.vouli :
                selectItem(5);
                break;
            case R.id.eurovouli :
                selectItem(6);
                break;
            case R.id.ekloges :
                selectItem(8);
                break;
            case R.id.topiki_dioikisi :
                selectItem(9);
                break;
            case R.id.oikonomia :
                selectItem(10);
                break;
            case R.id.ygeia :
                selectItem(13);
                break;
            case R.id.koinwniki_asfalisi :
                selectItem(14);
                break;
            case R.id.autoapasholoumenoi :
                selectItem(15);
                break;
            case R.id.agrotes :
                selectItem(17);
                break;
            case R.id.sintaxiouhoi :
                selectItem(18);
                break;
            case R.id.neolaia_paideia :
                selectItem(19);
                break;
            case R.id.kosmos :
                selectItem(20);
                break;
            case R.id.athlitismos :
                selectItem(21);
                break;
            case R.id.politismos :
                selectItem(22);
                break;
            case R.id.kinimatografos :
                selectItem(23);
                break;
            case R.id.theatro :
                selectItem(24);
                break;
            case R.id.mousiki :
                selectItem(25);
                break;
            case R.id.eikastika :
                selectItem(26);
                break;
            case R.id.epistimi :
                selectItem(27);
                break;
            case R.id.koinwnia :
                selectItem(28);
                break;
            case R.id.mme :
                selectItem(29);
                break;
            case R.id.apopseis_sholia :
                selectItem(30);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void cleanAndFetch(String url) {
        if (main != null)
            main.cancel(true);
        numberOfNews.setText(String.format("Αριθμός Ειδήσεων: %d", 0));
        linearLayout2.removeAllViews();
        getFeeds(url);
    }

    private class mainAsync extends AsyncTask<Uri, HashMap<String, Object>, Void> {

        private String string;

        void setUrl(String string) {
            this.string = string;
        }

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Void doInBackground(Uri... uris) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = null;
            try {
                builder = documentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }
            URL url = null;
            try {
                url = new URL(this.string);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            InputStream inputStream = null;
            try {
                if (url != null) {
                    inputStream = url.openStream();
                } else {
                    Log.e("null", "url was null");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            InputSource inputSource = new InputSource(inputStream);
            Document document = null;
            try {
                if (builder != null) {
                    document = builder.parse(inputSource);
                } else {
                    Log.e("null", "builder was null");
                }
            } catch (SAXException | IOException e) {
                e.printStackTrace();
            }
            NodeList nodeList = null;
            if (document != null) {
                document.getDocumentElement().normalize();
                nodeList = document.getElementsByTagName("item");
            } else {
                Log.e("null", "document was null");
            }
            if (nodeList != null) {
                for (int i = 0; i < nodeList.getLength() && !isCancelled(); i++) {
                    Element element = (Element) nodeList.item(i);
                    RssFeedHandler rssFeedHandler = new RssFeedHandler(string);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("numberOfNewsItem", i);
                    rssFeedHandler.processFeed(element, map);
                    publishProgress(map);
                }
            } else {
                Log.e("null", "nodeList was null");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(final HashMap<String, Object>... i) {
            LinearLayout linearLayout3 = new LinearLayout(MainActivity.this);
            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams3.setMargins(0, 10, 0, 10);
            linearLayout3.setLayoutParams(layoutParams3);
            linearLayout3.setOrientation(LinearLayout.VERTICAL);
            if (i[0].containsKey(KEY_VIDEO_LINK) && !(i[0].get(KEY_VIDEO_LINK)).equals("")) {
                linearLayout3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, CustomYoutubePlayerActivity.class);
                        String[] parts = ((String) i[0].get(KEY_VIDEO_LINK)).split("embed/");
                        final String[] parts2 = parts[1].split("\\?");
                        intent.putExtra("videoLink", parts2[0]);
                        startActivity(intent);
                    }

                });
            } else {
                linearLayout3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Intent intent = new Intent(MainActivity.this, NewsItem.class);
                        if (i[0].containsKey(KEY_NEWSPAPERS)) {
                            intent.putExtra(KEY_NEWSPAPERS, "1");
                            intent.putExtra(KEY_IMAGE_LINK, (String) i[0].get(KEY_IMAGE_LINK));
                        } else {
                            String link = (String) i[0].get(KEY_LINK);
                            String imageLink = (String) i[0].get(KEY_IMAGE_LINK);
                            String title = (String) i[0].get(KEY_TITLE);
                            intent.putExtra(KEY_LINK, link);
                            intent.putExtra(KEY_IMAGE_LINK, imageLink);
                            intent.putExtra(KEY_TITLE, title);
                        }
                        startActivity(intent);

                    }
                });
                ImageView imageView = new ImageView(MainActivity.this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                layoutParams.gravity = Gravity.CENTER;
                imageView.setLayoutParams(layoutParams);
                imageView.setImageBitmap((Bitmap) i[0].get("enclosure"));
                linearLayout3.addView(imageView);
            }
            LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams2.gravity = Gravity.CENTER;
            if(!i[0].containsKey("newspapers")) {
                TextView textView = new TextView(MainActivity.this);
                textView.setLayoutParams(layoutParams2);
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(5, 5, 5, 5);
                textView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.red));
                textView.setTypeface(Typeface.DEFAULT_BOLD);
                textView.setShadowLayer(6.0f, 3.0f, 3.0f, ContextCompat.getColor(MainActivity.this, android.R.color.black));
                textView.setTextColor(ContextCompat.getColor(MainActivity.this, android.R.color.white));
                if (width > 1100) {
                    textView.setTextSize(25.0f);
                } else if (width > 900) {
                    textView.setTextSize(22.0f);
                } else if (width > 700) {
                    textView.setTextSize(19.0f);
                } else if (width > 500) {
                    textView.setTextSize(16.0f);
                } else {
                    textView.setTextSize(13.0f);
                }
                textView.setText((String) i[0].get("title"));
                linearLayout3.addView(textView);
            }
            numberOfNews.setText(String.format("Αριθμός Ειδήσεων: %d", (int) i[0].get("numberOfNewsItem") + 1));
            linearLayout2.addView(linearLayout3);
        }

        @Override
        protected void onPostExecute(Void i) {
            Toast.makeText(MainActivity.this, "Η φόρτωση ολοκληρώθηκε", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.refresh:
                selectItem(selectedPosition);
                break;
            case R.id.links:
                intent = new Intent(MainActivity.this, LinksActivity.class);
                startActivity(intent);
                break;
            case R.id.webradio2:
                intent = new Intent(MainActivity.this, Webradio2Activity.class);
                startActivity(intent);
                break;
            case R.id.webradio:
                intent = new Intent(MainActivity.this, WebRadioActivity.class);
                intent.putExtra("url", "");
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private int getWidth() {
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.widthPixels;
    }

    @Override
    protected void onDestroy() {
        final Intent intent = new Intent(MusicService.ACTION_STOP);
        intent.setPackage(this.getPackageName());
        startService(intent);
        super.onDestroy();
    }

}