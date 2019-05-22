package br.curitiba.terraviva.terra_viva_app.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.curitiba.terraviva.terra_viva_app.MenuActions;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.Util;
import br.curitiba.terraviva.terra_viva_app.api.CadastroManager;
import br.curitiba.terraviva.terra_viva_app.model.Usuario;

import static br.curitiba.terraviva.terra_viva_app.Session.usuario;

public class CadastroActivity extends AppCompatActivity {
    TextView nome,email,tel,nasc,cpf,labelEnd,rua,local,cep,enviado;
    Button conferir,voltar;
    ImageView progress;
    CadastroManager manager;
    MenuActions action;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        nome = findViewById(R.id.tv_conf_nome);
        email = findViewById(R.id.tv_conf_email);
        tel =   findViewById(R.id.tv_conf_tel);
        nasc = findViewById(R.id.tv_conf_nasc);
        cpf = findViewById(R.id.tv_conf_cpf);
        labelEnd = findViewById(R.id.tv_conf_end);
        rua = findViewById(R.id.tv_conf_rua);
        local = findViewById(R.id.tv_conf_local);
        cep = findViewById(R.id.tv_conf_cep);
        enviado = findViewById(R.id.tv_email_enviado);
        conferir = findViewById(R.id.button_conf);
        voltar = findViewById(R.id.btn_voltar_home);
        progress = findViewById(R.id.img_progress_conf);
        action = new MenuActions(getApplicationContext(),this);

        manager = new CadastroManager(getApplicationContext(),this);


        Intent it = getIntent();
        Bundle bundle = it.getExtras();
        if(bundle != null){
            if(bundle.getSerializable("comprauser") != null){
                progress.setVisibility(View.VISIBLE);
                final Usuario user = (Usuario)bundle.getSerializable("comprauser");
                labelEnd.setText("DADOS PARA ENTREGA");
                setDados(user);
                conferir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                      Bundle userdata = new Bundle();
                      userdata.putString("nome_user",user.getNome());
                      userdata.putString("ddd_user",user.getDdd());
                      userdata.putString("tel_user",user.getTel());

                        userdata.putString("cep_user",user.getCep());
                        userdata.putString("rua_user",user.getRua());
                        userdata.putString("num_user",user.getNum());
                        userdata.putString("bairro_user",user.getBairro());
                        userdata.putString("cidade_user",user.getCidade());
                        userdata.putString("uf_user",user.getUf());

                      Intent intent = new Intent(getApplicationContext(),PagtoActivity.class);
                      intent.putExtras(userdata);
                      startActivity(intent);

                    }
                });

            }else
            if(bundle.getSerializable("cadastrouser") != null){
                final Usuario user = (Usuario)bundle.getSerializable("cadastrouser");
                setDados(user);
                conferir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        manager.novoUsuario(user,enviado);
                        conferir.setVisibility(View.GONE);
                        voltar.setVisibility(View.VISIBLE);
                    }
                });
                voltar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        action.goHome();
                        finish();
                    }
                });
            }else
            if(bundle.getSerializable("perfiluser") != null){
                final Usuario user = (Usuario)bundle.getSerializable("perfiluser");
                setDados(user);
                conferir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        manager.atualizaUsuario(user,enviado);
                        conferir.setVisibility(View.GONE);
                        voltar.setVisibility(View.VISIBLE);
                    }
                });
                voltar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        action.goHome();
                        finish();
                    }
                });
            }else
                Toast.makeText(getApplicationContext(),"ERRO",Toast.LENGTH_SHORT).show();
        }else
            Toast.makeText(getApplicationContext(),"ERRO",Toast.LENGTH_SHORT).show();
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

    private void setDados(Usuario usuario){
        nome.setText(usuario.getNome());
        email.setText(usuario.getEmail());
        tel.setText("("+usuario.getDdd()+")"+usuario.getTel());
        nasc.setText(Util.dateToStr(usuario.getNasc(),"dd/MM/yyyy"));
        cpf.setText(Util.imprimeCPF(usuario.getCpf()));
        rua.setText(usuario.getRua()+", "+usuario.getNum()+" - "+usuario.getCompl());
        local.setText(usuario.getBairro()+" - "+usuario.getCidade()+" - "+usuario.getUf());
        cep.setText(usuario.getCep());
    }
}
