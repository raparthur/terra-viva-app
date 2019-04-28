package br.curitiba.terraviva.terra_viva_app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import br.curitiba.terraviva.terra_viva_app.MaskEditUtil;
import br.curitiba.terraviva.terra_viva_app.MenuActions;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.api.CarrinhoManager;
import br.curitiba.terraviva.terra_viva_app.api.Correios;
import br.curitiba.terraviva.terra_viva_app.api.Viacep;
import br.curitiba.terraviva.terra_viva_app.model.Compra;
import static br.curitiba.terraviva.terra_viva_app.Session.compras;
import static br.curitiba.terraviva.terra_viva_app.Session.usuario;
import static br.curitiba.terraviva.terra_viva_app.Util.formatCurrency;

public class CarrinhoActivity extends AppCompatActivity {

    TextView tv_produtos,tv_frete,tv_total,tv_labelfrete,tv_labeltotal,tv_tempo;
    EditText et_cep;
    ListView lv_carrinho;
    Button btn_calcular,btn_avancar;
    float carrinho=0;
    Correios correios;
    RadioButton rbPac,rbSedex;
    Viacep viacep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

         new CarrinhoManager(getApplicationContext(),this);

        tv_produtos = findViewById(R.id.tv_carrinhoprodutos);
        tv_frete = findViewById(R.id.tv_frete);
        tv_total = findViewById(R.id.tv_carrinhototal);
        tv_labelfrete = findViewById(R.id.tv_labelfrete);
        tv_labeltotal = findViewById(R.id.tv_labeltotal);
        tv_tempo = findViewById(R.id.tv_tempo);
        et_cep = findViewById(R.id.et_cepcarrinho);
        lv_carrinho = findViewById(R.id.lv_carrinho);
        btn_calcular = findViewById(R.id.btn_calcularfrete);
        btn_avancar = findViewById(R.id.btn_carrinhoavancar);
        rbPac = findViewById(R.id.rb_pac);
        rbSedex = findViewById(R.id.rb_sedex);

        et_cep.addTextChangedListener(MaskEditUtil.mask(et_cep, MaskEditUtil.FORMAT_CEP));


        for (Compra c:compras) {
            float total = (float) c.getQtd() * c.getProduto().getValor();
            carrinho+=total;
        }

        reset();

        et_cep.setText(usuario.getCep());
        rbPac.setChecked(true);

        tv_produtos.setText(formatCurrency(carrinho));

        rbPac.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                rbSedex.setChecked(false);
                reset();
            }
        });

        rbSedex.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                rbPac.setChecked(false);
                reset();
            }
        });


        btn_calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculaFrete();
            }
        });
        btn_avancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(correios != null && correios.getCep().equals(et_cep.getText().toString())) {
                    if(!viacep.hadError()) {
                        Intent it = new Intent(getApplicationContext(), PessoaisActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("cep", et_cep.getText().toString().replace("-", ""));
                        it.putExtras(bundle);
                        startActivity(it);
                    }else
                        Toast.makeText(getApplicationContext(),"CEP INVÁLIDO OU SERVIÇO INDISPONÍVEL PARA ESTA REGIÃO",Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(),"CEP alterado. Por favor, recalcule o frete",Toast.LENGTH_LONG).show();
            }
        });
    }

    private void calculaFrete(){
        if(et_cep.length() == 9){
            viacep = new Viacep(getApplicationContext(),this,et_cep.getText().toString());
            if(rbPac.isChecked())
                 correios = new Correios(getApplicationContext(),this,Correios.PAC,et_cep.getText().toString(),
                    carrinho,tv_tempo,tv_frete,tv_total,btn_avancar);
            else
                correios = new Correios(getApplicationContext(),this,Correios.SEDEX,et_cep.getText().toString(),
                        carrinho,tv_tempo,tv_frete,tv_total,btn_avancar);

            tv_frete.setVisibility(View.VISIBLE);
            tv_total.setVisibility(View.VISIBLE);
            tv_labeltotal.setVisibility(View.VISIBLE);
            tv_labelfrete.setVisibility(View.VISIBLE);
            tv_tempo.setVisibility(View.VISIBLE);
            et_cep.clearFocus();

        }else
            Toast.makeText(getApplicationContext(),"CEP inválido",Toast.LENGTH_LONG).show();
    }

    //limpa todos os campos e esconde o botão avançar
    private void reset(){
        tv_frete.setVisibility(View.GONE);
        tv_total.setVisibility(View.GONE);
        tv_labeltotal.setVisibility(View.GONE);
        tv_labelfrete.setVisibility(View.GONE);
        tv_tempo.setVisibility(View.GONE);
        btn_avancar.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        if(usuario == null)
            inflater.inflate(R.menu.menu_logout_icons, menu);
        else
            inflater.inflate(R.menu.menu_login_icons, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuActions action = new MenuActions(getApplicationContext(),this);
        switch (item.getItemId()) {
            case R.id.nav_home:
                action.goHome();
                break;
            case R.id.nav_register:
                action.partiuCadastro();
                break;
            case R.id.nav_out:
                action.logout();
                break;
            case R.id.nav_profile:
                Toast.makeText(getApplicationContext(),"editar perfil",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_cart:
                //action.goCarrinho();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


}
