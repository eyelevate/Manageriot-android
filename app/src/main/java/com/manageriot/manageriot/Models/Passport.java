package com.manageriot.manageriot.Models;

import com.manageriot.manageriot.R;
import com.q42.qlassified.Qlassified;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wondochoung on 12/22/17.
 */

public class Passport {
    final String url = "http://manageriot.com/";

    public String makeUrl(String slug) {
        return url+"api/"+slug;
    }

    public String makeOauthUrl(String slug) {
        return url+slug;
    }

    public String makeCheckerToken() {
        return "Bearer "+ R.string.checker_token;
    }

    public String getActiveUser() {
        // get key store
        String email = Qlassified.Service.getString("keystore_current");
        if (email != null) {
            return email;
        } else {
            return "";
        }
    }

    public String getAccessToken() {
        String email = this.getActiveUser();
        String keychain = Qlassified.Service.getString("keystore_tokens");
        try {
            JSONObject tokens = new JSONObject(keychain);
            JSONObject keys = new JSONObject(tokens.getString(email));
            String access_token = keys.getString("access_token");
            return access_token;

        } catch (JSONException e2) {
            return "";
        }

    }

    public String getRefreshToken() {
        String email = this.getActiveUser();
        String keychain = Qlassified.Service.getString("keystore_tokens");
        try {
            JSONObject tokens = new JSONObject(keychain);
            JSONObject keys = new JSONObject(tokens.getString(email));
            String refresh_token = keys.getString("refresh_token");
            return refresh_token;

        } catch (JSONException e2) {
            return "";
        }


    }

    public String getAuthorization() {
        String access_token = this.getAccessToken();
        return "Bearer "+access_token;
    }


}
