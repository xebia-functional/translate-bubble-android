package com.fortysevendeg.translace_bubble.services.translate;

import com.fortysevendeg.translace_bubble.utils.TypeLanguage;

public interface TranslateService {

    String translate(String text, TypeLanguage from, TypeLanguage to);

}
