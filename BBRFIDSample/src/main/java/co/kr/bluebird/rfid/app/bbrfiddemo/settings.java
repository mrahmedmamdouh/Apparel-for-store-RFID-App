package co.kr.bluebird.rfid.app.bbrfiddemo;

import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import co.kr.bluebird.rfid.app.bbrfiddemo.BarcodeFiles.CZoneFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragments.SelectStoreFormFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.BarcodeFragments.ShipItemsFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.ConnectivityFragment;
import co.kr.bluebird.rfid.app.bbrfiddemo.fragment.TransRFIDFragment;

import static android.content.Context.MODE_PRIVATE;

public class settings extends Fragment {
    private EditText edit1,edit2,edit3;
    public String storeNumber, urlNumber,portNumber;
    Button btn;
    private static final String TAG = ConnectivityFragment.class.getSimpleName();

    private static final boolean D = Constants.CON_D;
    private Context c;
    Button setupZone,item;
    private Handler mOptionHandler;

    private Context mContext;
    private CZoneFragment mCZoneFragment;
    private ShipItemsFragment mShipmentFragment;
    private SelectStoreFormFragment mSelectStoreFormFragment;
    private TransRFIDFragment mTransRFIDFragment;
    private android.app.FragmentManager mFragmentManager;
    private android.app.Fragment mCurrentFragment;
    private FrameLayout frameLayout3;
    private RelativeLayout R1;

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
        setupZone = v.findViewById(R.id.btn_setupZone);
        item = v.findViewById(R.id.btn_item);
        mCurrentFragment = null;
        mFragmentManager = getFragmentManager();
        btn = (Button) v.findViewById(R.id.save_bttn);
        frameLayout3 = (FrameLayout) v.findViewById(R.id.frame_layout2);
        R1 = (RelativeLayout) v.findViewById(R.id.R1);

        setupZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // startActivity(new Intent(getActivity(),CZone.class));
                if (mCZoneFragment == null)
                    mCZoneFragment = CZoneFragment.newInstance();
                mCurrentFragment = mCZoneFragment;
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.replace(R.id.frame_layout2, mCurrentFragment);
                R1.setVisibility(View.GONE);

                ft.commit();
            }
        });
        item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  startActivity(new Intent(getActivity(),SelectStoreForm.class));
                if (mSelectStoreFormFragment == null)
                    mSelectStoreFormFragment = SelectStoreFormFragment.newInstance();
                mCurrentFragment = mSelectStoreFormFragment;
                FragmentTransaction ft = mFragmentManager.beginTransaction();
                ft.replace(R.id.frame_layout2, mCurrentFragment);
                R1.setVisibility(View.GONE);
                ft.commit();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(c, MainActivity.class);
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