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
package nl.wildlands.wildlandseducation.MapAssets;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import nl.wildlands.wildlandseducation.MainPagerAdapter;

import org.apache.http.protocol.HTTP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import nl.wildlands.wildlandseducation.Activities.Kaart;
import nl.wildlands.wildlandseducation.GlobalSettings.DefaultApplication;
import nl.wildlands.wildlandseducation.Pinpoint.Page;
import nl.wildlands.wildlandseducation.Pinpoint.PageImage;
import nl.wildlands.wildlandseducation.Pinpoint.Pinpoint;
import nl.wildlands.wildlandseducation.R;
import nl.wildlands.wildlandseducation.SQLite.PinpointsDataSource;

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
	private float startingScale = 2.0f;

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

    private PinpointsDataSource pinpointsDataSource;

    private float totalDiffX;
    private float totalDiffY;
    private ImageView underbar;
    private boolean switchBar;

    private ArrayList<ImageButton> imageButtons;
    private PopupWindow popupWindow;
    ViewPager mViewPager;
    //TabsAdapter mTabsAdapter;
    private int level;

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
        layoutOfPopup = new LinearLayout(context);
        imageButtons = new ArrayList<ImageButton>();
        totalDiffX = 0;
        totalDiffY = 0;
        switchBar = true;
        pinpointsDataSource = new PinpointsDataSource(context);
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

    /**
     * Voeg een nieuwe pinpoint toe
     * @param xNew
     * @param yNew
     * @param id
     * @param soort
     */
    public void addButton(int xNew, int yNew, long id, String soort)
    {
        final long btnId = id;
        Log.d("soort die in kaart ", soort);
        ImageButton imageBtn = new ImageButton(context);
        imageBtn.setId(i+(int)id);
        // Image afhankelijk van thema
        if(soort.equals("Energie")) {
            imageBtn.setImageResource(R.drawable.pinpoint_energie);
        }
        else if(soort.equals("Water"))
        {
            imageBtn.setImageResource(R.drawable.pinpoint_water);
        }
        else if(soort.equals("Bio Mimicry"))
        {
            imageBtn.setImageResource(R.drawable.pinpoint_bio);
        }
        else if(soort.equals("Materiaal")){
            imageBtn.setImageResource(R.drawable.pinpoint_materiaal);
        }
        else{
            imageBtn.setImageResource(R.drawable.pinpoint_dieren);
        }
        // ImageButton aanpassen
        imageBtn.setBackgroundColor(Color.TRANSPARENT);
        imageBtn.setLayoutParams(lp);

        imageBtn.setTranslationX(xNew/2);
        imageBtn.setTranslationY(yNew/2);
        // Bij klik start dialog
        imageBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                startAlertdialog(btnId);


            }
        });
        Log.d("imgid", String.valueOf(imageBtn.getId()));
        imageButtons.add(imageBtn);

        RelativeLayout kaart = (RelativeLayout)getRootView().findViewById(R.id.kaartScreen);
        kaart.addView(imageBtn);
    }

    public void setLevel(int level)
    {
        this.level = level;
    }
    /**
     * Start een dialog voor de geklikte pinpint
     * @param id
     */

    public void startAlertdialog(long id)
    {
        pinpointsDataSource.open();
        long pinpointId = -1;
        int type = -1;
        ArrayList<Pinpoint> pinpoints = pinpointsDataSource.getAllPinpoints();
        ArrayList<Page> pages = new ArrayList<Page>();
        String image;
        // Haal het type op
        for(Pinpoint pinpoint: pinpoints)
        {
           if(pinpoint.getPinpointId() == id)
           {
               pinpointId = id;
               Log.d("pinpointid", String.valueOf(pinpointId));
               if(pinpoint.getType().equals("Energie"))
               {
                   type = 1;
               }
               else if(pinpoint.getType().equals("Water"))
               {
                   type = 2;
               }
               else if(pinpoint.getType().equals("Materiaal"))
               {
                   type = 3;
               }
               else if(pinpoint.getType().equals("Bio Mimicry"))
               {
                   type = 4;
               }
               else
               {
                   type = 5;
               }
           }
        }

        for(Page page: pinpointsDataSource.getAllPages())
        {
            if(page.getPinpointid() == pinpointId && page.getLevel() == level)
            {
                // Voeg de pagina's toe aan arraylist
                pages.add(page);
            }
        }



        RelativeLayout kaart = (RelativeLayout)getRootView().findViewById(R.id.kaartScreen);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        View resultsView = LayoutInflater.from(context).inflate(R.layout.customviewpager,
                null);
        ViewPager vp = (ViewPager)resultsView.findViewById(R.id.pager);
        MainPagerAdapter pagerAdapter = new MainPagerAdapter();
        vp.setAdapter(pagerAdapter);

        // Maak 5 pagina's aan
        View haha = LayoutInflater.from(context).inflate(R.layout.custom_popup,null);
        View haha2 = LayoutInflater.from(context).inflate(R.layout.custom_popup,null);
        View haha3 = LayoutInflater.from(context).inflate(R.layout.custom_popup,null);
        View haha4 = LayoutInflater.from(context).inflate(R.layout.custom_popup,null);
        View haha5 = LayoutInflater.from(context).inflate(R.layout.custom_popup,null);
        ArrayList<View> views = new ArrayList<View>();

        alertDialogBuilder
                .setView(resultsView)
                .setCancelable(true)
        ;


        final AlertDialog alertDialog = alertDialogBuilder.create();

        // Voeg pagina's toe aan de views en bouw deze afhankelijk van de waardes
        for(int i =0; i < pages.size();i++) {
            Page page = pages.get(i);
            String path = "";
            String name = "";
            Bitmap b = null;
            for(PageImage pageImage: pinpointsDataSource.getAllPageImages())
            {
                if(pageImage.getPageId() == page.getId())
                {
                    path = pageImage.getImagePath();
                    name = pageImage.getName();
                    Log.d("name", name);
                }
            }
            try {
                File f=new File(path, name);
                b = BitmapFactory.decodeStream(new FileInputStream(f));


            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
                Log.d("File", "not found");
            }
            WebView tv = null;
            ImageView img = null;
            ImageButton imgBtn = null;

            // Afhankelijk van het type pagina
            if(i == 0) {
                tv = (WebView) haha.findViewById(R.id.webview);
                img=(ImageView)haha.findViewById(R.id.popupImg);
                imgBtn = (ImageButton)haha.findViewById(R.id.closeBtn);
                views.add(haha);
            }
            else if(i == 1)
            {
                tv = (WebView) haha2.findViewById(R.id.webview);
                img=(ImageView)haha2.findViewById(R.id.popupImg);
                imgBtn = (ImageButton)haha2.findViewById(R.id.closeBtn);
                views.add(haha2);
            }
            else if(i == 2)
            {
                tv = (WebView) haha3.findViewById(R.id.webview);
                img=(ImageView)haha3.findViewById(R.id.popupImg);
                imgBtn = (ImageButton)haha3.findViewById(R.id.closeBtn);
                views.add(haha3);
            }
            else if(i == 3)
            {
                tv = (WebView) haha4.findViewById(R.id.webview);
                img=(ImageView)haha4.findViewById(R.id.popupImg);
                imgBtn = (ImageButton)haha4.findViewById(R.id.closeBtn);
                views.add(haha4);
            }
            else if(i == 4){
                tv = (WebView) haha5.findViewById(R.id.webview);
                img=(ImageView)haha5.findViewById(R.id.popupImg);
                imgBtn = (ImageButton)haha5.findViewById(R.id.closeBtn);
                views.add(haha5);
            }
            imgBtn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    alertDialog.dismiss();
                }
            });

            if(type == 1) {
                imgBtn.setImageResource(R.drawable.close_energie);


            }
            else if(type == 2)
            {
                imgBtn.setImageResource(R.drawable.close_water);
            }
            else if(type == 3)
            {
                imgBtn.setImageResource(R.drawable.close_materiaal);
            }
            else if(type == 4)
            {
                imgBtn.setImageResource(R.drawable.close_bio);
            }
            else if(type == 5)
            {
                imgBtn.setImageResource(R.drawable.close_dieren);
            }


            if(b != null) {
                img.setImageBitmap(b);
            }

            tv.loadData(page.getText(), "text/html", HTTP.UTF_8);
            tv.setBackgroundColor(Color.parseColor("#F5F2DC"));
        }

        // Add view
        for(View view: views)
        {
            pagerAdapter.addView(view);
        }
        pagerAdapter.notifyDataSetChanged();

        alertDialog.show();

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

	public void animationStart(GestureAnimation gestureAnimation) {
		if(animator != null) {
			animator.play(gestureAnimation);
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

        // Verplaats de buttons mee
        for(ImageButton imageButton: imageButtons)
        {
            imageButton.setTranslationX(imageButton.getX() - tempX);
            imageButton.setTranslationY(imageButton.getY() - tempY);
        }


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

    public ArrayList<ImageButton> getImages()
    {
        return imageButtons;
    }




}
