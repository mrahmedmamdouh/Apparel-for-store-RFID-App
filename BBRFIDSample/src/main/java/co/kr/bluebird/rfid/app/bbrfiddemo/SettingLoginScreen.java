package co.kr.bluebird.rfid.app.bbrfiddemo;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.senddatafragment;

public class SettingLoginScreen extends Activity {
    public String storeNumber, urlNumber, portNumber;
    Button btn;
    private EditText edit1, edit2, edit3;
    private Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        edit1 = (EditText) findViewById(R.id.store_number);
        edit1.setText("01");
        edit2 = (EditText) findViewById(R.id.edt1);
        edit2.setText("192.168.1.124");
        edit3 = (EditText) findViewById(R.id.edt2);
        edit3.setText("8088");


        btn = (Button) findViewById(R.id.save_bttn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent1 = new Intent(SettingLoginScreen.this, Login.class);
                startActivity(intent1);


                storeNumber = edit1.getText().toString();
                SharedPreferences pref = getApplicationContext().getSharedPreferences("storeNumber", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("storeNumber", storeNumber);  // Saving string
                editor.apply();


                urlNumber = edit2.getText().toString();
                Bundle bundle1 = new Bundle();
                bundle1.putString("urlNumber", urlNumber);
// set Fragmentclass Arguments
                Fragment senddatafragment1 = new senddatafragment();
                senddatafragment1.setArguments(bundle1);


                portNumber = edit3.getText().toString();
                Bundle bundle2 = new Bundle();
                bundle2.putString("portNumber", portNumber);
// set Fragmentclass Arguments
                Fragment senddatafragment2 = new senddatafragment();
                senddatafragment2.setArguments(bundle2);
            }
        });

    }


}
