package br.curitiba.terraviva.terra_viva_app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import br.curitiba.terraviva.terra_viva_app.MaskEditUtil;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.Util;
import br.curitiba.terraviva.terra_viva_app.api.PagtoManager;
import br.curitiba.terraviva.terra_viva_app.model.Compra;
import cn.carbs.android.library.MDDialog;

import static br.curitiba.terraviva.terra_viva_app.Session.compras;
import static br.curitiba.terraviva.terra_viva_app.Session.frete;

public class PagtoActivity extends AppCompatActivity {

    private EditText et_number,et_titular,et_mes,et_ano,et_cvv;
    private Spinner sp_parcelas;
    private TextView tv_total,tv_num_pagto,tv_titular_pagto,tv_valid_pagto,tv_total_pagto;
    private float total = frete;
    private PagtoManager manager;
    private String nome_user,tel_user,num_user,cep_user,rua_user,bairro_user,cidade_user,uf_user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagto);

        tv_num_pagto = findViewById(R.id.num_pagto);
        tv_titular_pagto = findViewById(R.id.titular_pagto);
        tv_valid_pagto = findViewById(R.id.valid_pagto);
        tv_total_pagto = findViewById(R.id.pagar_pagto);
        Button btn_pagto = findViewById(R.id.btn_pagto);
        manager = new PagtoManager(getApplicationContext(),this);

        Intent it = getIntent();
        if(it != null){
            Bundle data = it.getExtras();
            if(data != null){
                nome_user = data.getString("nome_user");
                tel_user = "("+data.getString("ddd_user")+")"+data.getString("tel_user");
                num_user = data.getString("num_user");
                cep_user = data.getString("cep_user");
                rua_user = data.getString("rua_user");
                bairro_user = data.getString("bairro_user");
                cidade_user = data.getString("cidade_user");
                uf_user = data.getString("uf_user");
            }
        }

        for (Compra c:compras) {
            total+=c.getQtd()*c.getProduto().getValor();
        }
        openDialog();
        btn_pagto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                manager.pagar(nome_user,tel_user,num_user,cep_user,rua_user,bairro_user,cidade_user,uf_user);
            }
        });

    }

    private void openDialog(){
        new MDDialog.Builder(this)
                .setTitle("EFETUAR PAGAMENTO")
                .setContentView(R.layout.payment)
                .setContentViewOperator(new MDDialog.ContentViewOperator() {
                    @Override
                    public void operate(View contentView) {
                        et_number = contentView.findViewById(R.id.card_number);
                        et_titular = contentView.findViewById(R.id.card_holder);
                        et_mes = contentView.findViewById(R.id.card_month);
                        et_ano = contentView.findViewById(R.id.card_year);
                        et_cvv = contentView.findViewById(R.id.card_cvv);
                        sp_parcelas = contentView.findViewById(R.id.card_parcels);
                        tv_total = contentView.findViewById(R.id.card_total);
                        tv_total.setText("TOTAL A PAGAR: 1 x "+Util.formatCurrency(total + frete));
                        et_number.addTextChangedListener(MaskEditUtil.mask(et_number, MaskEditUtil.FORMAT_CREDITCARD));

                        List<String> parcelas = new ArrayList<>();
                        for(int i=1;i<=10;i++){
                            parcelas.add(i+" x "+Util.formatCurrency(total/i));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                                android.R.layout.simple_spinner_dropdown_item, parcelas);

                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
                        sp_parcelas.setAdapter(adapter);

                        sp_parcelas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                            @Override
                            public void onItemSelected(AdapterView<?> parent, View v, int posicao, long id) {
                                tv_total.setText("TOTAL A PAGAR: "+sp_parcelas.getSelectedItem().toString());
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });
                    }
                })
                .setNegativeButton("Cancelar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();

                    }
                })
                .setPositiveButton("Pagar", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        tv_num_pagto.setText(et_number.getText().toString());
                        tv_titular_pagto.setText(et_titular.getText().toString());
                        tv_valid_pagto.setText(et_mes.getText().toString()+"/"+et_ano.getText().toString());
                        tv_total_pagto.setText(tv_total.getText().toString());

                    }
                })
                .create()
                .show();
    }

}
