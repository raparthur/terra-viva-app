package br.curitiba.terraviva.terra_viva_app.connexion;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.Session;
import br.curitiba.terraviva.terra_viva_app.model.Compra;
import br.curitiba.terraviva.terra_viva_app.model.Produto;
import br.curitiba.terraviva.terra_viva_app.model.Usuario;
import br.curitiba.terraviva.terra_viva_app.view.DetailsActivity;
import br.curitiba.terraviva.terra_viva_app.view.HomeActivity;

public class PostManager {
    private Context context;
    private Activity activity;

    public PostManager(Context context,Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void login(String email,String senha){
        Map<String,String> params = new HashMap<>();
        params.put("email", email);
        params.put("pwd", senha);
        Volley volley = new Volley(context,"https://terraviva.curitiba.br/user/login",params, activity);
        String[] dados = {"email","nome","cpf","rua","num","compl","bairro","cidade","uf","nasc"};
        volley.postRequest(dados, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {
                if(response.size() > 0){
                    Session.usuario = new Usuario();
                    Session.usuario.setNome(response.get(0).get("nome"));
                    Session.usuario.setEmail(response.get(0).get("email"));
                    Session.usuario.setCpf(response.get(0).get("cpf"));
                    Session.usuario.setRua(response.get(0).get("rua"));
                    Session.usuario.setNum(response.get(0).get("num"));
                    Session.usuario.setCompl(response.get(0).get("compl"));
                    Session.usuario.setBairro(response.get(0).get("bairro"));
                    Session.usuario.setCidade(response.get(0).get("cidade"));
                    Session.usuario.setUf(response.get(0).get("uf"));

                    try {
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd",new Locale("pt","BR"));
                        Date date = format.parse(response.get(0).get("nasc"));
                        Session.usuario.setNasc(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        Session.usuario = null;
                    }
                }
                if(Session.usuario == null){
                    TextView erro = activity.findViewById(R.id.tv_erro_login);
                    erro.setText("Credenciais incorretas");
                    erro.setTextColor(Color.RED);
                }
                else{
                    Intent details = activity.getIntent();
                    if(details != null){
                        Bundle bundle = details.getExtras();
                        if(bundle != null){
                            Session.produto = (Produto)bundle.getSerializable("produto");
                            JsonHomeManager homeManager = new JsonHomeManager(context,activity);
                            homeManager.atualizaCarrinho();
                            Toast.makeText(context,"prod: "+Session.produto.getId(),Toast.LENGTH_LONG).show();
                            //inserir nova HomeActivity (agora com usuario logado) por baixo da pilha antes de ir para DetailsActivity
                            //(evita que apareça a HomeActivity que ainda não estava logada)
                            Intent it = new Intent(context,HomeActivity.class);
                            context.startActivity(it);
                            //vai para DetaisActivity com o ultimo produto clicado em foco.
                            Bundle data = new Bundle();
                            data.putSerializable("produto", Session.produto);
                            it = new Intent(context,DetailsActivity.class);
                            it.putExtras(data);
                            activity.startActivity(it);
                        }
                        else{
                            Intent it = new Intent(context,HomeActivity.class);
                            context.startActivity(it);
                        }
                    }
                    else{
                        Intent it = new Intent(context,HomeActivity.class);
                        context.startActivity(it);
                    }
                    activity.finish();
                }
            }
        });
    }
    public void comprar(final Produto produto, final int qtd, final Button btn_cart){
        Map<String,String> params = new HashMap<>();
        params.put("email", Session.usuario.getEmail());
        params.put("prod", String.valueOf(produto.getId()));
        params.put("qtd", String.valueOf(qtd));

        Volley volley = new Volley(context,"https://terraviva.curitiba.br/user/comprar",params, activity);
        String[] dados = {"id"};
        volley.postRequest(dados, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {

                if(Integer.parseInt(response.get(0).get("id")) > 0){
                    btn_cart.setText("Remover do\n carrinho");
                    btn_cart.setBackgroundColor(0xffcc0000);
                    atualizaEstoque(produto.getId(), produto.getEstoque() - qtd);
                    //Toast.makeText(context,"Produto adicionado ao carrinho",Toast.LENGTH_LONG).show();
                    JsonHomeManager manager = new JsonHomeManager(context,activity);
                    manager.atualizaCarrinho();
                }
                else
                    Toast.makeText(context,"Problemas ao efetuar compra - verifique sua conexão!",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void desfazCompra(final Compra compra, final Button btn_cart,final TextView tv_qtd){
        Map<String,String> params = new HashMap<>();
        params.put("id", String.valueOf(compra.getId()));

        Volley volley = new Volley(context,"https://terraviva.curitiba.br/user/desfaz_compra",params, activity);
        String[] dados = {"affected"};
        volley.postRequest(dados, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {

                if(Integer.parseInt(response.get(0).get("affected")) > 0){
                    btn_cart.setText("Comprar");
                    btn_cart.setBackgroundColor(0xff8b4513);
                    tv_qtd.setText("x 1");
                    atualizaEstoque(compra.getProduto().getId(), compra.getQtd() + compra.getProduto().getEstoque());
                    Toast.makeText(context,"Produto removido do carrinho",Toast.LENGTH_LONG).show();
                    JsonHomeManager manager = new JsonHomeManager(context,activity);
                    manager.getEstoque(Session.produto);
                }
                else
                    Toast.makeText(context,"Problemas ao atualizar carrinho - tente novamente!",Toast.LENGTH_LONG).show();
            }
        });
    }

    public void atualizaEstoque(final int produto,final int qtd){
        Map<String,String> params = new HashMap<>();
        params.put("produto", String.valueOf(produto));
        params.put("qtd", String.valueOf(qtd));

        Volley volley = new Volley(context,"https://terraviva.curitiba.br/user/atualiza_estoque",params, activity);
        String[] dados = {"affected"};
        volley.postRequest(dados, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {

                if(Integer.parseInt(response.get(0).get("affected")) > 0){
                    Toast.makeText(context,"quantidade atualizada",Toast.LENGTH_LONG).show();
                    JsonHomeManager manager = new JsonHomeManager(context,activity);
                    manager.atualizaCarrinho();
                }
                else
                    Toast.makeText(context,"Problemas! produto: "+produto+" - qtd: "+qtd,Toast.LENGTH_LONG).show();
            }
        });
    }

}
