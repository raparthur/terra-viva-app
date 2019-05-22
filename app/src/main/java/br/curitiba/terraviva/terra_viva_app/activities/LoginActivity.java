package br.curitiba.terraviva.terra_viva_app.activities;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import br.curitiba.terraviva.terra_viva_app.MaskEditUtil;
import br.curitiba.terraviva.terra_viva_app.MenuActions;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.api.LoginManager;

public class LoginActivity extends AppCompatActivity {
    private LoginManager manager;
    private TextView label_confirmar,tv_enviado;
    private EditText et_email,et_senha,et_cpf;
    private ImageButton btn_ver;
    private Button btn_confirmar;
    private boolean visiblePwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        manager = new LoginManager(getApplicationContext(), this);
        TextView tv_erro = findViewById(R.id.tv_erro_login);
        TextView tv_home = findViewById(R.id.tv_voltar_login);
        final TextView tv_recuperar = findViewById(R.id.tv_recuperar);
        label_confirmar = findViewById(R.id.tv_conf_login);
        tv_enviado = findViewById(R.id.tv_enviado_login);
        et_cpf = findViewById(R.id.et_cpf_login);
        btn_confirmar = findViewById(R.id.btn_confirmar_login);
        et_email = findViewById(R.id.et_email);
        et_senha = findViewById(R.id.et_senha);
        Button btn_entrar = findViewById(R.id.btn_entrar);
        btn_ver = findViewById(R.id.btn_ver);
        visiblePwd = false;
        btn_ver.setBackgroundResource(R.drawable.ic_eye);
        et_cpf.addTextChangedListener(MaskEditUtil.mask(et_cpf, MaskEditUtil.FORMAT_CPF));

        tv_recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                label_confirmar.setVisibility(View.VISIBLE);
                et_cpf.setVisibility(View.VISIBLE);
                btn_confirmar.setVisibility(View.VISIBLE);
                tv_recuperar.setText("");
            }
        });

        btn_confirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.dispararSenha(et_cpf,tv_enviado,btn_confirmar);
                tv_enviado.setVisibility(View.VISIBLE);
            }
        });

        tv_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new MenuActions(getApplicationContext(),LoginActivity.this)).goHome();
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
