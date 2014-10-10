package com.fortysevendeg.translace_bubble.services.responses;

import java.io.Serializable;

public class MyMemoryDataResponse implements Serializable {

    private String translatedText;

    public String getTranslatedText() {
        return translatedText;
    }

    public void setTranslatedText(String translatedText) {
        this.translatedText = translatedText;
    }
}
