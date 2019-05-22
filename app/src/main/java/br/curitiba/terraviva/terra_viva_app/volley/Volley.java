package br.curitiba.terraviva.terra_viva_app.volley;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Volley {
    private String url;
    private Context context;
    private Map params;

    public Volley(Context context, String url){
        this.url = url;
        this.context = context;
        this.params = new HashMap();
    }

    public Volley(Context context, String url, Map params){
        this.url = url;
        this.context = context;
        this.params = params;
    }

    public void getRequest(final String[] target, final VolleyCallback callback){
        StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.GET, url,
                new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray = null;
                        List<HashMap<String,String>> lista = new ArrayList<>();
                        try {
                            jsonArray = new JSONArray(response);

                            HashMap<String,String> item = null;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                item = new HashMap<>();
                                for( String param : target) {

                                    item.put(param, obj.getString(param));
                                }
                                lista.add(item);
                            }

                            callback.onSuccess((ArrayList<HashMap<String,String>>) lista);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error response", error.toString());
                        callback.onError(error.toString());
                    }
                }
        );
        postRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 40000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 40000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
        RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(context);
        queue.add(postRequest);
    }

    public void postRequest(final String[] target, final VolleyCallback callback){
        StringRequest postRequest = new StringRequest(com.android.volley.Request.Method.POST, url,
                new Response.Listener<String>()
                {

                    @Override
                    public void onResponse(String response) {
                        JSONArray jsonArray = null;
                        List<HashMap<String,String>> lista = new ArrayList<>();
                        try {
                            jsonArray = new JSONArray(response);

                            HashMap<String,String> item = null;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                item = new HashMap<>();
                                for( String param : target) {
                                    item.put(param, obj.getString(param));
                                }
                                lista.add(item);
                            }

                            callback.onSuccess((ArrayList<HashMap<String,String>>) lista);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.toString());
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }
        };
        RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(context);
        queue.add(postRequest);
    }


}
