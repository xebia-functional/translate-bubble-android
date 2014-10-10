package com.fortysevendeg.translace_bubble.ui.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.*;
import com.fortysevendeg.translace_bubble.R;
import com.fortysevendeg.translace_bubble.events.ChangeTypeFeedback;
import com.fortysevendeg.translace_bubble.events.FakeTranslatedEvent;
import com.fortysevendeg.translace_bubble.managers.ClipManager;
import com.fortysevendeg.translace_bubble.modules.AppModule;
import com.fortysevendeg.translace_bubble.ui.services.BubbleUIService;
import com.fortysevendeg.translace_bubble.ui.commons.BaseActivity;
import com.fortysevendeg.translace_bubble.utils.TypeLanguage;
import com.fortysevendeg.translace_bubble.utils.TypeTranslateUI;
import de.greenrobot.event.EventBus;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TranslateBubbleActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new DefaultPreferences()).commit();

        BubbleUIService.launchIfIsNecessary(this);

    }

    @Override
    protected List<Object> getModules() {
        return Arrays.<Object>asList(new AppModule(this));
    }


    public static class DefaultPreferences extends PreferenceFragment {

        private CheckBoxPreference typeBubble;

        private CheckBoxPreference typeNotification;

        private CheckBoxPreference typeWatch;

        private CheckBoxPreference serviceBing;

        private CheckBoxPreference serviceMyMemory;

        private ListPreference toLanguage;

        private ListPreference fromLanguage;

        private SharedPreferences sharedPreferences;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            findPreference("launchFake").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    EventBus.getDefault().post(new FakeTranslatedEvent());
                    return false;
                }
            });

            serviceBing = (CheckBoxPreference) findPreference("serviceBing");
            serviceBing.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean value = (Boolean) newValue;
                    if (value) {
                        serviceMyMemory.setChecked(false);
                    }
                    return true;
                }
            });

            serviceMyMemory = (CheckBoxPreference) findPreference("serviceMyMemory");
            serviceMyMemory.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean value = (Boolean) newValue;
                    if (value) {
                        serviceBing.setChecked(false);
                    }
                    return true;
                }
            });

            typeBubble = (CheckBoxPreference) findPreference("typeBubble");
            typeBubble.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean value = (Boolean) newValue;
                    if (value) {
                        typeNotification.setChecked(false);
                        typeWatch.setChecked(false);
                    }
                    EventBus.getDefault().post(new ChangeTypeFeedback(TypeTranslateUI.BUBBLE));
                    return true;
                }
            });

            typeNotification = (CheckBoxPreference) findPreference("typeNotification");
            typeNotification.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    boolean value = (Boolean) newValue;
                    if (value) {
                        typeBubble.setChecked(false);
                        typeWatch.setChecked(false);
                    }
                    EventBus.getDefault().post(new ChangeTypeFeedback(TypeTranslateUI.NOTIFICATION));
                    return true;
                }
            });

            typeWatch = (CheckBoxPreference) findPreference("typeWatch");
            typeWatch.setEnabled(false);
//            typeWatch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
//                @Override
//                public boolean onPreferenceChange(Preference preference, Object newValue) {
//                    boolean value = (Boolean) newValue;
//                    if (value) {
//                        typeBubble.setChecked(false);
//                        typeNotification.setChecked(false);
//                    }
//                    return true;
//                }
//            });

            List<CharSequence> languages = TypeLanguage.names(getActivity());
            List<CharSequence> languagesValues = TypeLanguage.names();

            fromLanguage = (ListPreference) findPreference("fromLanguage");
            fromLanguage.setEntries(languages.toArray(new CharSequence[0]));
            fromLanguage.setEntryValues(languagesValues.toArray(new CharSequence[0]));
            fromLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    changeFrom((String) newValue);
                    return true;
                }
            });

            toLanguage = (ListPreference) findPreference("toLanguage");
            toLanguage.setEntries(languages.toArray(new CharSequence[0]));
            toLanguage.setEntryValues(languagesValues.toArray(new CharSequence[0]));
            toLanguage.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    changeTo((String) newValue);
                    return true;
                }
            });

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

            changeFrom(sharedPreferences.getString("fromLanguage", "ENGLISH"));
            changeTo(sharedPreferences.getString("toLanguage", "SPANISH"));

        }

        private void changeTo(String key) {
            String toNameLang = getString(getResources().getIdentifier(key, "string", getActivity().getPackageName()));
            toLanguage.setTitle(getString(R.string.to, toNameLang));
        }

        private void changeFrom(String key) {
            String fromNameLang = getString(getResources().getIdentifier(key, "string", getActivity().getPackageName()));
            fromLanguage.setTitle(getString(R.string.from, fromNameLang));
        }

    }
}
