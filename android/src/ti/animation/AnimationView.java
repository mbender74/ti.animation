/*
  This file was auto-generated by the Titanium Module SDK helper for Android
  Appcelerator Titanium Mobile
  Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
  Licensed under the terms of the Apache Public License
  Please see the LICENSE included with this distribution for details.
 */
package ti.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView.ScaleType;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieCompositionFactory;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieOnCompositionLoadedListener;
import com.airbnb.lottie.TextDelegate;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiMessenger;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.io.TiBaseFile;
import org.appcelerator.titanium.io.TiFileFactory;
import org.appcelerator.titanium.proxy.TiViewProxy;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.view.TiUIView;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class AnimationView extends TiUIView implements LottieOnCompositionLoadedListener {

    private static final String LCAT = "AnimationViewProxy";

    private final LottieAnimationView lottieView;
    private final TiViewProxy proxy;
    private final TextDelegate delegate;
    private KrollFunction callbackReady = null;
    private float initialDuration = 0;
    private ValueAnimator va = null;

    AnimationView(TiViewProxy proxy) {
        super(proxy);

        this.proxy = proxy;
        String packageName = proxy.getActivity().getPackageName();
        Resources resources = proxy.getActivity().getResources();
        View viewWrapper;

        int resId_viewHolder;
        int resId_lottie;

        resId_viewHolder = resources.getIdentifier("layout_lottie", "layout", packageName);
        resId_lottie = resources.getIdentifier("animation_view", "id", packageName);

        LayoutInflater inflater = LayoutInflater.from(proxy.getActivity());
        viewWrapper = inflater.inflate(resId_viewHolder, null);

        lottieView = viewWrapper.findViewById(resId_lottie);
        delegate = new TextDelegate(lottieView);
        setNativeView(viewWrapper);

        setScaleMode(TiConvert.toString(proxy.getProperty("scaleMode")));
        lottieView.addAnimatorUpdateListener(new AnimatorUpdateListener());
        lottieView.addAnimatorListener(new AnimatorListener());
        lottieView.addLottieOnCompositionLoadedListener(this);

        if (TiConvert.toBoolean(proxy.getProperty("disableHardwareAcceleration"))) {
            lottieView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        } else {
            lottieView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        lottieView.enableMergePathsForKitKatAndAbove(TiConvert.toBoolean(proxy.getProperty("mergePath")));
    }

    @Override
    public void processProperties(KrollDict d) {
        super.processProperties(d);

        if (d.containsKey("scaleMode")) {
            setScaleMode(d.getString("scaleMode"));
        }
        if (d.containsKey("disableHardwareAcceleration")) {
            if (d.getBoolean("disableHardwareAcceleration")) {
                lottieView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
            } else {
                lottieView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            }
        }
        if (d.containsKey("loop")) {
            lottieView.setRepeatCount(d.getBoolean("loop") ? LottieDrawable.INFINITE : 0);
        }
        if (d.containsKey("ready")) {
            callbackReady = (KrollFunction) d.get("ready");
        }
        if (d.containsKey("mergePath")) {
            lottieView.enableMergePathsForKitKatAndAbove(d.getBoolean("mergePath"));
        }
        if (d.containsKey("progress")) {
            setProgress(Float.parseFloat(d.getString("progress")));
        }
        if (d.containsKey("speed")) {
            Object speed = d.get("speed");
            if (speed != null) {
                proxy.setProperty("duration", (initialDuration / TiConvert.toFloat(speed)));
            }
        }
        if (d.containsKey("autoStart")) {
            proxy.setProperty("autoStart", d.getBoolean("autoStart"));
        }
        if (d.containsKey("start")) {
            if (d.getBoolean("start")) {
                startAnimation(TiConvert.toInt(proxy.getProperty("startFrame")),
                        TiConvert.toInt(proxy.getProperty("endFrame")));
            }
        }

        if (d.containsKey("file") && !d.getString("file").equals("")) {
            if (TiApplication.isUIThread()) {
                loadFile(d.getString("file"));
            } else {
                TiMessenger.sendBlockingMainMessage(
                        proxy.getMainHandler().obtainMessage(AnimationViewProxy.MSG_LOAD_FILE, d.getString("file")));
            }
        } else if (d.containsKey("json")) {
            loadJson(d.getString("json"));
        }
    }

    @Override
    public void propertyChanged(String key, Object oldValue, Object newValue, KrollProxy proxy) {
        KrollDict d = new KrollDict();
        d.put(key, newValue);
        processProperties(d);
    }

    private void setScaleMode(String smode) {
        // Set scale mode on view
        //
        switch (smode) {
            case "center":
                lottieView.setScaleType(ScaleType.CENTER);
                break;
            case "centerCrop":
                lottieView.setScaleType(ScaleType.CENTER_CROP);
                break;
            case "centerInside":
            default:
                lottieView.setScaleType(ScaleType.CENTER_INSIDE);
                break;
        }
    }

    private void parseJson(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            proxy.setProperty("width", jsonObject.optInt("w", 0));
            proxy.setProperty("height", jsonObject.optInt("h", 0));
        } catch (Exception e) {
            Log.e(LCAT, "Couldn't read width/height");
        }
    }

    private void loadJson(String jsonString) {
        try {
            parseJson(jsonString);
            LottieCompositionFactory.fromJsonStringSync(jsonString, null);
        } catch (Exception e) {
            Log.e(LCAT, "Could not parse JSON string");
        }
    }

    @Override
    public void onCompositionLoaded(LottieComposition composition) {
        lottieView.setComposition(composition);
        lottieView.setImageAssetsFolder("Resources/" + TiConvert.toString(proxy.getProperty("assetFolder")));
        lottieView.setTextDelegate(delegate);

        initialDuration = lottieView.getDuration();
        if (TiConvert.toFloat(proxy.getProperty("speed")) == 1.0f) {
            proxy.setProperty("duration", initialDuration);
        } else {
            proxy.setProperty("duration", (initialDuration / TiConvert.toFloat(proxy.getProperty("speed"))));
        }
        if (TiConvert.toBoolean(proxy.getProperty("loop"))) {
            lottieView.setRepeatCount(LottieDrawable.INFINITE);
        }
        if (TiConvert.toBoolean(proxy.getProperty("autoStart"))) {
            startAnimation(TiConvert.toInt(proxy.getProperty("startFrame")),
                    TiConvert.toInt(proxy.getProperty("endFrame")));
        }
        if (callbackReady != null) {
            callbackReady.call(proxy.getKrollObject(), new KrollDict());
        }
        ((AnimationViewProxy) proxy).readyEvent(new KrollDict());
    }

    void loadFile(String f) {
        String url = proxy.resolveUrl(null, f);
        TiBaseFile file = TiFileFactory.createTitaniumFile(new String[]{url}, false);

        if (file.exists()) {
            try {
                InputStream stream = file.getInputStream();
                int size = stream.available();
                byte[] buffer = new byte[size];
                stream.read(buffer);
                String json = new String(buffer, StandardCharsets.UTF_8);
                parseJson(json);
                lottieView.setAnimation(url.replaceAll("file:///android_asset/", ""));
            } catch (Exception e) {
                Log.e(LCAT, "Error opening file " + file.name());
            }
        } else {
            Log.e(LCAT, "File " + file.name() + " not found!");
        }
    }

    void startAnimation(int startFrame, int endFrame) {
        lottieView.cancelAnimation();
        lottieView.setProgress(0f);
        proxy.setProperty("paused", false);

        if (startFrame == -1 && TiConvert.toInt(proxy.getProperty("startFrame"), -1) != -1) {
            startFrame = TiConvert.toInt(proxy.getProperty("startFrame"), -1);
        }

        if (endFrame == -1 && TiConvert.toInt(proxy.getProperty("endFrame"), -1) != -1) {
            endFrame = TiConvert.toInt(proxy.getProperty("endFrame"), -1);
        }

        if (TiConvert.toFloat(proxy.getProperty("speed")) == 1.0f) {
            if (startFrame != -1) {
                lottieView.setMinFrame(startFrame);
            }
            if (endFrame != -1) {
                lottieView.setMaxFrame(endFrame);
            }
            lottieView.playAnimation();
            va = null;
        } else {
            va = ValueAnimator.ofFloat(0f, 1f);
            va.setDuration((long) TiConvert.toFloat(proxy.getProperty("duration")));

            if (TiConvert.toBoolean(proxy.getProperty("loop"))) {
                va.setRepeatCount(-1);
            }
            va.addUpdateListener(animation -> {
                lottieView.setProgress((Float) animation.getAnimatedValue());
                KrollDict event = new KrollDict();
                event.put("frame", lottieView.getFrame());
                event.put("status", AnimationViewProxy.ANIMATION_RUNNING);
                ((AnimationViewProxy) proxy).updateEvent(event);
            });

            va.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    KrollDict event = new KrollDict();
                    event.put("status", AnimationViewProxy.ANIMATION_END);
                    event.put("loop", TiConvert.toBoolean(proxy.getProperty("loop")));
                    ((AnimationViewProxy) proxy).completeEvent(event);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    KrollDict event = new KrollDict();
                    event.put("status", AnimationViewProxy.ANIMATION_END);
                    event.put("loop", TiConvert.toBoolean(proxy.getProperty("loop")));
                    ((AnimationViewProxy) proxy).completeEvent(event);
                }
            });
            va.start();
        }
    }

    void pauseAnimation() {
        proxy.setProperty("paused", true);

        if (va != null) {
            va.pause();
        } else {
            lottieView.pauseAnimation();
        }
    }

    void resumeAnimation() {
        proxy.setProperty("paused", false);
        if (va != null) {
            va.resume();
        } else {
            lottieView.resumeAnimation();
        }
    }

    void stopAnimation() {
        proxy.setProperty("paused", false);
        if (va != null) {
            va.cancel();
        } else {
            lottieView.cancelAnimation();
        }
    }

    void setText(String layer, String text) {
        delegate.setText(layer, text);
    }

    float getProgress() {
        return lottieView.getProgress();
    }

    void setProgress(float val) {
        lottieView.setProgress(val);
    }

    int getFrame() {
        return lottieView.getFrame();
    }

    void setFrame(int val) {
        lottieView.setFrame(val);
    }

    protected class AnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        public void onAnimationUpdate(ValueAnimator animation) {
            KrollDict event = new KrollDict();
            event.put("frame", lottieView.getFrame());
            event.put("status", AnimationViewProxy.ANIMATION_RUNNING);
            ((AnimationViewProxy) proxy).updateEvent(event);
        }
    }

    protected class AnimatorListener implements Animator.AnimatorListener {
        public void onAnimationStart(Animator animation) {
            KrollDict event = new KrollDict();
            event.put("status", AnimationViewProxy.ANIMATION_START);
            ((AnimationViewProxy) proxy).updateEvent(event);
        }

        public void onAnimationEnd(Animator animation) {
            KrollDict event = new KrollDict();
            event.put("status", AnimationViewProxy.ANIMATION_END);
            event.put("loop", TiConvert.toBoolean(proxy.getProperty("loop")));
            ((AnimationViewProxy) proxy).completeEvent(event);
        }

        public void onAnimationCancel(Animator animation) {
            KrollDict event = new KrollDict();
            event.put("status", AnimationViewProxy.ANIMATION_CANCEL);
            ((AnimationViewProxy) proxy).updateEvent(event);
        }

        public void onAnimationRepeat(Animator animation) {
            KrollDict event = new KrollDict();
            event.put("status", AnimationViewProxy.ANIMATION_END);
            event.put("loop", TiConvert.toBoolean(proxy.getProperty("loop")));
            ((AnimationViewProxy) proxy).completeEvent(event);
        }
    }
}
