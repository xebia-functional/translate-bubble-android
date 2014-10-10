package com.fortysevendeg.translace_bubble.modules;

import android.content.Context;
import android.util.Log;
import com.fortysevendeg.translace_bubble.ui.services.BubbleUIService;
import com.fortysevendeg.translace_bubble.ui.activities.TranslateBubbleActivity;
import com.fortysevendeg.translace_bubble.managers.ClipManager;
import com.fortysevendeg.translace_bubble.managers.LaunchNotificationManager;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module(
        injects = {
                BubbleUIService.class,
                TranslateBubbleActivity.class
        }
)
public class AppModule {

    private final JobManager jobManager;

    private final ClipManager clipManager;

    private final LaunchNotificationManager launchNotificationManager;

    public AppModule(Context context) {
        Configuration configuration = new Configuration.Builder(context)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";
                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120)//wait 2 minute
                .build();
        jobManager = new JobManager(context, configuration);
        clipManager = new ClipManager(context);
        launchNotificationManager = new LaunchNotificationManager(context);
    }

    @Provides
    @Singleton
    public JobManager provideJobManager() {
        return jobManager;
    }

    @Provides
    @Singleton
    public ClipManager provideClipManager() {
        return clipManager;
    }

    @Provides
    @Singleton
    public LaunchNotificationManager provideLaunchNotificationManager() {
        return launchNotificationManager;
    }
}
