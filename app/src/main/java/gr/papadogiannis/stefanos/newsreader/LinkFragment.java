package gr.papadogiannis.stefanos.newsreader;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class LinkFragment extends Fragment {

	private ImageView imageView;
	private String url;
	private View view;
	private int[] drawables = {};
	private int[] urls={};

	public static LinkFragment newInstance(int number) {
		final LinkFragment linkFragment = new LinkFragment();
		final Bundle bundle = new Bundle(1);
	    bundle.putInt("number", number);
	    linkFragment.setArguments(bundle);
	    return linkFragment;
	}

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		final int number = getArguments().getInt("number");
		view = inflater.inflate(R.layout.myfragment_layout,container,false);
		imageView = (ImageView) view.findViewById(R.id.imageView);
		assignDrawableAndUrls(number);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view){
				RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF,
						0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
				rotateAnimation.setDuration(500);
				rotateAnimation.setAnimationListener(new AnimationListener() {

					@Override
					public void onAnimationStart(Animation animation) { }

					@Override
					public void onAnimationRepeat(Animation animation) { }

					@Override
					public void onAnimationEnd(Animation animation) {
						final Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(url));
						startActivity(intent);
					}

				});
				view.startAnimation(rotateAnimation);
			}

		});
        return view;
    }

	private void assignDrawableAndUrls(int i) {
		final Drawable drawable = getResources().getDrawable(drawables[i]);
		imageView.setImageDrawable(drawable);
		url = view.getResources().getString(urls[i]);
	}

}