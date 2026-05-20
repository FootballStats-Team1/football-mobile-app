package com.example.temp_project;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttpHandler {
    private final OkHttpClient client = new OkHttpClient();

    public interface StandingsCallback {
        void onSuccess(JSONArray standings);
        void onError(String message);
    }

    public void getStandings(StandingsCallback callback) {
        String url = Config.BASE_URL + "getStandings.php";

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Σφάλμα Δικτύου: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    try {
                        JSONArray jsonArray = new JSONArray(responseString);
                        callback.onSuccess(jsonArray);
                    } catch (JSONException e) {
                        callback.onError("Πρόβλημα στο Parsing: " + e.getMessage());
                    }
                } else {
                    callback.onError("Server returned: " + response.code());
                }
            }
        });
    }
}