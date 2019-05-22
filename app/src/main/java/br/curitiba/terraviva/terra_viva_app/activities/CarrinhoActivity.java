package br.curitiba.terraviva.terra_viva_app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import br.curitiba.terraviva.terra_viva_app.MaskEditUtil;
import br.curitiba.terraviva.terra_viva_app.MenuActions;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.Session;
import br.curitiba.terraviva.terra_viva_app.adapter.CarrinhoListCell;
import br.curitiba.terraviva.terra_viva_app.api.Correios;
import br.curitiba.terraviva.terra_viva_app.api.Viacep;
import br.curitiba.terraviva.terra_viva_app.model.Compra;
import static br.curitiba.terraviva.terra_viva_app.Session.compras;
import static br.curitiba.terraviva.terra_viva_app.Session.usuario;
import static br.curitiba.terraviva.terra_viva_app.Util.formatCurrency;

public class CarrinhoActivity extends AppCompatActivity {

    private TextView tv_frete;
    private TextView tv_total;
    private TextView tv_labelfrete;
    private TextView tv_labeltotal;
    private TextView tv_tempo;
    private EditText et_cep;
    private Button btn_avancar;
    private float carrinho=0;
    private Correios correios;
    private RadioButton rbPac,rbSedex;
    private Viacep viacep;
    private static final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrinho);

        setAdapter(Session.compras);

        TextView tv_produtos = findViewById(R.id.tv_carrinhoprodutos);
        tv_frete = findViewById(R.id.tv_frete);
        tv_total = findViewById(R.id.tv_carrinhototal);
        tv_labelfrete = findViewById(R.id.tv_labelfrete);
        tv_labeltotal = findViewById(R.id.tv_labeltotal);
        tv_tempo = findViewById(R.id.tv_tempo);
        et_cep = findViewById(R.id.et_cepcarrinho);
        Button btn_calcular = findViewById(R.id.btn_calcularfrete);
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
                action.partiuCadastro();
                break;
            case R.id.nav_cart:
                action.goCarrinho();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setAdapter(final List<Compra> compras){
        final Integer[] ids_prod = new Integer[compras.size()];
        final String[] nomes = new String[compras.size()];
        final String[] qtd = new String[compras.size()];
        final String[] images = new String[compras.size()];
        final float[] valores = new float[compras.size()];

        int i = 0;
        for (Compra compra : Session.compras) {
            if(compra != null){
                ids_prod[i] = compras.get(i).getProduto().getId();
                nomes[i] = compras.get(i).getProduto().getNome();
                qtd[i] = String.valueOf(compras.get(i).getQtd());
                images[i] = compras.get(i).getProduto().getImg();
                valores[i] = compras.get(i).getProduto().getValor();


                i++;
            }
        }
        CarrinhoListCell adapter = new CarrinhoListCell(this,nomes,valores,qtd,images);
        ListView lv_carrinho = findViewById(R.id.lv_carrinho);
        lv_carrinho.setAdapter(adapter);

        //definir tamanho listview
        ViewGroup.LayoutParams lp = lv_carrinho.getLayoutParams();
        lp.height = getItemHeightofListView(lv_carrinho,compras.size());
        lv_carrinho.setLayoutParams(lp);

        lv_carrinho.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Intent it = new Intent(getApplicationContext(),DetalhesActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("produto",compras.get(position).getProduto());
                it.putExtras(data);
                startActivity(it);
            }
        });
    }

    private static int getItemHeightofListView(ListView listView, int items) {
        ListAdapter adapter = listView.getAdapter();

        int grossElementHeight = 0;
        for (int i = 0; i < items; i++) {
            View childView = adapter.getView(i, null, listView);
            childView.measure(UNBOUNDED, UNBOUNDED);
            grossElementHeight += childView.getMeasuredHeight();
        }
        return grossElementHeight;
    }


}
