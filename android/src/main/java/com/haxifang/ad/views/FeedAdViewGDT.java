package com.haxifang.ad.views;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import com.haxifang.R;
import com.haxifang.ad.AdBoss;
import com.haxifang.ad.utils.DislikeDialog;
import com.haxifang.ad.utils.Utils;
import com.qq.e.ads.cfg.VideoOption;
import com.qq.e.ads.nativ.ADSize;
import com.qq.e.ads.nativ.NativeExpressAD;
import com.qq.e.ads.nativ.NativeExpressADView;
import com.qq.e.ads.nativ.NativeExpressMediaListener;
import com.qq.e.comm.constants.AdPatternType;
import com.qq.e.comm.pi.AdData;
import com.qq.e.comm.util.AdError;

import java.util.List;

import static com.facebook.react.bridge.UiThreadUtil.runOnUiThread;

public class FeedAdViewGDT extends RelativeLayout {

    private static final String TAG = "FeedAdGDT";

    private Activity mContext;
    private ReactContext reactContext;
    private String _codeid = "";
    //private AdSlot adSlot;

    private NativeExpressAD nativeExpressAD;
    private NativeExpressADView nativeExpressADView;

    private int _expectedWidth = 0;
    private int _expectedHeight = 0; // 高度0 自适应
    final private long startTime = 0;
    private boolean mHasShowDownloadActive = false;


    public FeedAdViewGDT(ReactContext context) {
        super(context);
        mContext = context.getCurrentActivity();
		reactContext = context;
		//开始展开
		inflate(context, R.layout.feed_view, this);

        // 这个函数很关键，不然不能触发再次渲染，让 view 在 RN 里渲染成功!!
        Utils.setupLayoutHack(this);
	}
	
    public void setWidth(int width) {
        Log.d(TAG, "setCodeId = " + _codeid + ", setWidth:" + width);
		_expectedWidth = width;
		
        showAd();
    }

    public void setCodeId(String codeId) {
        Log.d(TAG, "setCodeId: " + codeId + ", _expectedWidth:" + _expectedWidth);
        _codeid = codeId;
        showAd();
    }

    public void showAd() {
		Log.d(TAG, "showAd: width:" + _expectedWidth + " codeid:" + _codeid);

        // 显示广告
        if (_expectedWidth == 0 || _codeid.isEmpty()) {
            // 广告宽度未设置或 code id 未设置，停止显示广告
            return;
        }
        // 信息流广告原来不能提前预加载，很容易出现超时，必须当场加载
        // sdk里很容易出现 message send to dead thread ... 肯定有些资源线程依赖！
        runOnUiThread(() -> {
            //loadTTFeedAd();
            refreshAd();
        });
    }

    // 显示头条的信息流广告
    /*
    public void loadTTFeedAd() {
        if(AdBoss.TTAdSdk == null) {
            return;
        }

        // 创建广告请求参数AdSlot,具体参数含义参考文档 modules.add(new Interaction(reactContext));
        adSlot = new AdSlot.Builder().setCodeId(_codeid) // 广告位id
                .setSupportDeepLink(true).setAdCount(1) // 请求广告数量为1到3条
                .setExpressViewAcceptedSize(_expectedWidth, _expectedHeight) // 期望模板广告view的size,单位dp,高度0自适应
                .setImageAcceptedSize(640, 320)
                // 新版本，不设置AdType 也没差量无效了...
                .setNativeAdType(AdSlot.TYPE_INTERACTION_AD).build();

        // 请求广告，对请求回调的广告作渲染处理
        final FeedAdView _this = this;
        AdBoss.TTAdSdk.loadNativeExpressAd(adSlot, new TTAdNative.NativeExpressAdListener() {
            @Override
            public void onError(int code, String message) {
                message = "错误结果 loadNativeExpressAd onAdError: " + code + ", " + message;
                // TToast.show(getContext(), message);
                Log.d(TAG, message);
                _this.onAdError(message);
            }

            @Override
            public void onNativeExpressAdLoad(List<TTNativeExpressAd> ads) {
                Log.d(TAG, "onNativeExpressAdLoad: !!!");
                if (ads == null || ads.size() == 0) {
                    _this.onAdError("加载成功无广告内容");
                    return;
                }

                TTNativeExpressAd ad = ads.get(0);
                // 缓存加载成功的广告
                // AdBoss.feedAd = ad;
                _showTTAd(ad);
            }
        });
    }
    */

      // 1.加载广告，先设置加载上下文环境和条件
    private void refreshAd() {
       nativeExpressAD = new NativeExpressAD(mContext, new ADSize(340, ADSize.AUTO_HEIGHT), _codeid, new NativeExpressAD.NativeExpressADListener() {
          @Override
          public void onNoAD(AdError error) {
            Log.i("AD_DEMO", String.format("onADError, error code: %d, error msg: %s", error.getErrorCode(), error.getErrorMsg()));
          }

          @Override
          public void onADLoaded(List<NativeExpressADView> adList) {
            Log.i(TAG, "onADLoaded: " + adList.size());
            // 释放前一个 NativeExpressADView 的资源
            if (nativeExpressADView != null) {
              nativeExpressADView.destroy();
            }
            // 3.返回数据后，SDK 会返回可以用于展示 NativeExpressADView 列表
            nativeExpressADView = adList.get(0);
            if (nativeExpressADView.getBoundData().getAdPatternType() == AdPatternType.NATIVE_VIDEO) {
              nativeExpressADView.setMediaListener(new NativeExpressMediaListener() {
                  @Override
                  public void onVideoInit(NativeExpressADView nativeExpressADView) {
                  }

                  @Override
                  public void onVideoLoading(NativeExpressADView nativeExpressADView) {
                      Log.i(TAG, "onVideoLoading");
                  }

                  @Override
                  public void onVideoReady(NativeExpressADView nativeExpressADView, long l) {
                      Log.i(TAG, "onVideoReady");
                  }

                  @Override
                  public void onVideoStart(NativeExpressADView nativeExpressADView) {

                  }

                  @Override
                  public void onVideoPause(NativeExpressADView nativeExpressADView) {
                  }

                  @Override
                  public void onVideoCached(NativeExpressADView nativeExpressADView) {

                  }

                  @Override
                  public void onVideoComplete(NativeExpressADView nativeExpressADView) {
                  }

                  @Override
                  public void onVideoError(NativeExpressADView nativeExpressADView, AdError adError) {
                      Log.i(TAG, "onVideoError");
                  }

                  @Override
                  public void onVideoPageOpen(NativeExpressADView nativeExpressADView) {
                      Log.i(TAG, "onVideoPageOpen");
                  }

                  @Override
                  public void onVideoPageClose(NativeExpressADView nativeExpressADView) {
                      Log.i(TAG, "onVideoPageClose");
                  }
              } );
            }
            nativeExpressADView.render();

            View.OnLayoutChangeListener layoutListener = new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    // Make changes
                    float density  = getResources().getDisplayMetrics().density;

                    Log.d(TAG,
                            "onAdLayoutChange: "
                                    + left + "," + top + "," + right + "," + bottom + "," + density
                    );

                    int width = (int)((right - left)/density);
                    int height = (int)((bottom - top)/density);
                    if (width > 10 && height > 10) {
                        onAdLayout(width, height);
                        nativeExpressADView.removeOnLayoutChangeListener(this);
                    }
                }
            };
            nativeExpressADView.addOnLayoutChangeListener(layoutListener);
            // 需要保证 View 被绘制的时候是可见的，否则将无法产生曝光和收益。
            //container.addView(nativeExpressADView);
          }

          @Override
          public void onRenderFail(NativeExpressADView adView) {
            Log.i(TAG, "onRenderFail");
          }

          @Override
          public void onRenderSuccess(NativeExpressADView adView) {
            Log.i(TAG, "onRenderSuccess");
            RelativeLayout mExpressContainer = findViewById(R.id.feed_container);
            if(mExpressContainer!= null) {
                if (mExpressContainer.getChildCount() > 0) {
                    mExpressContainer.removeAllViews();
                }
                mExpressContainer.addView(adView);
                adView.measure(0, 0);
                Log.d(TAG,
                        "onAdLayout: "
                                + mExpressContainer.getWidth() + ", " + mExpressContainer.getHeight() + ','
                                + mExpressContainer.getMeasuredWidth() + ", " + mExpressContainer.getMeasuredHeight() + ','
                                + adView.getWidth() + ", " + adView.getHeight() + ','
                                + adView.getMeasuredWidth() + ", " + adView.getMeasuredHeight() + ','

                );

            }
            /*
            onAdLayout(
                    adView.getMeasuredWidth(),
                    adView.getMeasuredHeight()
            );*/
          }

          @Override
          public void onADExposure(NativeExpressADView adView) {
            Log.i(TAG, "onADExposure");
          }

          @Override
          public void onADClicked(NativeExpressADView adView) {
            Log.i(TAG, "onADClicked");
          }

          @Override
          public void onADClosed(NativeExpressADView adView) {
            Log.i(TAG, "onADClosed");
          }

          @Override
          public void onADLeftApplication(NativeExpressADView adView) {
            Log.i(TAG, "onADLeftApplication");
          }

          @Override
          public void onADOpenOverlay(NativeExpressADView adView) {
            Log.i(TAG, "onADOpenOverlay");
          }

          @Override
          public void onADCloseOverlay(NativeExpressADView adView) {
            Log.i(TAG, "onADCloseOverlay");
         }

          public void onDestroy() {
            // 4.使用完了每一个 NativeExpressADView 之后都要释放掉资源
            if (nativeExpressADView != null) {
              nativeExpressADView.destroy();
            }
          }
      }); // 传入Activity

      // 注意：如果您在平台上新建平台模板广告位时，选择了支持视频，那么可以进行个性化设置（可选）
      nativeExpressAD.setVideoOption(new VideoOption.Builder()
        .setAutoPlayPolicy(VideoOption.AutoPlayPolicy.WIFI) // WIFI 环境下可以自动播放视频
        .setAutoPlayMuted(true) // 自动播放时为静音
        .build()); //

      /**
      * 如果广告位支持视频广告，强烈建议在调用loadData请求广告前调用setVideoPlayPolicy，有助于提高视频广告的eCPM值 <br/>
      * 如果广告位仅支持图文广告，则无需调用
      */

      /**
      * 设置本次拉取的视频广告，从用户角度看到的视频播放策略<p/>
      *
      * "用户角度"特指用户看到的情况，并非SDK是否自动播放，与自动播放策略AutoPlayPolicy的取值并非一一对应 <br/>
      *
      * 如自动播放策略为AutoPlayPolicy.WIFI，但此时用户网络为4G环境，在用户看来就是手工播放的
      */
      nativeExpressAD.setVideoPlayPolicy(VideoOption.VideoPlayPolicy.AUTO); // 本次拉回的视频广告，从用户的角度看是自动播放的

      nativeExpressAD.loadAD(1);
    }

    // 显示广告
    /*
    private void _showTTAd(final TTNativeExpressAd ad) {
        mContext.runOnUiThread(() -> {
            bindAdListener(ad);
            ad.render();
        });
    }*/


    // 外部事件..
    public void onAdError(String message) {
        WritableMap event = Arguments.createMap();
        event.putString("message", message);
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onAdError", event);
    }

    public void onAdClick() {
        WritableMap event = Arguments.createMap();
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onAdClick", event);
    }

    public void onAdClose(String reason) {
        Log.d(TAG, "onAdClose: " + reason);
        WritableMap event = Arguments.createMap();
        event.putString("reason", reason);
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onAdClose", event);
    }

    public void onAdLayout(int width, int height) {
        Log.d(TAG, "onAdLayout: " + width + ", " + height);
        WritableMap event = Arguments.createMap();
        event.putInt("width", width);
        event.putInt("height", height);
        reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(getId(), "onAdLayout", event);
    }

}
