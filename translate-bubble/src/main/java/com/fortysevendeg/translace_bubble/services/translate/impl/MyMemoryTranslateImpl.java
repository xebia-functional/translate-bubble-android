package com.fortysevendeg.translace_bubble.services.translate.impl;

import android.util.Log;
import com.fortysevendeg.translace_bubble.services.responses.MyMemoryResponse;
import com.fortysevendeg.translace_bubble.services.translate.TranslateService;
import com.fortysevendeg.translace_bubble.utils.TypeLanguage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.*;
import java.net.URLEncoder;

public class MyMemoryTranslateImpl implements TranslateService {

    @Override
    public String translate(String text, TypeLanguage from, TypeLanguage to) {
        String translate = "";

        String json = null;
        try {
            json = getData(text, from.toMyMemory(), to.toMyMemory());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (json != null && !json.isEmpty()) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                MyMemoryResponse myMemoryResponse = mapper.readValue(json, MyMemoryResponse.class);
                translate = myMemoryResponse.getResponseData().getTranslatedText();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return translate;

    }

    public String getData(String text, String from, String to) {

        String url = null;

        try {
            url = "http://api.mymemory.translated.net/get?q=" +
                    URLEncoder.encode(text, "UTF-8") +  "&langpair=" +
                    URLEncoder.encode(from + "|" + to, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String json = "";
        if (url != null) {
            InputStream is = null;
            try {
                // defaultHttpClient
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpPost = new HttpGet(url);
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                is = httpEntity.getContent();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "n");
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }
        }

        return json;
    }

    public String getData1(String text, String from, String to) {

        String url = null;
        String result = "";
        try {
            url = String.format("http://mymemory.translated.net/api/get?q=%s&langpair=en|es",
                    URLEncoder.encode(text, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (url != null) {
            InputStream inputStream = null;
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
                inputStream = httpResponse.getEntity().getContent();

                if (inputStream != null) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    String line = "";
                    while ((line = bufferedReader.readLine()) != null)
                        result += line;

                    inputStream.close();
                }

            } catch (Exception e) {
                Log.d("InputStream", e.getLocalizedMessage());
            }
        }

        return result;
    }


}
