package com.sdsmdg.maps;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by rahul on 1/4/17.
 */
public class RequestItem implements Serializable{
    private static final String TAG = RequestItem.class.getSimpleName();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    private String name;
    private String userMobile;
    private String requestId;
    private String age;
    private String gender;
    private String latitude;
    private String longitude;

    public static String NAME = "name";
    public static String USER_MOBILE = "userMobiles";
    public static String REQUEST_ID = "requestId";
    public static String AGE = "age";
    public static String GENDER = "gender";
    public static String LATITUDE = "latitude";
    public static String LONGITUDE = "longitude";

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(NAME, name);
            jsonObject.put(USER_MOBILE, userMobile);
            jsonObject.put(REQUEST_ID, requestId);
            jsonObject.put(AGE,age);
            jsonObject.put(GENDER, gender);
            jsonObject.put(LATITUDE,latitude);
            jsonObject.put(LONGITUDE, longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject.toString();
    }

    public RequestItem(String name, String userMobile, String requestId, String age, String gender, String latitude, String longitude) {
        this.name = name;
        this.userMobile = userMobile;
        this.requestId = requestId;
        this.age = age;
        this.gender = gender;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public RequestItem(String jsonString){
        if(jsonString!=null){
            try {
                JSONObject jsonObject = new JSONObject(jsonString);
                Log.d(TAG, "RequestItem() called with: jsonString = [" + jsonObject.toString() + "]");
                this.name = jsonObject.getString(NAME);
                this.userMobile  = jsonObject.getString(USER_MOBILE);
                this.requestId = jsonObject.getString(REQUEST_ID);
                this.age = jsonObject.getString(AGE);
                this.gender = jsonObject.getString(GENDER);
                this.latitude = jsonObject.getString(LATITUDE);
                this.longitude = jsonObject.getString(LONGITUDE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
