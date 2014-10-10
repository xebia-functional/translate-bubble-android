package com.fortysevendeg.translace_bubble.jobs;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.fortysevendeg.translace_bubble.events.TranslatedEvent;
import com.fortysevendeg.translace_bubble.services.translate.TranslateService;
import com.fortysevendeg.translace_bubble.services.translate.impl.BingTranslateImpl;
import com.fortysevendeg.translace_bubble.services.translate.impl.MyMemoryTranslateImpl;
import com.fortysevendeg.translace_bubble.utils.TypeLanguage;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;
import de.greenrobot.event.EventBus;

public class TranslateJob extends Job {

    public static final int PRIORITY = 1;

    private String translatedText;

    private TranslateService translateService;

    private String text;

    private TypeLanguage to, from;

    public TranslateJob(Context context, String text) {
        super(new Params(PRIORITY).requireNetwork().persist());
        this.text = text;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.getBoolean("serviceBing", true)) {
            translateService = new BingTranslateImpl();
        } else {
            translateService = new MyMemoryTranslateImpl();
        }
        from = TypeLanguage.valueOf(sharedPreferences.getString("fromLanguage", "ENGLISH"));
        to = TypeLanguage.valueOf(sharedPreferences.getString("toLanguage", "SPANISH"));
    }

    @Override
    public void onAdded() {
        translatedText = translateService.translate(text, from, to);
        EventBus.getDefault().post(new TranslatedEvent(text, translatedText));
    }

    @Override
    public void onRun() throws Throwable {

    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

}
