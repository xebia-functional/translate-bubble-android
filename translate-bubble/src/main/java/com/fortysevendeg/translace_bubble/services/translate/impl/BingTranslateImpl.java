package com.fortysevendeg.translace_bubble.services.translate.impl;

import com.fortysevendeg.translace_bubble.services.translate.TranslateService;
import com.fortysevendeg.translace_bubble.utils.TypeLanguage;
import com.memetix.mst.translate.Translate;

public class BingTranslateImpl implements TranslateService {

    public static final String CLIENT_ID = "bubble_translator";
    public static final String CLIENT_SECRET = "Ebh1TxtCAxRMdn1kbZu2j4Q4BrGteZ1Chq0ClHJkRP0=";

    @Override
    public String translate(String text, TypeLanguage from, TypeLanguage to) {
        Translate.setClientId(CLIENT_ID);
        Translate.setClientSecret(CLIENT_SECRET);
        String translatedText = "";
        try {
            translatedText = Translate.execute(text, from.toBing(), to.toBing());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return translatedText;
    }

}
