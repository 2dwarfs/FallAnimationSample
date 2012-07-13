package com.twodwarfs.fallanimation;

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.tmp.animation.R;

public class FallAnimationActivity extends Activity {

	private int[] LEAVES = { 
			R.drawable.leaf_green, 
			R.drawable.leaf_red,
			R.drawable.leaf_yellow,
			R.drawable.leaf_other,
		};

	private Rect mDisplaySize = new Rect();
	
	private RelativeLayout mRootLayout;
	private ArrayList<View> mAllImageViews = new ArrayList<View>();
	
	private float mScale;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Display display = getWindowManager().getDefaultDisplay(); 
		display.getRectSize(mDisplaySize);
		
		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		mScale = metrics.density;

		mRootLayout = (RelativeLayout) findViewById(R.id.main_layout);

		new Timer().schedule(new ExeTimerTask(), 0, 5000);
	}

	public void startAnimation(final ImageView aniView) {

		aniView.setPivotX(aniView.getWidth()/2);
		aniView.setPivotY(aniView.getHeight()/2);

		long delay = new Random().nextInt(Constants.MAX_DELAY);

		final ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
		animator.setDuration(Constants.ANIM_DURATION);
		animator.setInterpolator(new AccelerateInterpolator());
		animator.setStartDelay(delay);

		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			
			int angle = 50 + (int)(Math.random() * 101);
			int movex = new Random().nextInt(mDisplaySize.right);
			
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float value = ((Float) (animation.getAnimatedValue())).floatValue();
				
				aniView.setRotation(angle*value);
				aniView.setTranslationX((movex-40)*value);
				aniView.setTranslationY((mDisplaySize.bottom + (150*mScale))*value);
			}
		});

		animator.start();
	}
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			int viewId = new Random().nextInt(LEAVES.length);
			Drawable d = getResources().getDrawable(LEAVES[viewId]);
			LayoutInflater inflate = LayoutInflater.from(FallAnimationActivity.this);
			ImageView imageView = (ImageView) inflate.inflate(R.layout.ani_image_view, null);
			imageView.setImageDrawable(d);
			mRootLayout.addView(imageView);
			
			mAllImageViews.add(imageView);			
			
			LayoutParams animationLayout = (LayoutParams) imageView.getLayoutParams();
			animationLayout.setMargins(0, (int)(-150*mScale), 0, 0);
			animationLayout.width = (int) (60*mScale);
			animationLayout.height = (int) (60*mScale);
			
			startAnimation(imageView);
		}
	};
	
	private class ExeTimerTask extends TimerTask {
		@Override
		public void run() {
			// we don't really use the message 'what' but we have to specify something.
			mHandler.sendEmptyMessage(Constants.EMPTY_MESSAGE_WHAT);
		}
	}
}