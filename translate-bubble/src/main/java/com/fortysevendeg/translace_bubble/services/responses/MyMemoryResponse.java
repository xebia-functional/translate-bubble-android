package com.fortysevendeg.translace_bubble.services.responses;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(value = {"responseDetails", "responseStatus", "responderId", "matches"}, ignoreUnknown = true)
public class MyMemoryResponse implements Serializable {

    private MyMemoryDataResponse responseData;

    public MyMemoryDataResponse getResponseData() {
        return responseData;
    }

    public void setResponseData(MyMemoryDataResponse responseData) {
        this.responseData = responseData;
    }
}
