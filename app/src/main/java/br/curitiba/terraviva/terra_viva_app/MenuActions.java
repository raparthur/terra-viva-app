package br.curitiba.terraviva.terra_viva_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import br.curitiba.terraviva.terra_viva_app.connexion.Volley;
import br.curitiba.terraviva.terra_viva_app.connexion.VolleyCallback;
import br.curitiba.terraviva.terra_viva_app.model.Usuario;
import br.curitiba.terraviva.terra_viva_app.view.HomeActivity;

public class MenuActions {
    private Context context;
    private Activity activity;

    public MenuActions(Context context,Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void goHome(){
        Intent it = new Intent(context,HomeActivity.class);
        context.startActivity(it);
        activity.finish();
    }

    public void register(){
        Map<String,String> params = new HashMap<>();
        params.put("email", "user@user.com");
        params.put("pwd", "123");
        Volley volley = new Volley(context,"https://terraviva.curitiba.br/user/login",params, activity);
        String[] login = {"login"};
        volley.postRequest(login, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {
                String nome = response.get(0).get("login");
                Toast.makeText(context, nome, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
