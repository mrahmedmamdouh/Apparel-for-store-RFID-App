package co.kr.bluebird.rfid.app.bbrfiddemo;


import android.annotation.SuppressLint;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles.CZoneFragment;

import static android.content.Context.MODE_PRIVATE;
import static com.android.volley.VolleyLog.TAG;

public class ProductInfo extends android.app.Fragment {

    public static ProductInfo newInstance() {
        return new ProductInfo();
    }
    TextView ProductInfo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    public ProductInfo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_czone, container, false);

        ProductInfo = view.findViewById(R.id.productInfo);

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        SharedPreferences pref = getActivity().getSharedPreferences("RFID", MODE_PRIVATE);
        String RFID = pref.getString("RFID", "DEFAULT");
        params.put("RFID" , RFID);



        client.post("http://41.65.223.218:8888/api/RFIDInfo?RFID=1930553301", params, new AsyncHttpResponseHandler() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(String id) {
                if (id.equals("true")){

                    Log.d(TAG, "onSuccess: " + id);
                    try {
                        JSONArray data_array = new JSONArray(id);

                        for (int i = 0; i < data_array.length(); i++) {
                            JSONObject obj = data_array.getJSONObject(i);
                            ProductInfo.setText( "Brand: "+ obj.getString("brand") + ", \n" +"Section: "+ obj.getString("Section")  + ", \n"+"Family: "+ obj.getString("family")  + ", \n"+"SubFamily: "+ obj.getString("subfamily")  + ", \n"+"Color: "+ obj.getString("color")  + ", \n"+"ColorCode: "+ obj.getString("colorcode")  + ", \n"+"SizeCode: "+ obj.getString("sizecode")  + ", \n"+"FullPrice: "+ obj.getInt("FullPrice")  + ", \n"+"RetailPrice: "+ obj.getInt("RetailPrice")  + ", \n"+"Description: "+ obj.getString("Description") );


                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                    super.onSuccess(id); }}});


        return view ;
    }

    }
