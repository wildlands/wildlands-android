/*
 * Copyright (c) 2012 Jason Polites
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.wildlands.wildlandseducation;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class GestureImageView extends ImageView{

    private static Activity mActivity;

    private long thread;

	public static final String GLOBAL_NS = "http://schemas.android.com/apk/res/android";
	public static final String LOCAL_NS = "http://schemas.polites.com/android";

	private final Semaphore drawLock = new Semaphore(0);
	private Animator animator;

    private Context context;
	private Drawable drawable;

	private float x = 0, y = 0;

	private boolean layout = false;

	private float scaleAdjust = 1.0f;
	private float startingScale = -1.0f;

	private float scale = 1.0f;
	private float maxScale = 5.0f;
	private float minScale = 0.75f;
	private float fitScaleHorizontal = 1.0f;
	private float fitScaleVertical = 1.0f;
	private float rotation = 0.0f;

	private float centerX;
	private float centerY;
	
	private Float startX, startY;

	private int hWidth;
	private int hHeight;

    private float tempX;
    private float tempY;

    private LinearLayout layoutOfPopup;

    int i = 0;

    private Canvas tempCanvas;
	private int resId = -1;
	private boolean recycle = false;
	private boolean strict = false;

	private int displayHeight;
	private int displayWidth;

	private int alpha = 255;
	private ColorFilter colorFilter;


	private int deviceOrientation = -1;
	private int imageOrientation;

	private GestureImageViewListener gestureImageViewListener;
	private GestureImageViewTouchListener gestureImageViewTouchListener;
	
	private OnTouchListener customOnTouchListener;
	private OnClickListener onClickListener;

    private float totalDiffX;
    private float totalDiffY;
    private ImageView underbar;
    private boolean switchBar;
    private ButtonHandler btnHandler;
    private ArrayList<ImageButton> imageButtons;
    private PopupWindow popupWindow;
    ViewPager mViewPager;
    //TabsAdapter mTabsAdapter;

    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
    RelativeLayout.LayoutParams lp2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);


	public GestureImageView(Context context, AttributeSet attrs, int defStyle) {
		this(context, attrs);
        thread=Thread.currentThread().getId();
        this.context = context;
        RelativeLayout kaart = (RelativeLayout)findViewById(R.id.kaartScreen);
       // imageBtn.setTranslationX(400);
	}

	public GestureImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		String scaleType = attrs.getAttributeValue(GLOBAL_NS, "scaleType");
		
		if(scaleType == null || scaleType.trim().length() == 0) {
			setScaleType(ScaleType.CENTER_INSIDE);
		}
		
		String strStartX = attrs.getAttributeValue(LOCAL_NS, "start-x");
		String strStartY = attrs.getAttributeValue(LOCAL_NS, "start-y");
		
		if(strStartX != null && strStartX.trim().length() > 0) {
			startX = Float.parseFloat(strStartX);
		}
		
		if(strStartY != null && strStartY.trim().length() > 0) {
			startY = Float.parseFloat(strStartY);
		}
		
		setStartingScale(attrs.getAttributeFloatValue(LOCAL_NS, "start-scale", startingScale));
		setMinScale(attrs.getAttributeFloatValue(LOCAL_NS, "min-scale", minScale));
		setMaxScale(attrs.getAttributeFloatValue(LOCAL_NS, "max-scale", maxScale));
		setStrict(attrs.getAttributeBooleanValue(LOCAL_NS, "strict", strict));
		setRecycle(attrs.getAttributeBooleanValue(LOCAL_NS, "recycle", recycle));
        thread=Thread.currentThread().getId();
		initImage();
        this.context = context;
        btnHandler = new ButtonHandler();
        layoutOfPopup = new LinearLayout(context);
        imageButtons = new ArrayList<ImageButton>();
        totalDiffX = 0;
        totalDiffY = 0;
        switchBar = true;
	}

	public GestureImageView(Context context) {
		super(context);
		setScaleType(ScaleType.CENTER_INSIDE);
        thread=Thread.currentThread().getId();
		initImage();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		if(drawable != null) {
			int orientation = getResources().getConfiguration().orientation;
			if(orientation == Configuration.ORIENTATION_LANDSCAPE) {
				displayHeight = MeasureSpec.getSize(heightMeasureSpec);

				if(getLayoutParams().width == LayoutParams.WRAP_CONTENT) {
					float ratio = (float) getImageWidth() / (float) getImageHeight();
					displayWidth = Math.round((float) displayHeight * ratio) ;
				}
				else {
					displayWidth = MeasureSpec.getSize(widthMeasureSpec);
				}
			}
			else {
				displayWidth = MeasureSpec.getSize(widthMeasureSpec);
				if(getLayoutParams().height == LayoutParams.WRAP_CONTENT) {
					float ratio = (float) getImageHeight() / (float) getImageWidth();
					displayHeight = Math.round((float) displayWidth * ratio) ;
				}
				else {
					displayHeight = MeasureSpec.getSize(heightMeasureSpec);
				}				
			}
		}
		else {
			displayHeight = MeasureSpec.getSize(heightMeasureSpec);
			displayWidth = MeasureSpec.getSize(widthMeasureSpec);
		}

		setMeasuredDimension(displayWidth, displayHeight);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);
		if(changed || !layout) {
			setupCanvas(displayWidth, displayHeight, getResources().getConfiguration().orientation);
		}
	}

	protected void setupCanvas(int measuredWidth, int measuredHeight, int orientation) {

		if(deviceOrientation != orientation) {
			layout = false;
			deviceOrientation = orientation;
		}

		if(drawable != null && !layout) {
			int imageWidth = getImageWidth();
			int imageHeight = getImageHeight();

			hWidth = Math.round(((float) imageWidth / 2.0f));
			hHeight = Math.round(((float) imageHeight / 2.0f));
			
			measuredWidth -= (getPaddingLeft() + getPaddingRight());
			measuredHeight -= (getPaddingTop() + getPaddingBottom());
			
			computeCropScale(imageWidth, imageHeight, measuredWidth, measuredHeight);
			
			if(startingScale <= 0.0f) {
				computeStartingScale(imageWidth, imageHeight, measuredWidth, measuredHeight);
			}

			scaleAdjust = startingScale;

			this.centerX = (float) measuredWidth / 2.0f;
			this.centerY = (float) measuredHeight / 2.0f;
			
			if(startX == null) {
				x = centerX;
			}
			else {
				x = startX;
			}

			if(startY == null) {
				y = centerY;
			}
			else {
				y = startY;
			}	

			gestureImageViewTouchListener = new GestureImageViewTouchListener(this, measuredWidth, measuredHeight);
			
			if(isLandscape()) {
				gestureImageViewTouchListener.setMinScale(minScale * fitScaleHorizontal);
			}
			else {
				gestureImageViewTouchListener.setMinScale(minScale * fitScaleVertical);
			}
			
			
			gestureImageViewTouchListener.setMaxScale(maxScale * startingScale);
			
			gestureImageViewTouchListener.setFitScaleHorizontal(fitScaleHorizontal);
			gestureImageViewTouchListener.setFitScaleVertical(fitScaleVertical);
			gestureImageViewTouchListener.setCanvasWidth(measuredWidth);
			gestureImageViewTouchListener.setCanvasHeight(measuredHeight);
			gestureImageViewTouchListener.setOnClickListener(onClickListener);

			drawable.setBounds(-hWidth,-hHeight,hWidth,hHeight);

			super.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if(customOnTouchListener != null) {
						customOnTouchListener.onTouch(v, event);
					}
					return gestureImageViewTouchListener.onTouch(v, event);
				}
			});	

			layout = true;
		}
	}
	
	protected void computeCropScale(int imageWidth, int imageHeight, int measuredWidth, int measuredHeight) {
		fitScaleHorizontal = (float) measuredWidth / (float) imageWidth;
		fitScaleVertical = (float) measuredHeight / (float) imageHeight;
	}

    public void addButton(int x, int y, int id)
    {

        ImageButton imageBtn = new ImageButton(context);
        imageBtn.setId(i+id);
        imageBtn.setImageResource(R.drawable.pin);
        imageBtn.setBackground(null);
        imageBtn.setLayoutParams(lp);
        imageBtn.setTranslationX(x);
        imageBtn.setTranslationY(y);
        imageBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startPopUp();


            }
        });
        Log.d("imgid", String.valueOf(imageBtn.getId()));
        imageButtons.add(imageBtn);
        RelativeLayout kaart = (RelativeLayout)getRootView().findViewById(R.id.kaartScreen);
        kaart.addView(imageBtn);

    }

    public void addUnderbar()
    {
        underbar = new ImageView(context);
        underbar.setLayoutParams(lp);
        underbar.setImageResource(R.drawable.ice);
        underbar.setTranslationY(1680);
        RelativeLayout kaart = (RelativeLayout)getRootView().findViewById(R.id.kaartScreen);
        kaart.addView(underbar);
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void startPopUp()
    {
        Log.d("popi=up", "gestart");
        RelativeLayout rlTotal = new RelativeLayout(context);
        /*
        HorizontalScrollView hScroll = new HorizontalScrollView(context);
        RelativeLayout rlHor = new RelativeLayout(context);
        hScroll.addView(rlHor);

        for(int i = 0; i < 2; i++) {
            ScrollView sc = new ScrollView(context);
            //sc.setTranslationY(650);
            RelativeLayout rlScroll = new RelativeLayout(context);
            sc.addView(rlScroll);

            ImageButton closeButton = new ImageButton(context);
            int j = 101;
            closeButton.setId(j + 0);
            closeButton.setImageResource(R.drawable.closebtn);
            closeButton.setBackground(null);
            closeButton.setTranslationY(100);
            closeButton.setTranslationX(650);
            closeButton.setLayoutParams(lp);
            closeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });


            ImageView img = new ImageView(context);
            img.setLayoutParams(lp);
            img.setImageResource(R.drawable.duck2);
            img.setTranslationY(50);


            TextView textView = new TextView(context);
            textView.setText("Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus. ");
            textView.setTranslationY(650);
            rlScroll.addView(textView);
            rlTotal.setBackgroundResource(R.drawable.background);
           // sc.addView(imgView);
            rlScroll.addView(img);
            rlHor.addView(sc);

            rlHor.addView(closeButton);


        }
        rlTotal.addView(hScroll);
        popupWindow = new PopupWindow(rlTotal, 900, 1600);
        //popupWindow.setContentView(layoutOfPopup);
        popupWindow.showAtLocation(this, Gravity.NO_GRAVITY, 100, 50);
        */

    }

	protected void computeStartingScale(int imageWidth, int imageHeight, int measuredWidth, int measuredHeight) {
		switch(getScaleType()) {
			case CENTER:
				// Center the image in the view, but perform no scaling.
				startingScale = 1.0f;
				break;
				
			case CENTER_CROP:
				// Scale the image uniformly (maintain the image's aspect ratio) so that both dimensions
				// (width and height) of the image will be equal to or larger than the corresponding dimension of the view (minus padding).
				startingScale = Math.max((float) measuredHeight / (float) imageHeight, (float) measuredWidth / (float) imageWidth);
				break;
				
			case CENTER_INSIDE:

				// Scale the image uniformly (maintain the image's aspect ratio) so that both dimensions
				// (width and height) of the image will be equal to or less than the corresponding dimension of the view (minus padding).
				float wRatio = (float) imageWidth / (float) measuredWidth;
				float hRatio = (float) imageHeight / (float) measuredHeight;

				if(wRatio > hRatio) {
					startingScale = fitScaleHorizontal;
				}
				else {
					startingScale = fitScaleVertical;
				}

				break;
		}
	}

	protected boolean isRecycled() {
		if(drawable != null && drawable instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
			if(bitmap != null) {
				return bitmap.isRecycled();
			}
		}
		return false;
	}

	protected void recycle() {
		if(recycle && drawable != null && drawable instanceof BitmapDrawable) {
			Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
			if(bitmap != null) {
				bitmap.recycle();
			}
		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		if(layout) {
			if(drawable != null && !isRecycled()) {
				canvas.save();
				
				float adjustedScale = scale * scaleAdjust;

				canvas.translate(x, y);

				if(rotation != 0.0f) {
					canvas.rotate(rotation);
				}

				if(adjustedScale != 1.0f) {
					canvas.scale(adjustedScale, adjustedScale);
				}

				drawable.draw(canvas);


				canvas.restore();
			}

			if(drawLock.availablePermits() <= 0) {
				drawLock.release();
			}
		}

	}


	public boolean waitForDraw(long timeout) throws InterruptedException {
		return drawLock.tryAcquire(timeout, TimeUnit.MILLISECONDS);
	}

	@Override
	protected void onAttachedToWindow() {

		animator = new Animator(this, "GestureImageViewAnimator");
		animator.start();

		if(resId >= 0 && drawable == null) {
			setImageResource(resId);
		}

		super.onAttachedToWindow();
	}

	public void animationStart(Animation animation) {
		if(animator != null) {
			animator.play(animation);
		}
	}

	public void animationStop() {
		if(animator != null) {
			animator.cancel();
		}
	}

	@Override
	protected void onDetachedFromWindow() {
		if(animator != null) {
			animator.finish();
		}
		if(recycle && drawable != null && !isRecycled()) {
			recycle();
			drawable = null;
		}
		super.onDetachedFromWindow();
	}

	protected void initImage() {
		if(this.drawable != null) {
			this.drawable.setAlpha(alpha);
			this.drawable.setFilterBitmap(true);
			if(colorFilter != null) {
				this.drawable.setColorFilter(colorFilter);
			}
		}
		
		if(!layout) {
			requestLayout();
			redraw();
		}

	}

	public void setImageBitmap(Bitmap image) {
		this.drawable = new BitmapDrawable(getResources(), image);
		initImage();
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		this.drawable = drawable;
		initImage();
	}

	public void setImageResource(int id) {
		if(this.drawable != null) {
			this.recycle();
		}
		if(id >= 0) {
			this.resId = id;
			setImageDrawable(getContext().getResources().getDrawable(id));
		}
	}

	public int getScaledWidth() {
		return Math.round(getImageWidth() * getScale());
	}
	
	public int getScaledHeight() {
		return Math.round(getImageHeight() * getScale());
	}
	
	public int getImageWidth() {
		if(drawable != null) {
			return drawable.getIntrinsicWidth();
		}
		return 0;
	}

	public int getImageHeight() {
		if(drawable != null) {
			return drawable.getIntrinsicHeight();
		}
		return 0;
	}

    //public void addImageButton(ImageButton imgBtn)
   // {
   //     this.imageBtn = imgBtn;
    //}

	public void moveBy(float x, float y) {
		this.x += x;
		this.y += y;
        for(ImageView imageButton: imageButtons) {
            imageButton.setTranslationY(y);
            imageButton.setTranslationX(x);
        }
	}

	public void setPosition(float x, float y) {
        float diff = this.x - x;
        float diffY = this.y - y;
		this.x = x;
		this.y = y;
        tempX = diff;
        tempY = diffY;
        totalDiffY += diffY;
        totalDiffX += diff;
        if(totalDiffX > 600 && switchBar)
        {
            switchBar = false;
            new ChangeBar().execute();

        }else if(totalDiffX < 600 && switchBar == false){
            switchBar = true;
            new ChangeBar().execute();
        }
        new Move().execute();

      // new UISwitch(MyActivity.instance).execute();

	}

	public void redraw() {
		postInvalidate();
	}

	public void setMinScale(float min) {
		this.minScale = min;
		if(gestureImageViewTouchListener != null) {
			gestureImageViewTouchListener.setMinScale(min * fitScaleHorizontal);
		}
	}

	public void setMaxScale(float max) {
		this.maxScale = max;
		if(gestureImageViewTouchListener != null) {
			gestureImageViewTouchListener.setMaxScale(max * startingScale);
		}
	}

	public void setScale(float scale) {
		scaleAdjust = scale;
	}

	public float getScale() {
		return scaleAdjust;
	}

	public float getImageX() {
		return x;
	}

	public float getImageY() {
		return y;
	}

	public boolean isStrict() {
		return strict;
	}

	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	public boolean isRecycle() {
		return recycle;
	}

	public void setRecycle(boolean recycle) {
		this.recycle = recycle;
	}

	public void reset() {
		x = centerX;
		y = centerY;

		scaleAdjust = startingScale;
		if (gestureImageViewTouchListener != null) {
		    gestureImageViewTouchListener.reset();
		}
		redraw();
	}

	public void setRotation(float rotation) {
		this.rotation = rotation;
	}

	public void setGestureImageViewListener(GestureImageViewListener pinchImageViewListener) {
		this.gestureImageViewListener = pinchImageViewListener;
	}

	public GestureImageViewListener getGestureImageViewListener() {
		return gestureImageViewListener;
	}

	@Override
	public Drawable getDrawable() {
		return drawable;
	}

	@Override
	public void setAlpha(int alpha) {
		this.alpha = alpha;
		if(drawable != null) {
			drawable.setAlpha(alpha);
		}
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		this.colorFilter = cf;
		if(drawable != null) {
			drawable.setColorFilter(cf);
		}
	}

	@Override
	public void setImageURI(Uri mUri) {
		if ("content".equals(mUri.getScheme())) {
			try {
				String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
				
				Cursor cur = getContext().getContentResolver().query(mUri, orientationColumn, null, null, null);
				
				if (cur != null && cur.moveToFirst()) {
					imageOrientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
				}  
				
				InputStream in = null;
				
				try {
					in = getContext().getContentResolver().openInputStream(mUri);
					Bitmap bmp = BitmapFactory.decodeStream(in);
					
					if(imageOrientation != 0) {
						Matrix m = new Matrix();
						m.postRotate(imageOrientation);
						Bitmap rotated = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), m, true);
						bmp.recycle();
						setImageDrawable(new BitmapDrawable(getResources(), rotated));
					}
					else {
						setImageDrawable(new BitmapDrawable(getResources(), bmp));
					}
				}
				finally {
					if(in != null) {
						in.close();
					}
					
					if(cur != null) {
						cur.close();
					}
				}
			}
			catch (Exception e) {
				Log.w("GestureImageView", "Unable to open content: " + mUri, e);
			}
		}
		else {
			setImageDrawable(Drawable.createFromPath(mUri.toString()));
		}

		if (drawable == null) {
			Log.e("GestureImageView", "resolveUri failed on bad bitmap uri: " + mUri);
			// Don't try again.
			mUri = null;
		}
	}

	@Override
	public Matrix getImageMatrix() {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}		
		return super.getImageMatrix();
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		if(scaleType == ScaleType.CENTER ||
			scaleType == ScaleType.CENTER_CROP ||
			scaleType == ScaleType.CENTER_INSIDE) {
			
			super.setScaleType(scaleType);
		}
		else if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
	}

	@Override
	public void invalidateDrawable(Drawable dr) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
		super.invalidateDrawable(dr);
	}

	@Override
	public int[] onCreateDrawableState(int extraSpace) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
		return super.onCreateDrawableState(extraSpace);
	}

	@Override
	public void setAdjustViewBounds(boolean adjustViewBounds) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
		super.setAdjustViewBounds(adjustViewBounds);
	}

	@Override
	public void setImageLevel(int level) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
		super.setImageLevel(level);
	}

	@Override
	public void setImageMatrix(Matrix matrix) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
	}

	@Override
	public void setImageState(int[] state, boolean merge) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
	}

	@Override
	public void setSelected(boolean selected) {
		if(strict) {
			throw new UnsupportedOperationException("Not supported");
		}
		super.setSelected(selected);
	}

	@Override
	public void setOnTouchListener(OnTouchListener l) {
		this.customOnTouchListener = l;
	}
	
	public float getCenterX() {
		return centerX;
	}
	
	public float getCenterY() {
		return centerY;
	}
	
	public boolean isLandscape() {
		return getImageWidth() >= getImageHeight();
	}
	
	public boolean isPortrait() {
		return getImageWidth() <= getImageHeight();
	}
	
	public void setStartingScale(float startingScale) {
		this.startingScale = startingScale;
	}
	
	public void setStartingPosition(float x, float y) {
		this.startX = x;
		this.startY = y;
	}

	@Override
	public void setOnClickListener(OnClickListener l) {
		this.onClickListener = l;
		
		if(gestureImageViewTouchListener != null) {
			gestureImageViewTouchListener.setOnClickListener(l);
		}
	}

	/**
	 * Returns true if the image dimensions are aligned with the orientation of the device.
	 * @return
	 */
	public boolean isOrientationAligned() {
		if(deviceOrientation == Configuration.ORIENTATION_LANDSCAPE) {
			return isLandscape();
		}
		else if(deviceOrientation == Configuration.ORIENTATION_PORTRAIT) {
			return isPortrait();
		}
		return true;
	}
	
	public int getDeviceOrientation() {
		return deviceOrientation;
	}

    class Move extends AsyncTask<String, String, String> implements View.OnClickListener {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {

            return "ja";

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            for(ImageButton imageButton: imageButtons)
            {
                float x = imageButton.getX();
                float y = imageButton.getY();
                imageButton.setTranslationX(x - tempX);
                imageButton.setTranslationY(y - tempY);
                redraw();
            }

        }

        @Override
        public void onClick(View v) {

        }
    }

    class ChangeBar extends AsyncTask<String, String, String> implements View.OnClickListener {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {

            return "ja";

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
            if(totalDiffX > 600){
                underbar.setImageResource(R.drawable.desert);
            }
            else if(totalDiffX < 600){
                underbar.setImageResource(R.drawable.ice);
            }
        }

        @Override
        public void onClick(View v) {

        }
    }

    class ZoomScale extends AsyncTask<String, String, String> implements View.OnClickListener {

        boolean failure = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... args) {

            return "ja";

        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog once product deleted
           // imageBtn.draw(tempCanvas);


        }

        @Override
        public void onClick(View v) {

        }
    }

    public class ButtonHandler implements View.OnClickListener {

        public ButtonHandler()
        {

        }
        @Override
        public void onClick(View v) {

            switch(v.getId())
            {
                case 1:
                    startPopUp();
                    break;
                case 100:

                    startPopUp();
                    break;
                case 101:
                    popupWindow = null;
                    break;

            }
        }
    }

    public class ScreenSlidePageFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            ViewGroup rootView = (ViewGroup) inflater.inflate(
                    R.layout.fragment_pinpoint, container, false);

            return rootView;
        }
    }

}
