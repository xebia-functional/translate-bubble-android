package com.fortysevendeg.translace_bubble.events;

import com.fortysevendeg.translace_bubble.utils.TypeTranslateUI;

public class ChangeTypeFeedback {

    private TypeTranslateUI type;

    public ChangeTypeFeedback(TypeTranslateUI type) {
        this.type = type;
    }

    public TypeTranslateUI getType() {
        return type;
    }

    public void setType(TypeTranslateUI type) {
        this.type = type;
    }

}
