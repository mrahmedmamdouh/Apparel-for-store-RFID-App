/*
 * Copyright (C) 2015 - 2017 Bluebird Inc, All rights reserved.
 * 
 * http://www.bluebirdcorp.com/
 * 
 * Author : Bogon Jun
 *
 * Date : 2016.01.18
 */

package co.kr.bluebird.rfid.app.bbrfiddemo.control;

public class ListItem  {
    
    public int mIv;
    
    public String mUt;
    
    public String mDt;
    
    public int mDupCount;
    public String idi;

    public boolean mHasPc;


    public ListItem() {
        mIv = -1;
        mUt = null;
        mDt = null;
        idi = null;

        mDupCount = 0;
        mHasPc = true;
    }
}
