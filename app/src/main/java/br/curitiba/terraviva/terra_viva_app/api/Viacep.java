package br.curitiba.terraviva.terra_viva_app.api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import br.curitiba.terraviva.terra_viva_app.activities.CarrinhoActivity;
import br.curitiba.terraviva.terra_viva_app.volley.Volley;
import br.curitiba.terraviva.terra_viva_app.volley.VolleyCallback;

public class Viacep {
    private String cep;
    private boolean error = true;
    private ProgressDialog pDialog;

    public String getCep(){
        return this.cep;
    }
    public boolean hadError(){
        return error;
    }
    public Viacep(final Context context,final Activity activity,final boolean backOnError,final EditText et_cep,final Button avancar,
                  final EditText bairro, final EditText cidade, final EditText rua, final EditText uf,final EditText... editTexts) {
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Buscando endereço...");
        pDialog.setCancelable(false);
        pDialog.show();

        final String CleanCep = et_cep.getText().toString().replace("-","");
        final Volley volley = new Volley(context, "https://terraviva.curitiba.br/api/viacep/" + CleanCep);
        String[] items = {"rua", "cep","bairro", "localidade", "uf"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {
                if(response.size() > 0) {
                    cep = response.get(0).get("cep");
                    rua.setText(response.get(0).get("rua"));
                    bairro.setText(response.get(0).get("bairro"));
                    cidade.setText(response.get(0).get("localidade"));
                    uf.setText(response.get(0).get("uf"));
                    for (EditText et:editTexts) {
                        et.setEnabled(true);
                    }
                    avancar.setEnabled(true);
                }else{
                    if(backOnError){
                        Toast.makeText(context,"Problemas para conectar á base de endereços. Tente novamente ",Toast.LENGTH_LONG).show();
                        Intent it = new Intent(context,CarrinhoActivity.class);
                        activity.startActivity(it);
                    }
                    cep = "";
                    et_cep.setError("CEP inválido");
                    rua.setText("");
                    bairro.setText("");
                    cidade.setText("");
                    uf.setText("");
                    for (EditText et:editTexts) {
                        et.setEnabled(false);
                    }
                    avancar.setEnabled(false);
                }
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }

            @Override
            public void onError(String error) {
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
                Toast.makeText(context,"Falha na conexão com o servidor Viacep. Tente novamente",Toast.LENGTH_LONG).show();
            }
        });
    }
    public Viacep(final Context context,final Activity activity,String cep) {

        final String CleanCep = cep.replace("-","");
        final Volley volley = new Volley(context, "https://terraviva.curitiba.br/api/viacep/" + CleanCep);
        String[] items = {"rua", "cep","bairro", "localidade", "uf"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {
                if(response.size() > 0) {
                    error = false;
                }else{
                    error = true;
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

}
