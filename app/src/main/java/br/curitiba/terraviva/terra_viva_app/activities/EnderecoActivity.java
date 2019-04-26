package br.curitiba.terraviva.terra_viva_app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import br.curitiba.terraviva.terra_viva_app.MaskEditUtil;
import br.curitiba.terraviva.terra_viva_app.R;

import static br.curitiba.terraviva.terra_viva_app.Session.usuario;

public class EnderecoActivity extends AppCompatActivity {

    private EditText et_rua,et_num,et_compl,et_bairro,et_cidade,et_cep;
    private Spinner uf;
    private Button avancar;
    private TextView tv_titulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);


        tv_titulo = findViewById(R.id.tv_titulo_endereco);
        et_rua = findViewById(R.id.et_rua);
        et_num = findViewById(R.id.et_num);
        et_compl = findViewById(R.id.et_compl);
        et_bairro = findViewById(R.id.et_bairro);
        et_cidade = findViewById(R.id.et_cidade);
        et_cep = findViewById(R.id.et_cep);

        uf = findViewById(R.id.sp_uf);

        String[] arraySpinner = new String[] {"AC","AL","AP","AM","BA","CE","DF","ES","GO",
                "MA","MT","MS","MG","PA","PB","PR","PE","PI","RJ","RN","RO","RR","SC","SP","TO"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        uf.setAdapter(adapter);

        et_cep.addTextChangedListener(MaskEditUtil.mask(et_cep, MaskEditUtil.FORMAT_CEP));

        avancar = findViewById(R.id.btn_avancar_entrega);

        if(usuario!=null) {
            Intent it = getIntent();
            if (it != null) {
                Bundle data = it.getExtras();
                if (data != null) {
                    tv_titulo.setText("DADOS PARA ENTREGA");
                    et_cep.setText(data.getString("cep"));
                    if(data.getString("cep").equals(usuario.getCep())){
                        et_bairro.setText(usuario.getBairro());
                        et_cidade.setText(usuario.getCidade());
                        et_compl.setText(usuario.getCompl());
                        et_num.setText(usuario.getNum());
                        et_rua.setText(usuario.getRua());
                        uf.setSelection(getIndex(uf,usuario.getUf()));
                    }
                }
            }
            avancar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getApplicationContext(),"ir para pagto...",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //private method of your class
    private int getIndex(Spinner spinner, String uf){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(uf)){
                return i;
            }
        }
        return 0;
    }
}
