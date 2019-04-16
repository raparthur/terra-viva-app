package br.curitiba.terraviva.terra_viva_app.view;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.connexion.PostManager;

public class LoginActivity extends AppCompatActivity {
    private PostManager manager;
    private Intent it;
    private TextView tv_erro,tv_home,tv_recuperar;
    private EditText et_email,et_senha;
    private ImageButton btn_ver;
    private Button btn_entrar;
    private boolean visiblePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        manager = new PostManager(getApplicationContext(), this);
        tv_erro = findViewById(R.id.tv_erro_login);
        tv_home = findViewById(R.id.tv_voltar_login);
        tv_recuperar = findViewById(R.id.tv_recuperar);
        et_email = findViewById(R.id.et_email);
        et_senha = findViewById(R.id.et_senha);
        btn_entrar = findViewById(R.id.btn_entrar);
        btn_ver = findViewById(R.id.btn_ver);
        visiblePwd = false;
        btn_ver.setBackgroundResource(R.drawable.ic_eye);

        tv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                it = new Intent(getApplicationContext(),HomeActivity.class);
                startActivity(it);
            }
        });
        tv_recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        btn_ver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(visiblePwd){
                    visiblePwd = false;
                    et_senha.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    btn_ver.setBackgroundResource(R.drawable.ic_eye);
                }else{
                    visiblePwd = true;
                    et_senha.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    btn_ver.setBackgroundResource(R.drawable.ic_eyeslash);
                }
                et_senha.setSelection(et_senha.getText().length());
            }
        });
        btn_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email,senha;
                if(et_email.length() > 0 && et_senha.length() > 0){
                    email = et_email.getText().toString();
                    senha = et_senha.getText().toString();

                    manager.login(email,senha);
                }else{
                    TextView erro = findViewById(R.id.tv_erro_login);
                    erro.setText("Ambos os campos são obrigatórios!");
                    erro.setTextColor(Color.RED);
                }
            }
        });
    }

}
