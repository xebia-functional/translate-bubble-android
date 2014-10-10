package com.fortysevendeg.translace_bubble.events;

public class FakeTranslatedEvent {

    private String sample = "example " + System.currentTimeMillis();

    public FakeTranslatedEvent() {
    }

    public String getSample() {
        return sample;
    }

    public void setSample(String sample) {
        this.sample = sample;
    }
}
