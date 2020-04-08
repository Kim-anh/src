package fr.garcia.gaetan.image_mobile_app;

import org.json.JSONObject;

interface FetchDataListener {
    void onFetchComplete(JSONObject data);

    void onFetchFailure(String msg);

    void onFetchStart();
}