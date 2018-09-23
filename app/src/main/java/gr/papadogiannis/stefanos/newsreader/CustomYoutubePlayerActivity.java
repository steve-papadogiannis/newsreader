package gr.papadogiannis.stefanos.newsreader;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeIntents;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class CustomYoutubePlayerActivity extends YouTubeBaseActivity
    implements YouTubePlayer.OnInitializedListener {

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private String videoLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.custom_youtube);
        final Intent intent = getIntent();
        videoLink = intent.getStringExtra("videoLink");
        if (doesDeviceSupportYouTubePlayback()) {
            YouTubePlayerView youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
            youTubeView.initialize(Config.DEVELOPER_KEY, this);
        } else {
            Toast.makeText(this, "Η έκδοση του youtube που χρειάζεται για να παίξει τα πολυμέσα"
                + " στην είδηση πρέπει να είναι μεγαλύτερη ή ίση της 4.2.16", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format(
                "Error on youtube player: %s", errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
        YouTubePlayer player, boolean wasRestored) {
        if (!wasRestored) {
            player.setShowFullscreenButton(false);
            player.cueVideo(videoLink);
        }
    }

    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.youtube_view);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOVERY_DIALOG_REQUEST) {
            // Retry initialization if user performed a recovery action
            getYouTubePlayerProvider().initialize(Config.DEVELOPER_KEY, this);
        }
    }

    private boolean doesDeviceSupportYouTubePlayback() {
        final String version = YouTubeIntents.getInstalledYouTubeVersionName(this);
        if (version != null) {
            final String[] partsOfVersion = version.split("\\.");
            int first = partsOfVersion.length > 0 ? Integer.parseInt(partsOfVersion[0]) : 0;
            int second = partsOfVersion.length > 1 ? Integer.parseInt(partsOfVersion[1]) : 0;
            int third = partsOfVersion.length > 2 ? Integer.parseInt(partsOfVersion[2]) : 0;
            return (first == 4 && second == 2 && third >= 16) || (first == 4 && second >= 3) || first >= 5;
        }
        return false;
    }

}
