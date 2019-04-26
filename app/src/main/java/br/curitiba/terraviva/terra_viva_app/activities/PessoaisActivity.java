package br.curitiba.terraviva.terra_viva_app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import br.curitiba.terraviva.terra_viva_app.MaskEditUtil;
import br.curitiba.terraviva.terra_viva_app.R;

import static br.curitiba.terraviva.terra_viva_app.Session.usuario;
import static br.curitiba.terraviva.terra_viva_app.Util.dateToStr;

public class PessoaisActivity extends AppCompatActivity {

    private EditText et_nome,et_email,et_nasc,et_tel,et_cel,et_cpf;
    private RadioGroup rg_tel;
    private RadioButton rb_fixo,rb_cel;
    private Button avancar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pessoais);

        et_nome = findViewById(R.id.et_nome_pessoais);
        et_email = findViewById(R.id.et_email_pessoais);
        et_nasc = findViewById(R.id.et_nasc_pessoais);
        et_tel = findViewById(R.id.et_tel_pessoais);
        et_cel = findViewById(R.id.et_cel_pessoais);
        et_cpf = findViewById(R.id.et_cpf_pessoais);

        rg_tel = findViewById(R.id.rg_pessoais);
        rb_cel = findViewById(R.id.rb_cel_pessoais);
        rb_fixo = findViewById(R.id.rb_fixo_pessoais);

        avancar = findViewById(R.id.btn_avancar_pessoais);

        et_cel.setVisibility(View.GONE);
        rb_fixo.setChecked(true);

        et_cpf.addTextChangedListener(MaskEditUtil.mask(et_cpf, MaskEditUtil.FORMAT_CPF));
        et_nasc.addTextChangedListener(MaskEditUtil.mask(et_nasc, MaskEditUtil.FORMAT_DATE));
        et_tel.addTextChangedListener(MaskEditUtil.mask(et_tel, MaskEditUtil.FORMAT_FIXO));
        et_cel.addTextChangedListener(MaskEditUtil.mask(et_cel, MaskEditUtil.FORMAT_CEL));

        rb_cel.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                et_cel.setVisibility(View.VISIBLE);
                et_tel.setVisibility(View.GONE);
            }
        });

        rb_fixo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                et_tel.setVisibility(View.VISIBLE);
                et_cel.setVisibility(View.GONE);
            }
        });

        if(usuario != null){
            if(usuario.getTel().length() == 8){
                et_cel.setVisibility(View.GONE);
                et_tel.setVisibility(View.VISIBLE);
                et_tel.setText("("+usuario.getDdd()+")"+usuario.getTel());
                rb_fixo.setChecked(true);
            }else{
                et_tel.setVisibility(View.GONE);
                et_cel.setVisibility(View.VISIBLE);
                et_cel.setText("("+usuario.getDdd()+")"+usuario.getTel());
                rb_cel.setChecked(true);
            }


            et_nome.setText(usuario.getNome());
            et_email.setText(usuario.getEmail());
            et_nasc.setText(dateToStr(usuario.getNasc(),"dd/MM/yyyy"));
            et_cpf.setText(usuario.getCpf());
            
        }

        avancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(getApplicationContext(),EnderecoActivity.class);
                Intent old = getIntent();
                if(old!=null){
                    Bundle bundle = old.getExtras();
                    if(bundle!=null){
                        it.putExtras(bundle);
                    }
                }
                startActivity(it);
            }
        });
    }
}
