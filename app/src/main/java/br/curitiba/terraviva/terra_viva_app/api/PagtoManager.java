package br.curitiba.terraviva.terra_viva_app.api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.curitiba.terraviva.terra_viva_app.Session;
import br.curitiba.terraviva.terra_viva_app.activities.FinalizadoActivity;
import br.curitiba.terraviva.terra_viva_app.volley.Volley;
import br.curitiba.terraviva.terra_viva_app.volley.VolleyCallback;

public class PagtoManager {
    private Activity activity;
    private Context context;
    private ProgressDialog pDialog;
    public PagtoManager(Context context,Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void pagar(String nome,String tel,String num,String cep,String rua,
                      String bairro,String cidade,String uf) {
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Efetivando venda...");
        pDialog.setCancelable(false);
        pDialog.show();
        Map<String, String> params = new HashMap<>();
        params.put("email", Session.usuario.getEmail());
        params.put("frete", String.valueOf(Session.frete));
        params.put("prazo", String.valueOf(Session.prazo));
        params.put("nome", nome);
        params.put("tel", tel);
        params.put("num", num);
        params.put("cep", cep);
        params.put("rua", rua);
        params.put("bairro", bairro);
        params.put("cidade", cidade);
        params.put("uf", uf);

        Volley volley = new Volley(context, "https://terraviva.curitiba.br/user/finalizar", params);
        String[] dados = {"inserted"};

        volley.postRequest(dados, new VolleyCallback() {

            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {
                if(Integer.parseInt(response.get(0).get("inserted")) > 0){
                    activity.startActivity(new Intent(context,FinalizadoActivity.class));
                    Session.compras = null;
                    if (pDialog != null && pDialog.isShowing())
                        pDialog.dismiss();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(context,"Problemas ao conectar-se ao servidor",Toast.LENGTH_LONG).show();
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        });
    }
}
