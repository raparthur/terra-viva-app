package br.curitiba.terraviva.terra_viva_app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.Session;

public class FinalizadoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalizado);

        TextView prazo = findViewById(R.id.prazo_final);
        prazo.setText(Session.prazo);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(),HomeActivity.class));
    }
}
