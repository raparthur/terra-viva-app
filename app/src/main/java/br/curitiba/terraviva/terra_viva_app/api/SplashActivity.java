package br.curitiba.terraviva.terra_viva_app.api;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.activities.HomeActivity;
import br.curitiba.terraviva.terra_viva_app.volley.Volley;
import br.curitiba.terraviva.terra_viva_app.volley.VolleyCallback;

public class SplashActivity extends AppCompatActivity implements Runnable{
    private ProgressBar pBar;
    private Button btn;
    private boolean stopThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        pBar = findViewById(R.id.progressBarSplash);
        btn = findViewById(R.id.buttonSplash);

        pBar.setVisibility(View.VISIBLE);
        btn.setVisibility(View.GONE);

        testarConexao();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SplashActivity.class));
                finish();
            }
        });

        Handler h = new Handler();
        int DELAY = 10000;
        h.postDelayed(this, DELAY);
    }

    private void testarConexao() {

            Volley volley = new Volley(getApplicationContext(), "https://terraviva.curitiba.br/api");
            String[] items = {"status"};
            volley.getRequest(items, new VolleyCallback() {

                @Override
                public void onSuccess(ArrayList<HashMap<String, String>> response) {
                    if (response.get(0).get("status").equals("ok")) {
                        stopThread = true;
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        ProgressDialog pDialog = new ProgressDialog(SplashActivity.this);
                        pDialog.setMessage("Aguarde...");
                        pDialog.setCancelable(false);
                        pDialog.show();
                        finish();
                    }
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(getApplicationContext(), "Sem resposta do servidor. Por favor, tente novamente!", Toast.LENGTH_LONG).show();
                    pBar.setVisibility(View.GONE);
                    btn.setVisibility(View.VISIBLE);
                }
            });
    }

    @Override
    public void run() {
        if(!stopThread) {
            Toast.makeText(getApplicationContext(), "Problemas ao acessar o servidor. Verifique sua conexão!", Toast.LENGTH_LONG).show();
            pBar.setVisibility(View.GONE);
            btn.setVisibility(View.VISIBLE);
        }
    }
}
