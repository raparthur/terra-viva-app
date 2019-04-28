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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import br.curitiba.terraviva.terra_viva_app.MaskEditUtil;
import br.curitiba.terraviva.terra_viva_app.MenuActions;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.Util;
import br.curitiba.terraviva.terra_viva_app.api.Viacep;
import br.curitiba.terraviva.terra_viva_app.model.Usuario;

import static br.curitiba.terraviva.terra_viva_app.Session.usuario;
import static br.curitiba.terraviva.terra_viva_app.Util.dateToStr;
import static br.curitiba.terraviva.terra_viva_app.Util.isEmail;
import static br.curitiba.terraviva.terra_viva_app.Util.isEmpty;
import static br.curitiba.terraviva.terra_viva_app.Util.isValid;

public class PessoaisActivity extends AppCompatActivity {

    private EditText et_nome, et_email, et_nasc, et_tel, et_cel, et_cpf, et_senha, et_confirm;
    private TextView label_senha, label_confirm;
    private RadioGroup rg_tel;
    private RadioButton rb_fixo, rb_cel;
    private Button avancar;
    private ImageView progress;

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
        et_senha = findViewById(R.id.et_senha_p);
        et_confirm = findViewById(R.id.et_confirm_p);

        label_confirm = findViewById(R.id.tv_label_confirm_p);
        label_senha = findViewById(R.id.tv_label_senha);

        rg_tel = findViewById(R.id.rg_pessoais);
        rb_cel = findViewById(R.id.rb_cel_pessoais);
        rb_fixo = findViewById(R.id.rb_fixo_pessoais);

        avancar = findViewById(R.id.btn_avancar_pessoais);

        progress = findViewById(R.id.img_progress_p);

        et_cel.setVisibility(View.GONE);
        rb_fixo.setChecked(true);

        et_cpf.addTextChangedListener(MaskEditUtil.mask(et_cpf, MaskEditUtil.FORMAT_CPF));
        et_nasc.addTextChangedListener(MaskEditUtil.mask(et_nasc, MaskEditUtil.FORMAT_DATE));
        et_tel.addTextChangedListener(MaskEditUtil.mask(et_tel, MaskEditUtil.FORMAT_FIXO));
        et_cel.addTextChangedListener(MaskEditUtil.mask(et_cel, MaskEditUtil.FORMAT_CEL));

        rb_cel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                et_cel.setVisibility(View.VISIBLE);
                et_tel.setVisibility(View.GONE);
            }
        });

        rb_fixo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                et_tel.setVisibility(View.VISIBLE);
                et_cel.setVisibility(View.GONE);
            }
        });

        if (usuario != null) {
            Intent prev = getIntent();
            if (prev == null || prev.getExtras() == null) {
                progress.setVisibility(View.INVISIBLE);
            }
            label_senha.setVisibility(View.GONE);
            label_confirm.setVisibility(View.GONE);
            et_confirm.setVisibility(View.GONE);
            et_senha.setVisibility(View.GONE);
            //decide se o tel cadastrado é fixo ou celular
            if (usuario.getTel().length() == 8) {
                et_cel.setVisibility(View.GONE);
                et_tel.setVisibility(View.VISIBLE);
                et_tel.setText("(" + usuario.getDdd() + ")" + usuario.getTel());
                rb_fixo.setChecked(true);
            } else {
                et_tel.setVisibility(View.GONE);
                et_cel.setVisibility(View.VISIBLE);
                et_cel.setText("(" + usuario.getDdd() + ")" + usuario.getTel());
                rb_cel.setChecked(true);
            }

            et_nome.setText(usuario.getNome());
            et_email.setText(usuario.getEmail());
            et_nasc.setText(dateToStr(usuario.getNasc(), "dd/MM/yyyy"));
            et_cpf.setText(usuario.getCpf());
        } else {
            progress.setVisibility(View.INVISIBLE);
        }

        avancar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Util.isEmpty(et_nome) && Util.isCPF(et_cpf) && isEmail(et_email) && isValid(10, "Data inválida", et_nasc)) {
                    if ((rb_cel.isChecked() && Util.isValid(14, "Formato incorreto de celular", et_cel) ||
                            rb_fixo.isChecked() && Util.isValid(13, "Formato incorreto de telefone", et_tel))) {

                        Usuario u = new Usuario();
                        u.setEmail(et_email.getText().toString());
                        u.setNome(et_nome.getText().toString());
                        u.setCpf(Util.clearCpf(et_cpf.getText().toString()));
                        String ddd, tel;
                        if (rb_fixo.isChecked()) {
                            ddd = et_tel.getText().toString().substring(1, 2);
                            tel = et_tel.getText().toString().substring(4);
                        } else {
                            ddd = et_cel.getText().toString().substring(1, 2);
                            tel = et_cel.getText().toString().substring(4);
                        }
                        u.setTel(tel);
                        u.setDdd(ddd);
                        u.setNasc(Util.strToDate(et_nasc.getText().toString(), "dd/MM/yyyy"));
                        Intent it = new Intent(getApplicationContext(), EnderecoActivity.class);
                        if (usuario != null) {
                            Intent old = getIntent();
                            Bundle bundle = new Bundle();
                            if(old != null){
                                Bundle cep = old.getExtras();
                                if(cep != null){

                                        bundle.putString("cep",cep.getString("cep"));

                                    }
                                }
                            bundle.putSerializable("user", u);
                            it.putExtras(bundle);
                            startActivity(it);
                        } else {
                            if (Util.isValidPwd(et_senha, et_confirm)) {
                                u.setSenha((et_senha.getText().toString()));
                                Bundle data = new Bundle();
                                data.putSerializable("user", u);
                                it.putExtras(data);
                                startActivity(it);
                            }
                        }
                    }
                }
            }
        });
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
                Toast.makeText(getApplicationContext(),"editar perfil",Toast.LENGTH_SHORT).show();
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
