package co.kr.bluebird.rfid.app.bbrfiddemo;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.ConnectivityFragment;

import static android.content.Context.MODE_PRIVATE;

public class settings extends Fragment {
    private EditText edit1,edit2,edit3;
    public String storeNumber, urlNumber,portNumber;
    Button btn;
    private static final String TAG = ConnectivityFragment.class.getSimpleName();

    private static final boolean D = Constants.CON_D;
    private Context c;
    private Handler mOptionHandler;

    public static settings newInstance() {
        return new settings();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (D) Log.d(TAG, "onCreateView");
        View v = inflater.inflate(R.layout.settings, container, false);

        c = inflater.getContext();

        mOptionHandler = ((MainActivity) getActivity()).mUpdateConnectHandler;


        edit1 = (EditText) v.findViewById(R.id.store_number);
        edit2 = (EditText) v.findViewById(R.id.edt1);
        edit3 = (EditText) v.findViewById(R.id.edt2);


        btn = (Button) v.findViewById(R.id.save_bttn);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent1 = new Intent(getActivity(), MainActivity.class);
                startActivity(intent1);


                storeNumber = edit1.getText().toString();
                SharedPreferences pref = getActivity().getSharedPreferences("storeNumber", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("storeNumber", storeNumber);  // Saving string
                editor.apply();

                urlNumber = edit2.getText().toString();
                SharedPreferences pref1 = getActivity().getSharedPreferences("urlNumber", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = pref1.edit();
                editor1.putString("urlNumber", urlNumber);  // Saving string
                editor1.apply();

                portNumber = edit3.getText().toString();
                SharedPreferences pref2 = getActivity().getSharedPreferences("portNumber", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = pref2.edit();
                editor2.putString("portNumber", portNumber);  // Saving string
                editor2.apply();


            }
        });

return v;
    }


}