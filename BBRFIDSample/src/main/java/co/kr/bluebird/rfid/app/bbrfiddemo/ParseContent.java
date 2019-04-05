package co.kr.bluebird.rfid.app.bbrfiddemo;

import android.app.Activity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

import co.kr.bluebird.rfid.app.bbrfiddemo.Constants;
import co.kr.bluebird.rfid.app.bbrfiddemo.repl_elements;

public class ParseContent {

    private final String KEY_SUCCESS = "status";
    private final String KEY_MSG = "message";
    private Activity activity;

    ArrayList<HashMap<String, String>> arraylist;

    public ParseContent(Activity activity) {
        this.activity = activity;
    }

    public boolean isSuccess(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.optString(KEY_SUCCESS).equals("true")) {
                return true;
            } else {

                return false;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public String getErrorCode(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getString(KEY_MSG);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "No data";
    }

    public ArrayList<repl_elements> getInfo(String response) {
        ArrayList<repl_elements> playersModelArrayList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.getString(KEY_SUCCESS).equals("true")) {

                arraylist = new ArrayList<HashMap<String, String>>();
                JSONArray dataArray = jsonObject.getJSONArray("data");

                for (int i = 0; i < dataArray.length(); i++) {
                    repl_elements repl_e = new repl_elements();
                    JSONObject dataobj = dataArray.getJSONObject(i);
                    repl_e.setIt_id(dataobj.getString(Constants.it_id));
                    repl_e.setSize(dataobj.getString(Constants.size));
                    repl_e.setStyle(dataobj.getString(Constants.style));
                    repl_e.setColor(dataobj.getString(Constants.color));
                    playersModelArrayList.add(repl_e);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return playersModelArrayList;
    }

}