package gr.papadogiannis.stefanos.newsreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;

/**
 * Receives broadcasted intents. In particular, we are interested in the
 * android.media.AUDIO_BECOMING_NOISY and android.intent.action.MEDIA_BUTTON intents, which is
 * broadcast, for example, when the user disconnects the headphones. This class works because we are
 * declaring it in a &lt;receiver&gt; tag in AndroidManifest.xml.
 */
public class MusicIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            // send an intent to our MusicService to telling it to pause the audio
            final Intent intentTemp = new Intent(MusicService.ACTION_PAUSE);
            intentTemp.setPackage(context.getPackageName());
            context.startService(intentTemp);
        } else if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            final KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent != null) {
                if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                    return;
                switch (keyEvent.getKeyCode()) {
                    case KeyEvent.KEYCODE_HEADSETHOOK:
                    case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                        context.startService(new Intent(MusicService.ACTION_TOGGLE_PLAYBACK));
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PLAY:
                        context.startService(new Intent(MusicService.ACTION_PLAY));
                        break;
                    case KeyEvent.KEYCODE_MEDIA_PAUSE:
                        context.startService(new Intent(MusicService.ACTION_PAUSE));
                        break;
                    case KeyEvent.KEYCODE_MEDIA_STOP:
                        context.startService(new Intent(MusicService.ACTION_STOP));
                        break;
                }
            }
        }
    }

}
