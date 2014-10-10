package com.fortysevendeg.translace_bubble.utils;

import android.content.Context;
import com.memetix.mst.language.Language;

import java.util.ArrayList;
import java.util.List;

public enum TypeLanguage {
    AUTO_DETECT,
    ARABIC,
    BULGARIAN,
    CATALAN,
    CHINESE_SIMPLIFIED,
    CHINESE_TRADITIONAL,
    CZECH,
    DANISH,
    DUTCH,
    ENGLISH,
    ESTONIAN,
    FINNISH,
    FRENCH,
    GERMAN,
    GREEK,
    HAITIAN_CREOLE,
    HEBREW,
    HINDI,
    HMONG_DAW,
    HUNGARIAN,
    INDONESIAN,
    ITALIAN,
    JAPANESE,
    KOREAN,
    LATVIAN,
    LITHUANIAN,
    MALAY,
    NORWEGIAN,
    PERSIAN,
    POLISH,
    PORTUGUESE,
    ROMANIAN,
    RUSSIAN,
    SLOVAK,
    SLOVENIAN,
    SPANISH,
    SWEDISH,
    THAI,
    TURKISH,
    UKRAINIAN,
    URDU,
    VIETNAMESE;

    public static List<CharSequence> names() {
        List<CharSequence> values = new ArrayList<CharSequence>();
        for (TypeLanguage value : values()) {
            values.add(value.name());
        }
        return values;
    }

    public static List<CharSequence> names(Context context) {
        List<CharSequence> values = new ArrayList<CharSequence>();
        for (TypeLanguage value : values()) {
            values.add(context.getString(context.getResources().getIdentifier(value.name(), "string", context.getPackageName())));
        }
        return values;
    }

    public String toMyMemory() {
        if (equals(ARABIC)) {
            return "ar-SA";
        } else if (equals(BULGARIAN)) {
            return "bg-BG";
        } else if (equals(CATALAN)) {
            return "ca-ES";
        } else if (equals(CHINESE_SIMPLIFIED)) {
            return "zh-CN";
        } else if (equals(CHINESE_TRADITIONAL)) {
            return "zh-TW";
        } else if (equals(CZECH)) {
            return "cs";
        } else if (equals(DANISH)) {
            return "da-DK";
        } else if (equals(DUTCH)) {
            return "nl-AN";
        } else if (equals(ENGLISH)) {
            return "en-GB";
        } else if (equals(ESTONIAN)) {
            return "et";
        } else if (equals(FINNISH)) {
            return "fi-FI";
        } else if (equals(FRENCH)) {
            return "fr-FR";
        } else if (equals(GERMAN)) {
            return "de-DE";
        } else if (equals(GREEK)) {
            return "el-GR";
        } else if (equals(HAITIAN_CREOLE)) {
            return "ht";
        } else if (equals(HEBREW)) {
            return "he";
        } else if (equals(HINDI)) {
            return "hi-IN";
        } else if (equals(HMONG_DAW)) {
            return "mww";
        } else if (equals(HUNGARIAN)) {
            return "hu-HU";
        } else if (equals(INDONESIAN)) {
            return "id-ID";
        } else if (equals(ITALIAN)) {
            return "it-IT";
        } else if (equals(JAPANESE)) {
            return "ja-JA";
        } else if (equals(KOREAN)) {
            return "ko-KR";
        } else if (equals(LATVIAN)) {
            return "lv";
        } else if (equals(LITHUANIAN)) {
            return "lt-LT";
        } else if (equals(MALAY)) {
            return "ms-MY";
        } else if (equals(NORWEGIAN)) {
            return "no-NO";
        } else if (equals(PERSIAN)) {
            return "fa-IR";
        } else if (equals(POLISH)) {
            return "pl-PL";
        } else if (equals(PORTUGUESE)) {
            return "pt-PT";
        } else if (equals(ROMANIAN)) {
            return "ro-RO";
        } else if (equals(RUSSIAN)) {
            return "ru-RU";
        } else if (equals(SLOVAK)) {
            return "sk-SK";
        } else if (equals(SLOVENIAN)) {
            return "sl-SI";
        } else if (equals(SPANISH)) {
            return "es-ES";
        } else if (equals(SWEDISH)) {
            return "sv-SE";
        } else if (equals(THAI)) {
            return "th-TH";
        } else if (equals(TURKISH)) {
            return "tr-TR";
        } else if (equals(UKRAINIAN)) {
            return "uk-UA";
        } else if (equals(URDU)) {
            return "ur-PK";
        } else if (equals(VIETNAMESE)) {
            return "vi-VN";
        }
        return "Autodetect";
    }

    public Language toBing() {
        if (equals(ARABIC)) {
            return Language.ARABIC;
        } else if (equals(BULGARIAN)) {
            return Language.BULGARIAN;
        } else if (equals(CATALAN)) {
            return Language.CATALAN;
        } else if (equals(CHINESE_SIMPLIFIED)) {
            return Language.CHINESE_SIMPLIFIED;
        } else if (equals(CHINESE_TRADITIONAL)) {
            return Language.CHINESE_TRADITIONAL;
        } else if (equals(CZECH)) {
            return Language.CZECH;
        } else if (equals(DANISH)) {
            return Language.DANISH;
        } else if (equals(DUTCH)) {
            return Language.DUTCH;
        } else if (equals(ENGLISH)) {
            return Language.ENGLISH;
        } else if (equals(ESTONIAN)) {
            return Language.ESTONIAN;
        } else if (equals(FINNISH)) {
            return Language.FINNISH;
        } else if (equals(FRENCH)) {
            return Language.FRENCH;
        } else if (equals(GERMAN)) {
            return Language.GERMAN;
        } else if (equals(GREEK)) {
            return Language.GREEK;
        } else if (equals(HAITIAN_CREOLE)) {
            return Language.HAITIAN_CREOLE;
        } else if (equals(HEBREW)) {
            return Language.HEBREW;
        } else if (equals(HINDI)) {
            return Language.HINDI;
        } else if (equals(HMONG_DAW)) {
            return Language.HMONG_DAW;
        } else if (equals(HUNGARIAN)) {
            return Language.HUNGARIAN;
        } else if (equals(INDONESIAN)) {
            return Language.INDONESIAN;
        } else if (equals(ITALIAN)) {
            return Language.ITALIAN;
        } else if (equals(JAPANESE)) {
            return Language.JAPANESE;
        } else if (equals(KOREAN)) {
            return Language.KOREAN;
        } else if (equals(LATVIAN)) {
            return Language.LATVIAN;
        } else if (equals(LITHUANIAN)) {
            return Language.LITHUANIAN;
        } else if (equals(MALAY)) {
            return Language.MALAY;
        } else if (equals(NORWEGIAN)) {
            return Language.NORWEGIAN;
        } else if (equals(PERSIAN)) {
            return Language.PERSIAN;
        } else if (equals(POLISH)) {
            return Language.POLISH;
        } else if (equals(PORTUGUESE)) {
            return Language.PORTUGUESE;
        } else if (equals(ROMANIAN)) {
            return Language.ROMANIAN;
        } else if (equals(RUSSIAN)) {
            return Language.RUSSIAN;
        } else if (equals(SLOVAK)) {
            return Language.SLOVAK;
        } else if (equals(SLOVENIAN)) {
            return Language.SLOVENIAN;
        } else if (equals(SPANISH)) {
            return Language.SPANISH;
        } else if (equals(SWEDISH)) {
            return Language.SWEDISH;
        } else if (equals(THAI)) {
            return Language.THAI;
        } else if (equals(TURKISH)) {
            return Language.TURKISH;
        } else if (equals(UKRAINIAN)) {
            return Language.UKRAINIAN;
        } else if (equals(URDU)) {
            return Language.URDU;
        } else if (equals(VIETNAMESE)) {
            return Language.VIETNAMESE;
        }
        return Language.AUTO_DETECT;
    }

}
