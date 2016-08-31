package com.ghondar.gplayer;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.HashMap;
import java.util.Map;

import tcking.github.com.giraffeplayer.GiraffePlayer;
import tcking.github.com.giraffeplayer.GiraffePlayerActivity;
import tcking.github.com.giraffeplayer.GiraffePlayerActivity.Config;
import tv.danmaku.ijk.media.player.IMediaPlayer;

/**
 * Created by ghondar on 8/31/16.
 */

public class GPlayer extends ReactContextBaseJavaModule {

    public static final String SCALETYPE_FITPARENT="SCALETYPE_FITPARENT";
    public static final String SCALETYPE_FILLPARENT="SCALETYPE_FILLPARENT";
    public static final String SCALETYPE_WRAPCONTENT="SCALETYPE_WRAPCONTENT";
    public static final String SCALETYPE_FITXY="SCALETYPE_FITXY";
    public static final String SCALETYPE_16_9="SCALETYPE_16_9";
    public static final String SCALETYPE_4_3="SCALETYPE_4_3";

    private GiraffePlayer gplayer;
    private ReactApplicationContext context;
    private Config config;

    public GPlayer(ReactApplicationContext reactContext) {
        super(reactContext);
        this.context = reactContext;
        config = GiraffePlayerActivity.configPlayer(reactContext);
    }

    private void sendEvent(String eventName,
                           @Nullable WritableMap params) {
        this.context
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @Override
    public String getName() {
        return "GPlayerAndroid";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(SCALETYPE_FITPARENT, "fitParent");
        constants.put(SCALETYPE_FILLPARENT, "fillParent");
        constants.put(SCALETYPE_WRAPCONTENT, "wrapContent");
        constants.put(SCALETYPE_FITXY, "fitXY");
        constants.put(SCALETYPE_16_9, "16:9");
        constants.put(SCALETYPE_4_3, "14:3");
        return constants;
    }

    @ReactMethod
    public void setScaletype(String SCALE_TYPE) {
        config.setScaleType(SCALE_TYPE);
    }

    @ReactMethod
    public void setFullScreenOnly(Boolean path) {
        config.setFullScreenOnly(path);
    }

    @ReactMethod
    public void setTitle(String title) {
        config.setTitle(title);
    }

    @ReactMethod
    public void setShowNavIcon(Boolean showNavIcon) {
        config.setShowNavIcon(showNavIcon);
    }

    @ReactMethod
    public void setDefaultRetryTime(Long defaultRetryTime) {
        config.setDefaultRetryTime(defaultRetryTime);
    }

    @ReactMethod
    public void play(String path) {
        config.onPlayer(new GiraffePlayerActivity.OnPlayerListener() {
            @Override
            public void onPlayer(GiraffePlayer player) {
                player.onInfo(new GiraffePlayer.OnInfoListener() {
                    @Override
                    public void onInfo(int what, int extra) {
                        WritableMap params = Arguments.createMap();
                        switch(what) {
                            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                                sendEvent("onBufferingStart", params);
                                break;
                            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                                sendEvent("onBufferingEnd", params);
                                break;
                            case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                                //download speed
                                params.putInt("speed", extra);
                                sendEvent("onNetworkBandwidth", params);
                                break;
                            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                                sendEvent("onRenderingStart", params);
                                break;
                        }
                    }
                }).onControlPanelVisibilityChange(new GiraffePlayer.OnControlPanelVisibilityChangeListener() {

                    @Override
                    public void change(boolean isShowing) {
                        WritableMap params = Arguments.createMap();
                        params.putBoolean("show", isShowing);
                        sendEvent("onControlPanelVisibilityChange", params);
                    }
                }).onError(new GiraffePlayer.OnErrorListener() {

                    @Override
                    public void onError(int what, int extra) {
                        WritableMap params = Arguments.createMap();
                        params.putString("Error", "video play error");
                        sendEvent("onError", params);
                    }
                }).onComplete(new Runnable() {

                    @Override
                    public void run() {
                        WritableMap params = Arguments.createMap();
                        sendEvent("onComplete", params);
                    }
                });
                gplayer = player;
            }
        });
        config.play(path);
    }

    @ReactMethod
    public void toggleAspectRatio() {
        if(gplayer != null)
            gplayer.toggleAspectRatio();
    }

    @ReactMethod
    public void seekTo(int msec, Boolean showControlPanel) {
        if(gplayer != null)
            gplayer.seekTo(msec, showControlPanel);
    }

    @ReactMethod
    public void forward(Float percent) {
        if(gplayer != null)
            gplayer.forward(percent);
    }

    @ReactMethod
    public void backward(Float percent) {
        if(gplayer != null)
            gplayer.forward(-percent);
    }

    @ReactMethod
    public void getCurrentPosition(Promise promise) {
        if(gplayer != null)
            promise.resolve(gplayer.getCurrentPosition());
        else
            promise.reject("Player not initialized.");
    }

    @ReactMethod
    public void getDuration(Promise promise) {
        if(gplayer != null)
            promise.resolve(gplayer.getDuration());
        else
            promise.reject("Player not initialized.");
    }

    @ReactMethod
    public void stop() {
        if(gplayer != null)
            gplayer.stop();
    }

    @ReactMethod
    public void pause() {
        if(gplayer != null)
            gplayer.pause();
    }

    @ReactMethod
    public void start() {
        if(gplayer != null)
            gplayer.start();
    }
}
