package gr.papadogiannis.stefanos.newsreader;

import java.io.IOException;
import android.app.Activity;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;


public class Webradio2Activity extends Activity implements OnClickListener{
	private static Button buttonPlay;
    private static Button buttonStopPlay;
	private static boolean isPaused=false;
    private static MediaPlayer mediaPlayer=null;
    private static int isSet=0;
    private static boolean BUTTON_PLAY_ENABLED;
    private static boolean BUTTON_STOP_PLAY_ENABLED;
    private static int VISIBILITY;
    private static ProgressBar progressBar;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webradio2);
        initializeUIElements();
        if(isSet==1){
	    	buttonPlay.setEnabled(BUTTON_PLAY_ENABLED);
	    	buttonStopPlay.setEnabled(BUTTON_STOP_PLAY_ENABLED);
	    	progressBar.setVisibility(VISIBILITY);
	    }
        if(mediaPlayer==null){
        	initializeMediaPlayer();
        }
    }
    private void initializeUIElements(){
        buttonPlay=(Button)findViewById(R.id.buttonPlay);
        buttonPlay.setOnClickListener(this);
        buttonStopPlay=(Button)findViewById(R.id.buttonStopPlay);
        buttonStopPlay.setEnabled(false);
        buttonStopPlay.setOnClickListener(this);
        progressBar=(ProgressBar)findViewById(R.id.circularProgressBar);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }
    public void onClick(View view){
        if(view==buttonPlay){
            startPlaying();
        }
        else if(view==buttonStopPlay){
            stopPlaying();
        }
    }
    private void startPlaying(){
        buttonPlay.setEnabled(false);
        if(isPaused){
        	mediaPlayer.start();
        	isPaused=false;
        	buttonStopPlay.setEnabled(true);
        }
        else{
        	progressBar.setVisibility(View.VISIBLE);
	        mediaPlayer.prepareAsync();
	        mediaPlayer.setOnPreparedListener(new OnPreparedListener(){
				@Override
				public void onPrepared(MediaPlayer mediaPlayer){
					mediaPlayer.start();
					progressBar.setVisibility(View.INVISIBLE);
					buttonStopPlay.setEnabled(true);
				}
			});
        }
    }
    private void stopPlaying(){
        if(mediaPlayer.isPlaying()){
        	mediaPlayer.pause();
        }
        isPaused=true;
        buttonPlay.setEnabled(true);
        buttonStopPlay.setEnabled(false);
    }
    @Override
    public void onStop(){
    	BUTTON_PLAY_ENABLED=buttonPlay.isEnabled();
    	BUTTON_STOP_PLAY_ENABLED=buttonStopPlay.isEnabled();
    	VISIBILITY=progressBar.getVisibility();
    	isSet=1;
    	super.onStop();
    }
    @Override
    public void finish(){
    	stopPlaying();
    	super.finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
		if(item.getItemId()==android.R.id.home){	
        	finish();
        	return true;	        		
        }
        return super.onOptionsItemSelected(item);
    }
    private void initializeMediaPlayer(){ 
        mediaPlayer=new MediaPlayer();
        setPlayerDataSource();
    }
    private void setPlayerDataSource(){
    	try{
            mediaPlayer.setDataSource("");
        }
        catch(IllegalArgumentException e){
            e.printStackTrace();
        }
        catch(IllegalStateException e){
            e.printStackTrace();
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig){
    	super.onConfigurationChanged(newConfig);
    	Configuration configuration=getResources().getConfiguration();
    	if(configuration.orientation==Configuration.ORIENTATION_PORTRAIT){
    		setContentView(R.layout.webradio2);
    	}
    	else if(configuration.orientation==Configuration.ORIENTATION_LANDSCAPE){
    		setContentView(R.layout.webradio2);
    	}
    }
}