package gr.papadogiannis.stefanos.newsreader;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_screen);
		Animation animation=AnimationUtils.loadAnimation(this,R.anim.fade_in);
		ImageView imageView=(ImageView)findViewById(R.id.imgLogo);
		TextView textView=(TextView)findViewById(R.id.moto);
		imageView.startAnimation(animation);
		textView.startAnimation(animation);
		int SPLASH_TIME_OUT = 3000;
		new Handler().postDelayed(new Runnable(){
			/*
			 * Showing splash screen with a timer. This will be useful when you
			 * want to show case your app logo / company
			 */
			@Override
			public void run(){
				// This method will be executed once the timer is over
				// Start your app main activity
				Intent intent=new Intent(SplashScreen.this,MainActivity.class);
				startActivity(intent);
				overridePendingTransition(R.anim.fade_in2,R.anim.fade_out);
				// close this activity
				finish();
			}
		}, SPLASH_TIME_OUT);
	}

}