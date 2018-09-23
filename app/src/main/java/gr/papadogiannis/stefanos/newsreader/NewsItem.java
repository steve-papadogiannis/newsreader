package gr.papadogiannis.stefanos.newsreader;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
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
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class NewsItem extends AppCompatActivity {

    private String title;
    private String imageLink;
    private TextView textView0,textView1,textView2,textView3;
    private String link;
    private ImageView imageView1;
    private Bitmap image;
    private LinearLayout linearLayout1;
    private boolean isNewspaper = false;
    private ZoomableImageView zoomableImageView;
    private FloatingActionButton button1, button2;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        isNewspaper = intent.getStringExtra(MainActivity.KEY_NEWSPAPERS) != null &&
                intent.getStringExtra(MainActivity.KEY_NEWSPAPERS).equals("1");
        imageLink=intent.getStringExtra(MainActivity.KEY_IMAGE_LINK);
        if (!isNewspaper) {
            setContentView(R.layout.news_item);
            linearLayout1=(LinearLayout)findViewById(R.id.linearLayout);
            title=intent.getStringExtra(MainActivity.KEY_TITLE);
            link=intent.getStringExtra(MainActivity.KEY_LINK);
            imageView1=(ImageView)findViewById(R.id.txtImage);
            image=intent.getParcelableExtra(MainActivity.KEY_ENCLOSURE);
            textView0=(TextView)findViewById(R.id.txtSupertitle);
            textView1=(TextView)findViewById(R.id.txtTitle);
            textView2=(TextView)findViewById(R.id.txtDate);
            textView3=(TextView)findViewById(R.id.txtSubtitle);
            swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe);
            swipeRefreshLayout.setColorSchemeResources(R.color.red);
            swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.image_loading);
                    progressBar.setVisibility(View.VISIBLE);
                    imageView1.setVisibility(View.GONE);
                    linearLayout1.removeAllViews();
                    ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.content_loading);
                    progressBar1.setVisibility(View.VISIBLE);
                    NewsItemGetter newsItemGetter=new NewsItemGetter();
                    newsItemGetter.setFeed(link);
                    newsItemGetter.execute();
                    refreshListView();
                    ImageGetter imageGetter=new ImageGetter();
                    imageGetter.execute();
                }
            });
            NewsItemGetter newsItemGetter = new NewsItemGetter();
            newsItemGetter.setFeed(link);
            newsItemGetter.execute();
            refreshListView();
        } else {
            setContentView(R.layout.newspaper_item);
            linearLayout1=(LinearLayout)findViewById(R.id.linearLayout);
            zoomableImageView=(ZoomableImageView)findViewById(R.id.txtImage);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ImageGetter imageGetter=new ImageGetter();
        imageGetter.execute();
    }

    protected void refreshListView(){
        textView1.setText(title);
    }

    private class NewsItemGetter extends AsyncTask<Uri, HashMap<String, String>, Void> {

        private String url;
        private Document document=null;
        private String date;
        private String supertitle="";
        private String subtitle="";
        private String videoLink="";
        private String description;
        private Elements elements,elements2;
        private Element element;

        void setFeed(String string){
            this.url=string;
        }

        @Override
        protected Void doInBackground(Uri... uris) {
            if(url.startsWith("/")){
                url="yada"+url;
            }
            for(int i=0;i<10&&document==null;i++){
                try{
                    document=Jsoup.connect(url).get();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            /*Arhi tomea imerominias*/
            if(document!=null){
                elements = document.select("div[class=topinfo]");
                if (elements.size() > 0) {
                    elements2 = elements.select("span[class=field-name-field-created]");
                    if (elements2.size() > 0)
                        element = elements2.get(0);
                    String date1 = element.text();
                    if (!date1.equals(""))
                        date = date1;
                    String date2 = "";
                    elements2 = elements.select("span[class=field-name-field-changed]");
                    if (elements2.size() > 0) {
                        element = elements2.get(0);
                        if (element != null)
                            date2 = element.text();
                        if (!date2.equals("")) {
                            date += "\n" + date2;
                        }
                    }
                }
                else{
                    elements=document.select("div[class=panel-pane pane-page-content");
                    if(elements.size()>0)
                        elements2=elements.select("span[class=field-name-field-changed]");
                    if(elements2.size()>0)
                        element=elements2.get(0);
                    String date1=element.text();
                    if(!date1.equals(""))
                        date=date1;
                }
            }
            else{
                return null;
            }
            /*Telos tomea imerominias*/
            /*Arhi tomea ipertitlou*/
            if(document!=null){
                elements=document.select("header");
                elements2=elements.select("div[class=field field-name-field-supertitle field-type-text field-label-hidden]");
                String supertitle1="";
                if(elements2.size()>0){
                    element=elements2.get(0);
                    if(element!=null)
                        supertitle1=element.text();
                    supertitle=supertitle1;
                }
            }else{
                return null;
            }
            /*Telos tomea ipertitlou*/
            /*Arhi tomea ipotitlou*/
            if(document!=null){
                elements=document.select("header");
                elements2=elements.select("div[class=field field-name-field-subtitle field-type-text field-label-hidden]");
                String subtitle1="";
                if(elements2.size()>0){
                    element=elements2.get(0);
                    if(element!=null)
                        subtitle1=element.text();
                    subtitle=subtitle1;
                }
            }else{
                return null;
            }
            /*Telos tomea ipotitlou*/
            Elements elements2,elements3;
            Element element;
            if(document!=null){
                elements=document.select("div[id=resize-container]");
                if(elements.size()>0){
                    elements2 = elements.select("div[class=media-youtube-video media-youtube-1]");
                    if (elements2.size() > 0) {
                        elements3 = elements2.select("iframe[class=media-youtube-player]");
                        if (elements3.size() > 0) {
                            element = elements3.get(0);
                            if (element != null) {
                                String src = element.attr("src");
                                String[] parts = src.split("embed/");
                                final String[] parts2 = parts[1].split("\\?");
                                NewsItem.this.runOnUiThread(new Runnable() {
                                    public void run() {
                                        Button button = new Button(NewsItem.this);
                                        button.setText("Video");
                                        button.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(NewsItem.this, CustomYoutubePlayerActivity.class);
                                                intent.putExtra("videoLink", parts2[0]);
                                                startActivity(intent);
                                            }
                                        });
                                        linearLayout1.addView(button);
                                    }
                                });
                            }
                        }
                    }
                    elements2 = elements.select("div[class=field field-name-body field-type-text-with-summary field-label-hidden]");
                    if (elements2.size() > 0) {
                        Elements links = elements2.select("gr");
                        if (links.attr("href").startsWith("/eidisi") || links.attr("href").startsWith("/node")) {
                            links.attr("href", "yada" + links.attr("href"));
                        }
                        Elements paragraphs = elements2.select("p");
                        if (paragraphs.attr("align").equals("justify")) {
                            paragraphs.attr("align", "");
                        }
                        description = elements2.get(0).html();
                    }
                    NewsItem.this.runOnUiThread(new Runnable() {
                        public void run() {
                            final WebView webView1 = new WebView(NewsItem.this);
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            layoutParams.gravity = Gravity.CENTER;
                            webView1.setLayoutParams(layoutParams);
                            webView1.setInitialScale(220);
                            webView1.setPadding(0, 5, 0, 5);
                            webView1.setInitialScale(220);
                            webView1.loadData(description, "text/html;lang=el;charset=UTF-8", null);
                            ProgressBar content_loading = (ProgressBar) findViewById(R.id.content_loading);
                            content_loading.setVisibility(View.GONE);
                            button1=(FloatingActionButton)findViewById(R.id.zoomin);
                            button1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    webView1.setInitialScale((int) (webView1.getScale() * 100) + 15);
                                }
                            });
                            button2=(FloatingActionButton)findViewById(R.id.zoomout);
                            button2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    webView1.setInitialScale((int) (webView1.getScale() * 100) - 15);
                                }
                            });
                            linearLayout1.addView(webView1);
                        }
                    });
                    elements2 = elements.select("div[class=field field-name-field-tidings-paragraph field-type-field-collection field-label-hidden]");
                    elements2 = elements2.select("div[class=field-items]");
                    if (elements2.size() > 0) {
                        element = elements2.get(0);
                        elements2 = element.children();
                        HashMap<String, String> map;
                        for (int i = 0; i < elements2.size(); i++) {
                            map = new HashMap<>();
                            String collectionTitle = "";
                            String collectionBody = "";
                            elements3 = elements2.get(i).select("div[class=field field-name-field-paragraph-title field-type-text field-label-hidden]");
                            if (elements3 != null && elements3.size() > 0) {
                                element = elements3.get(0);
                                if (element != null) {
                                    Elements links = element.select("gr");
                                    if (links.attr("href").startsWith("/eidisi") || links.attr("href").startsWith("/node")) {
                                        links.attr("href", "yada" + links.attr("href"));
                                    }
                                    Elements paragraphs = element.select("p");
                                    if (paragraphs.attr("align").equals("justify")) {
                                        paragraphs.attr("align", "");
                                    }
                                    collectionTitle = element.html();
                                    map.put("collectionTitle", collectionTitle);
                                }
                            }
                            elements3 = elements2.get(i).select("div[class=field field-name-field-paragraph-body-intro]");
                            if (elements3 != null && elements3.size() > 0) {
                                element = elements3.get(0);
                                if (element != null) {
                                    Elements links = elements2.select("gr");
                                    if (links.attr("href").startsWith("/eidisi") || links.attr("href").startsWith("/node")) {
                                        links.attr("href", "yada" + links.attr("href"));
                                    }
                                    Elements paragraphs = elements2.select("p");
                                    if (paragraphs.attr("align").equals("justify")) {
                                        paragraphs.attr("align", "");
                                    }
                                    collectionTitle += element.html();
                                    map.put("collectionTitle", collectionTitle);
                                }
                            }
                            elements3 = elements2.get(i).select("div[class=field field-name-field-paragraph-media-video field-type-file field-label-hidden]");
                            if (elements3 != null && elements3.size() > 0) {
                                element = elements3.get(0);
                                elements3 = element.select("iframe[class=media-youtube-player]");
                                if (elements3.size() > 0) {
                                    element = elements3.get(0);
                                    if (element != null) {
                                        String src = element.attr("src");
                                        String[] parts = src.split("embed/");
                                        parts = parts[1].split("\\?");
                                        videoLink = parts[0];
                                        map.put("videoLink", videoLink);
                                    }
                                }
                            }
                            elements3 = elements2.get(i).select("div[class=field field-name-field-paragraph-body field-type-text-with-summary field-label-hidden]");
                            if (elements3 != null && elements3.size() > 0) {
                                element = elements3.get(0);
                                if (element != null) {
                                    Elements links = element.select("gr");
                                    if (links.attr("href").startsWith("/eidisi") || links.attr("href").startsWith("/node")) {
                                        links.attr("href", "yada" + links.attr("href"));
                                    }
                                    Elements paragraphs = element.select("p");
                                    if (paragraphs.attr("align").equals("justify")) {
                                        paragraphs.attr("align", "");
                                    }
                                    collectionBody = element.html();
                                    map.put("collectionBody", collectionBody);
                                }
                            }
                            if (!collectionTitle.equals("") || !collectionBody.equals("") || !videoLink.equals(""))
                                publishProgress(map);
                        }
                    }
                }
                else{
                    elements=document.select("div[class=panel-pane pane-page-content]");
                    if(elements.size()>0){
                        elements2 = elements.select("div[class=field field-name-body field-type-text-with-summary field-label-hidden]");
                        if (elements2.size() > 0) {
                            Elements links = elements2.select("gr");
                            if (links.attr("href").startsWith("/eidisi") || links.attr("href").startsWith("/node")) {
                                links.attr("href", "yada" + links.attr("href"));
                            }
                            Elements paragraphs = elements2.select("p");
                            if (paragraphs.attr("align").equals("justify")) {
                                paragraphs.attr("align", "");
                            }
                            description = elements2.get(0).html();
                        }
                        NewsItem.this.runOnUiThread(new Runnable() {
                            public void run() {
                                final WebView webView1 = new WebView(NewsItem.this);
                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                                layoutParams.gravity = Gravity.CENTER;
                                webView1.setLayoutParams(layoutParams);
                                webView1.setInitialScale(220);
                                webView1.setPadding(0, 5, 0, 5);
                                webView1.setInitialScale(220);
                                button1=(FloatingActionButton)findViewById(R.id.zoomin);
                                button1.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        webView1.setInitialScale((int) (webView1.getScale() * 100) + 15);
                                    }
                                });
                                button2=(FloatingActionButton)findViewById(R.id.zoomout);
                                button2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        webView1.setInitialScale((int) (webView1.getScale() * 100) - 15);
                                    }
                                });
                                webView1.loadData(description, "text/html;lang=el;charset=UTF-8", null);
                                ProgressBar content_loading = (ProgressBar) findViewById(R.id.content_loading);
                                content_loading.setVisibility(View.GONE);
                                linearLayout1.addView(webView1);
                            }
                        });
                    }
                }
            }else{
                description = "Ξαναφορτώστε την είδηση γιατί συνέβει κάποιο σφάλμα";
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(final HashMap<String,String>... i) {
            WebView webView1=new WebView(NewsItem.this);
            LinearLayout.LayoutParams layoutParams=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity=Gravity.CENTER;
            webView1.setLayoutParams(layoutParams);
            webView1.setPadding(0, 5, 0, 5);
            webView1.setInitialScale(220);
            webView1.getSettings().setBuiltInZoomControls(true);
            webView1.loadData(i[0].get("collectionTitle"), "text/html;lang=el;charset=UTF-8", null);
            linearLayout1.addView(webView1);
            Button button=new Button(NewsItem.this);
            button.setText("Video");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent=new Intent(NewsItem.this,CustomYoutubePlayerActivity.class);
                    intent.putExtra("videoLink",i[0].get("videoLink"));
                    startActivity(intent);
                }
            });
            linearLayout1.addView(button);
            WebView webView2=new WebView(NewsItem.this);
            LinearLayout.LayoutParams layoutParams2=new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams2.gravity=Gravity.CENTER;
            webView2.setLayoutParams(layoutParams);
            webView2.setInitialScale(220);
            webView2.setPadding(0, 5, 0, 5);
            webView2.getSettings().setBuiltInZoomControls(true);
            webView2.loadData(i[0].get("collectionBody"),"text/html;lang=el;charset=UTF-8",null);
            linearLayout1.addView(webView2);
        }

        @Override
        protected void onPostExecute(Void unused) {
            if (date != null)
                textView2.setText(date);
            if(!supertitle.equals("")){
                textView0.setText(supertitle);
                textView0.setVisibility(View.VISIBLE);
            }
            if(!subtitle.equals("")){
                textView3.setText(subtitle);
                textView3.setVisibility(View.VISIBLE);
            }
            if (!isNewspaper)
                swipeRefreshLayout.setRefreshing(false);
        }

    }

    private class ImageGetter extends AsyncTask<Uri, Void, Void> {

        private Bitmap bitmap;

        @Override
        protected Void doInBackground(Uri... uris) {
            try {
                HttpURLConnection connection = ( HttpURLConnection ) new URL( imageLink ).openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(input);
                input.close();
                connection.disconnect();
            }
            catch(IOException e) {
                Log.e("ImageGetter", e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void unused) {
            ProgressBar progressBar=(ProgressBar)findViewById(R.id.image_loading);
            progressBar.setVisibility(View.GONE);
            if (!isNewspaper) {
                int width = getWidth();
                int height = (int) (Math.round(width / 1.78));
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                imageView1.setLayoutParams(layoutParams);
                imageView1.setVisibility(View.VISIBLE);
                if(bitmap!=null){
                    imageView1.setImageBitmap(bitmap);
                }
                else{
                    imageView1.setImageBitmap(image);
                }
            } else {
                zoomableImageView.setVisibility(View.VISIBLE);
                if(bitmap!=null){
                    zoomableImageView.setImageBitmap(bitmap);
                }
                else{
                    zoomableImageView.setImageBitmap(image);
                }
            }
            if (!isNewspaper)
                swipeRefreshLayout.setRefreshing(false);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.news_item_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.refresh:
                ConnectivityManager connectivityManager=(ConnectivityManager)
                    getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if(connectivityManager.getActiveNetworkInfo()==null){
                    new AlertDialog.Builder(this).setMessage("Δεν είστε συνδεμένοι στο internet")
                        .setCancelable(false).setPositiveButton("Ok",null).show();
                }
                else{
                    ProgressBar progressBar=(ProgressBar)findViewById(R.id.image_loading);
                    progressBar.setVisibility(View.VISIBLE);
                    if (!isNewspaper) {
                        imageView1.setVisibility(View.GONE);
                        linearLayout1.removeAllViews();
                        ProgressBar progressBar1 = (ProgressBar) findViewById(R.id.content_loading);
                        progressBar1.setVisibility(View.VISIBLE);
                        NewsItemGetter newsItemGetter = new NewsItemGetter();
                        newsItemGetter.setFeed(link);
                        newsItemGetter.execute();
                        refreshListView();
                    } else {
                        zoomableImageView.setImageDrawable(null);
                    }
                    ImageGetter imageGetter = new ImageGetter();
                    imageGetter.execute();
                }
                break;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getWidth(){
        WindowManager wm=(WindowManager)getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        Display display=wm.getDefaultDisplay();
        DisplayMetrics metrics=new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.widthPixels;
    }

}