package com.loror.mvvm.utls;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.loror.lororUtil.image.ImageUtil;
import com.loror.lororUtil.image.ImageUtilCallBack;
import com.loror.lororUtil.image.ReadImageResult;

import java.util.ArrayList;
import java.util.List;

/**
 * html加载解析图片
 */
public class WebImageGetter implements Html.ImageGetter {

    public interface SaveImageListener {
        void saveImage(String path);
    }

    private final Activity mContext;
    private final TextView mTextView;
    private final boolean fillScreen;
    private final SparseArray<String> paths = new SparseArray<>();
    private final SparseIntArray imageHeights = new SparseIntArray();
    private int index;
    private int widthLimit;

    public WebImageGetter(Activity context, TextView textView, boolean fillScreen) {
        this.mContext = context;
        this.mTextView = textView;
        this.fillScreen = fillScreen;
    }

    public CharSequence html(String html) {
        return Html.fromHtml(html, this, (opening, tag, output, xmlReader) -> {

        });
    }

    public void setWidthLimit(int widthLimit) {
        this.widthLimit = widthLimit;
    }

    public int getWidthLimit() {
        return widthLimit;
    }

    public List<String> getPaths() {
        try {
            int size = this.paths.size();
            List<String> paths = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                paths.add(this.paths.get(i));
            }
            return paths;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public List<Integer> getImageHeights() {
        try {
            int size = this.imageHeights.size();
            List<Integer> height = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                height.add(this.imageHeights.get(i));
            }
            return height;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /**
     * 获取屏幕宽度像素
     */
    protected int getScreenWidth() {
        WindowManager manager = mContext.getWindowManager();
        DisplayMetrics outMetrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    @Override
    public Drawable getDrawable(String source) {
        if (!source.startsWith("http")) {
            source = "http:" + source;
        }
        final int finalIndex = index;
        index++;
        LevelListDrawable drawable = new LevelListDrawable();
        if (widthLimit == 0) {
            widthLimit = (int) (getScreenWidth() * 2.0 / 3);
        }
        ImageUtil.with(mContext).from(source).setWidthLimit(widthLimit).setAutoRotateIsDegree(true)
                .setOnLoadListener(new ImageUtilCallBack() {
                    @Override
                    public void onStart(ImageView imageView) {

                    }

                    @Override
                    public void onLoadCach(ImageView imageView, ReadImageResult result) {
                        onFinish(imageView, result);
                    }

                    @Override
                    public void onFinish(ImageView imageView, ReadImageResult result) {
                        if (result.getBitmap() != null) {
                            Bitmap bitmap = result.getBitmap();
                            int height;
                            if (!fillScreen && bitmap.getWidth() < widthLimit) {
                                drawable.setBounds(0, 0, bitmap.getWidth(), height = bitmap.getHeight());
                            } else {
                                double bl = bitmap.getHeight() * 1.0 / bitmap.getWidth();
                                drawable.setBounds(0, 0, widthLimit, height = (int) (widthLimit * bl));
                            }
                            drawable.addLevel(1, 1, new BitmapDrawable(bitmap));
                            drawable.setLevel(1);
                            mTextView.setText(mTextView.getText());
                            mTextView.refreshDrawableState();
                            imageHeights.put(finalIndex, height);
                            paths.put(finalIndex, result.getPath());
                        }
                    }

                    @Override
                    public void onFailed(ImageView imageView, ReadImageResult result) {

                    }
                })
                .loadImage();
        return drawable;
    }

    //长按保存图片
    public void canLongSave(SaveImageListener saveImageListener) {
        float[] location = new float[2];
        Handler handler = new Handler();
        Runnable runnable = () -> {
            float top = location[1];
            int total = 0;
            List<Integer> heights = this.getImageHeights();
            for (int i = 0; i < heights.size(); i++) {
                total += heights.get(i);
                if (total >= top) {
                    if (saveImageListener != null) {
                        saveImageListener.saveImage(getPaths().get(i));
                    }
                    Log.e("TOP___", i + " " + total);
                    Log.e("TOP___", top + " ");
                    return;
                }
            }
            Log.e("TOP___", "no " + total);
            Log.e("TOP___", top + " ");
        };
        mTextView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    location[0] = event.getX();
                    location[1] = event.getY();
                    handler.postDelayed(runnable, 1200);
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (Math.abs(event.getX() - location[0]) > 50 || Math.abs(event.getY() - location[1]) > 50) {
                        handler.removeCallbacks(runnable);
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                case MotionEvent.ACTION_UP:
                    handler.removeCallbacks(runnable);
                    break;
            }
            return true;
        });
    }
}
