package com.sdsmdg.maps;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rahul on 14/12/16.
 */

public class VolleyRequest extends StringRequest {

    private static final String GET_DISTANCE = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&key=AIzaSyCr2AVH-9JAHDKi8q9yRt-3hMGj2StrvsM";
//    private static final String CHECK_OTP_URL = "http://dashboard108.herokuapp.com/otp/check?";
    private Map<String, String> params;
    private String TAG = "Volley";

    public VolleyRequest(String data, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Request.Method.GET, GET_DISTANCE + data, listener, errorListener);
//        Log.d(TAG, "VolleyRequest() called with:"+GENERATE_OTP_URL+" mobile = [" + mobile + "], listener = [" + listener + "], errorListener = [" + errorListener + "]");
//        params = new HashMap<>();
//        params.put("mobile", mobile);
    }


    @Override
    public Map<String, String> getParams() {
        return params;
    }
}


