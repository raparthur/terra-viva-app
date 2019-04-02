package br.curitiba.terraviva.terra_viva_app.connexion;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
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
    private ProgressDialog pDialog;

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
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Aguarde...");
        pDialog.setCancelable(false);
        pDialog.show();
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

                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            callback.onSuccess((ArrayList<HashMap<String,String>>) lista);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        Toast.makeText(context, "erro", Toast.LENGTH_SHORT).show();
                        Log.d("Error response", error.toString());
                    }
                }
        );
        RequestQueue queue = com.android.volley.toolbox.Volley.newRequestQueue(context);
        queue.add(postRequest);
    }

    public void postRequest(final String[] target, final VolleyCallback callback){
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Aguarde...");
        pDialog.setCancelable(false);
        pDialog.show();
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

                            if (pDialog.isShowing())
                                pDialog.dismiss();
                            callback.onSuccess((ArrayList<HashMap<String,String>>) lista);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context, "erro", Toast.LENGTH_SHORT).show();
                        if (pDialog.isShowing())
                            pDialog.dismiss();
                        Log.d("Error response", error.toString());
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
