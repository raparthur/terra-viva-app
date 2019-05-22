package br.curitiba.terraviva.terra_viva_app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.curitiba.terraviva.terra_viva_app.MaskEditUtil;
import br.curitiba.terraviva.terra_viva_app.MenuActions;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.Util;
import br.curitiba.terraviva.terra_viva_app.api.Viacep;
import br.curitiba.terraviva.terra_viva_app.model.Usuario;

import static br.curitiba.terraviva.terra_viva_app.Session.usuario;

public class EnderecoActivity extends AppCompatActivity {

    private EditText et_rua, et_num, et_compl, et_bairro, et_cidade, et_cep, et_uf;
    private Button avancar;
    Viacep viacep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_endereco);

        TextView tv_titulo = findViewById(R.id.tv_titulo_endereco);
        et_rua = findViewById(R.id.et_rua);
        et_num = findViewById(R.id.et_num);
        et_compl = findViewById(R.id.et_compl);
        et_bairro = findViewById(R.id.et_bairro);
        et_cidade = findViewById(R.id.et_cidade);
        et_cep = findViewById(R.id.et_cep);
        et_uf = findViewById(R.id.et_uf);

        et_rua.setEnabled(false);
        et_cidade.setEnabled(false);
        et_bairro.setEnabled(false);
        et_uf.setEnabled(false);

        et_cep.addTextChangedListener(MaskEditUtil.mask(et_cep, MaskEditUtil.FORMAT_CEP));

        avancar = findViewById(R.id.btn_avancar_entrega);
        Button btn_cep = findViewById(R.id.btn_cep);

        ImageView progress = findViewById(R.id.img_progressend);


        btn_cep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viacep = new Viacep(getApplicationContext(), EnderecoActivity.this,false, et_cep, avancar,
                        et_bairro, et_cidade, et_rua, et_uf, et_num, et_compl);
            }
        });

        if (usuario != null) {
            final Intent it = getIntent();
            if (it != null) {
                Bundle data = it.getExtras();
                /** PROCESSO DE COMPRA
                 */
                if (data != null && data.getString("cep") != null) {
                    progress.setVisibility(View.VISIBLE);
                    tv_titulo.setText("DADOS PARA ENTREGA");
                    et_cep.setText(data.getString("cep"));
                    et_cep.setEnabled(false);
                    btn_cep.setEnabled(false);
                    if (data.getString("cep").equals(usuario.getCep())) {
                        et_bairro.setText(usuario.getBairro());
                        et_cidade.setText(usuario.getCidade());
                        et_compl.setText(usuario.getCompl());
                        et_num.setText(usuario.getNum());
                        et_rua.setText(usuario.getRua());
                        et_uf.setText(usuario.getUf());
                    } else {
                        viacep = new Viacep(getApplicationContext(), EnderecoActivity.this, true,et_cep, avancar, et_bairro,
                                et_cidade, et_rua, et_uf, et_compl, et_num);
                    }
                    avancar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent newIntent = new Intent(getApplicationContext(), CadastroActivity.class);
                            Bundle data = it.getExtras();
                            if (data != null) {
                                Usuario user = (Usuario) data.getSerializable("user");
                                if (user != null) {
                                    if(!Util.isEmpty(et_num,et_compl)) {
                                            Bundle bundle = new Bundle();
                                            user.setRua(et_rua.getText().toString());
                                            user.setUf(et_uf.getText().toString());
                                            user.setCidade(et_cidade.getText().toString());
                                            user.setNum(et_num.getText().toString());
                                            user.setCep(et_cep.getText().toString());
                                            user.setCompl(et_compl.getText().toString());
                                            user.setBairro(et_bairro.getText().toString());
                                            bundle.putSerializable("comprauser", user);
                                            newIntent.putExtras(bundle);
                                            startActivity(newIntent);
                                    }
                                }
                            }
                        }
                    });
                    /** ATUALIZAR PERFIL
                     */
                }else{
                    et_num.setText(usuario.getNum());
                    et_compl.setText(usuario.getCompl());
                    et_bairro.setText(usuario.getBairro());
                    et_cidade.setText(usuario.getCidade());
                    et_rua.setText(usuario.getRua());
                    et_uf.setText(usuario.getUf());
                    et_cep.setText(usuario.getCep());
                    avancar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent newIntent = new Intent(getApplicationContext(), CadastroActivity.class);
                            Bundle data = it.getExtras();
                            if (data != null) {
                                Usuario user = (Usuario) data.getSerializable("user");
                                if (user != null) {
                                    if(!Util.isEmpty(et_num,et_compl)) {
                                        if((viacep == null && usuario.getCep().equals(et_cep.getText().toString().replace("-","")))
                                                || viacep != null && viacep.getCep().equals(et_cep.getText().toString())) {
                                            Bundle bundle = new Bundle();
                                            user.setRua(et_rua.getText().toString());
                                            user.setUf(et_uf.getText().toString());
                                            user.setCidade(et_cidade.getText().toString());
                                            user.setNum(et_num.getText().toString());
                                            user.setCep(et_cep.getText().toString());
                                            user.setCompl(et_compl.getText().toString());
                                            user.setBairro(et_bairro.getText().toString());
                                            bundle.putSerializable("perfiluser", user);
                                            newIntent.putExtras(bundle);
                                            startActivity(newIntent);
                                        }else
                                            Toast.makeText(getApplicationContext(),"CEP alterado. Por favor, valide seu CEP novamente",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        }
                    });
                }
            }
            /** NOVO CADASTRO
             */
        } else {
            et_num.setEnabled(false);
            et_num.setEnabled(false);
            avancar.setEnabled(false);
            final Intent it = getIntent();
            Bundle data = it.getExtras();
            if (data != null) {
                final Usuario user = (Usuario) data.getSerializable("user");
                if (user != null) {
                    avancar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(Util.isValid(9,"CEP inválido",et_cep) && !Util.isEmpty(et_num,et_compl)) {
                                if(viacep.getCep().equals(et_cep.getText().toString())) {
                                    user.setRua(et_rua.getText().toString());
                                    user.setUf(et_uf.getText().toString());
                                    user.setCidade(et_cidade.getText().toString());
                                    user.setNum(et_num.getText().toString());
                                    user.setCep(et_cep.getText().toString());
                                    user.setCompl(et_compl.getText().toString());
                                    user.setBairro(et_bairro.getText().toString());

                                    Intent newIntent = new Intent(getApplicationContext(), CadastroActivity.class);
                                    Bundle data = new Bundle();
                                    data.putSerializable("cadastrouser", user);
                                    newIntent.putExtras(data);
                                    startActivity(newIntent);
                                }else
                                    Toast.makeText(getApplicationContext(),"CEP alterado. Por favor, valide seu CEP novamente",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();

        //conforme logado ou não, apenas alguns ícones dotoolbar irão aparecer (irá "inflar" arquivos de menu diferentes)
        if(usuario == null)
            inflater.inflate(R.menu.menu_logout_icons, menu);
        else
            inflater.inflate(R.menu.menu_login_icons, menu);

        return true;
    }

    //icones da toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        MenuActions action = new MenuActions(getApplicationContext(),this);
        switch (item.getItemId()) {
            case R.id.nav_home:
                action.goHome();
                break;
            case R.id.nav_in:
                Intent it = new Intent(this,LoginActivity.class);
                startActivity(it);
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

}
