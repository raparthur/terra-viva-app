package br.curitiba.terraviva.terra_viva_app.volley;

import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.HashMap;

public interface VolleyCallback {
    void onSuccess(ArrayList<HashMap<String, String>> response);
    void onError(String error);
}
