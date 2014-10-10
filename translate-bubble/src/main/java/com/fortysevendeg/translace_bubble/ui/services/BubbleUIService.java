package com.fortysevendeg.translace_bubble.ui.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.util.Log;
import android.view.*;
import com.fortysevendeg.translace_bubble.R;
import com.fortysevendeg.translace_bubble.events.ChangeTypeFeedback;
import com.fortysevendeg.translace_bubble.events.FakeTranslatedEvent;
import com.fortysevendeg.translace_bubble.ui.commons.BaseService;
import com.fortysevendeg.translace_bubble.events.TranslatedEvent;
import com.fortysevendeg.translace_bubble.jobs.TranslateJob;
import com.fortysevendeg.translace_bubble.managers.LaunchNotificationManager;
import com.fortysevendeg.translace_bubble.modules.AppModule;
import com.fortysevendeg.translace_bubble.managers.ClipManager;
import com.fortysevendeg.translace_bubble.ui.components.BubbleView;
import com.fortysevendeg.translace_bubble.ui.components.CloseView;
import com.fortysevendeg.translace_bubble.ui.components.ContentView;
import com.fortysevendeg.translace_bubble.utils.TypeTranslateUI;
import com.path.android.jobqueue.JobManager;
import de.greenrobot.event.EventBus;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public class BubbleUIService extends BaseService {

    enum BubbleStatus {
        FLOATING, CONTENT
    }

    private WindowManager windowManager;

    private BubbleView bubble;

    private ContentView contentView;

    private CloseView closeView;

    private WindowManager.LayoutParams paramsBubble;

    private WindowManager.LayoutParams paramsContentView;

    private int width;

    private int height;

    private TypeTranslateUI typeTranslateUI = TypeTranslateUI.BUBBLE;

    private BubbleStatus bubbleStatus = BubbleStatus.FLOATING;

    @Inject
    JobManager jobManager;

    @Inject
    ClipManager clipManager;

    @Inject
    LaunchNotificationManager launchNotificationManager;

    private ClipboardManager.OnPrimaryClipChangedListener clipChangedListener = new ClipboardManager.OnPrimaryClipChangedListener() {
        @Override
        public void onPrimaryClipChanged() {
            String text = null;
            try {
                text = clipManager.getText().toString();
            } catch (Exception e) {
                text = (String) clipManager.getText();
            }
            if (text != null && !text.isEmpty()) {
                onStartTranslate(text);
            }
        }
    };

    private ContentView.GestureListener gestureListener = new ContentView.GestureListener() {
        @Override
        public void onUp() {
            collapse();
        }

        @Override
        public void onDown() {
            close();
        }

        @Override
        public void onPrevious() {
        }

        @Override
        public void onNext() {
        }
    };

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        private int initialX;
        private int initialY;
        private float initialTouchX;
        private float initialTouchY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    initialX = paramsBubble.x;
                    initialY = paramsBubble.y;
                    initialTouchX = event.getRawX();
                    initialTouchY = event.getRawY();
                    return true;
                case MotionEvent.ACTION_CANCEL:
                    closeView.hide();
                    break;
                case MotionEvent.ACTION_UP:
                    closeView.hide();
                    if (initialX == paramsBubble.x && initialY == paramsBubble.y) {
                        bubbleStatus = BubbleStatus.CONTENT;
                        bubble.hide();
                        contentView.show();
                    } else {
                        bubble.drop(paramsBubble, windowManager);
                    }
                    return true;
                case MotionEvent.ACTION_MOVE:
                    if (!closeView.isVisible()) {
                        closeView.show();
                    }
                    paramsBubble.x = initialX + (int) (event.getRawX() - initialTouchX);
                    paramsBubble.y = initialY + (int) (event.getRawY() - initialTouchY);
                    windowManager.updateViewLayout(bubble, paramsBubble);
                    return true;
            }
            return false;
        }
    };

    public static void launchIfIsNecessary(Context context) {
        Log.d("TranslateService", "launchIfIsNecessary context -> " + context);
        try {
            context.startService(new Intent(context.getApplicationContext(), BubbleUIService.class));
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(getApplicationContext()));
    }

    @Override
    public void onCreate() {
        super.onCreate();

        EventBus.getDefault().register(this);

        clipManager.getClipboard().addPrimaryClipChangedListener(clipChangedListener);

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        closeView = new CloseView(this);
        closeView.hide();

        int heightCloseZone = (int) getResources().getDimension(R.dimen.height_close_zone);

        WindowManager.LayoutParams closeViewParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                heightCloseZone,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        closeViewParams.gravity = Gravity.BOTTOM | Gravity.LEFT;

        windowManager.addView(closeView, closeViewParams);

        bubble = new BubbleView(this);
        bubble.hide();
        bubble.setOnTouchListener(touchListener);

        paramsBubble = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        paramsBubble.x = 0;
        paramsBubble.y = (int) getResources().getDimension(R.dimen.bubble_start_pos_y);
        paramsBubble.gravity = Gravity.TOP | Gravity.LEFT;
        bubble.init(height, width);

        windowManager.addView(bubble, paramsBubble);

        contentView = new ContentView(this);
        contentView.setGestureListener(gestureListener);
        contentView.hide();
        contentView.setListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collapse();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });

        paramsContentView = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        paramsContentView.gravity = Gravity.BOTTOM | Gravity.LEFT;

        windowManager.addView(contentView, paramsContentView);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("typeBubble", false)) {
            typeTranslateUI = TypeTranslateUI.BUBBLE;
        } else if (sharedPreferences.getBoolean("typeNotification", false)) {
            typeTranslateUI = TypeTranslateUI.NOTIFICATION;
        } else if (sharedPreferences.getBoolean("typeWatch", false)) {
            typeTranslateUI = TypeTranslateUI.WATCH;
        }

    }

    private void close() {
        bubbleStatus = BubbleStatus.FLOATING;
        contentView.hide();
        bubble.hide();
    }

    private void collapse() {
        bubbleStatus = BubbleStatus.FLOATING;
        contentView.collapse(paramsContentView, windowManager);
        bubble.show(paramsBubble, windowManager);
        bubble.stopAnimation();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Do your other onStartCommand stuff..
        ensureServiceStaysRunning();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        clipManager.getClipboard().removePrimaryClipChangedListener(clipChangedListener);
        EventBus.getDefault().unregister(this);
        if (bubble != null) windowManager.removeView(bubble);
        if (contentView != null) windowManager.removeView(contentView);
        if (closeView != null) windowManager.removeView(closeView);
    }

    private void ensureServiceStaysRunning() {
        // KitKat appears to have (in some cases) forgotten how to honor START_STICKY
        // and if the service is killed, it doesn't restart.  On an emulator & AOSP device, it restarts...
        // on my CM device, it does not - WTF?  So, we'll make sure it gets back
        // up and running in a minimum of 20 minutes.  We reset our timer on a handler every
        // 2 minutes...but since the handler runs on uptime vs. the alarm which is on realtime,
        // it is entirely possible that the alarm doesn't get reset.  So - we make it a noop,
        // but this will still count against the app as a wakelock when it triggers.  Oh well,
        // it should never cause a device wakeup.  We're also at SDK 19 preferred, so the alarm
        // mgr set algorithm is better on memory consumption which is good.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // A restart intent - this never changes...
            final int restartAlarmInterval = 5 * 60 * 1000;
            final int resetAlarmTimer = 2 * 60 * 1000;
            final Intent restartIntent = new Intent(this, BubbleUIService.class);
            restartIntent.putExtra("ALARM_RESTART_SERVICE_DIED", true);
            final AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Handler restartServiceHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    // Create a pending intent
                    PendingIntent pintent = PendingIntent.getService(getApplicationContext(), 0, restartIntent, 0);
                    alarmMgr.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + restartAlarmInterval, pintent);
                    sendEmptyMessageDelayed(0, resetAlarmTimer);
                }
            };
            restartServiceHandler.sendEmptyMessageDelayed(0, 0);
        }
    }

    private void onStartTranslate(String text) {
        if (typeTranslateUI.equals(TypeTranslateUI.BUBBLE)) {
            if (bubbleStatus.equals(BubbleStatus.FLOATING)) {
                bubble.show(paramsBubble, windowManager);
            } else {
                contentView.setTexts(getString(R.string.translating), "");
            }
        } else if (typeTranslateUI.equals(TypeTranslateUI.NOTIFICATION)) {
            launchNotificationManager.translating();
        }
        jobManager.addJobInBackground(new TranslateJob(getApplicationContext(), text));
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(TranslatedEvent translatedEvent) {
        if (translatedEvent.getTranslated().isEmpty()) {
            translatedFailed();
        } else {
            if (typeTranslateUI.equals(TypeTranslateUI.BUBBLE)) {
                contentView.setTexts(translatedEvent.getOriginal(), translatedEvent.getTranslated());
                if (bubbleStatus.equals(BubbleStatus.FLOATING)) {
                    bubble.stopAnimation();
                }
            } else if (typeTranslateUI.equals(TypeTranslateUI.NOTIFICATION)) {
                launchNotificationManager.launch(translatedEvent.getOriginal(), translatedEvent.getTranslated());
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                   clipManager.reset();
                }
            }, 500);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(FakeTranslatedEvent fakeTranslatedEvent) {
        onStartTranslate(fakeTranslatedEvent.getSample());
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(ChangeTypeFeedback changeTypeFeedback) {
        typeTranslateUI = changeTypeFeedback.getType();
    }

    private void translatedFailed() {
        if (typeTranslateUI.equals(TypeTranslateUI.BUBBLE)) {
            contentView.setTexts(getString(R.string.failedTitle), getString(R.string.failedMessage));
            if (bubbleStatus.equals(BubbleStatus.FLOATING)) {
                bubble.stopAnimation();
            }
        } else if (typeTranslateUI.equals(TypeTranslateUI.NOTIFICATION)) {
            launchNotificationManager.failed();
        }
    }


}
