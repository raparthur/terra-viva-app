package br.curitiba.terraviva.terra_viva_app.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.Session;
import br.curitiba.terraviva.terra_viva_app.Util;
import br.curitiba.terraviva.terra_viva_app.model.Compra;
import br.curitiba.terraviva.terra_viva_app.model.Produto;
import br.curitiba.terraviva.terra_viva_app.model.Usuario;
import br.curitiba.terraviva.terra_viva_app.activities.DetalhesActivity;
import br.curitiba.terraviva.terra_viva_app.activities.HomeActivity;
import br.curitiba.terraviva.terra_viva_app.volley.Volley;
import br.curitiba.terraviva.terra_viva_app.volley.VolleyCallback;

import static br.curitiba.terraviva.terra_viva_app.Util.strToDate;

public class LoginManager {
    private Context context;
    private Activity activity;

    public LoginManager(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void login(String email,String senha){
    Map<String,String> params = new HashMap<>();
        params.put("email", email);
        params.put("pwd", senha);
    Volley volley = new Volley(context,"https://terraviva.curitiba.br/user/login",params, activity);
    String[] dados = {"email","nome","cpf","rua","num","compl","bairro","cidade","uf","nasc","cep","ddd","tel"};
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
                Session.usuario.setCep(response.get(0).get("cep"));
                Session.usuario.setDdd(response.get(0).get("ddd"));
                Session.usuario.setTel(response.get(0).get("tel"));

                Date date = Util.strToDate(response.get(0).get("nasc"),"yyyy-MM-dd");
                Session.usuario.setNasc(date);


                EstoqueController controller = new EstoqueController(context,activity);
                controller.atualizaListaCompra();

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
                        //inserir nova HomeActivity (agora com usuario logado) por baixo da pilha antes de ir para DetailsActivity
                        //(evita que apareça a HomeActivity que ainda não estava logada)
                        Intent it = new Intent(context,HomeActivity.class);
                        context.startActivity(it);
                        //vai para DetaisActivity com o ultimo produto clicado em foco, armazenado em "Session.produto".
                        redireciona();
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
//alimenta o carrinho de compras, caso tenha ficado algum parado, e redireciona para DetailsActiviy - quando se estava na tela de detalhes e é clicado no botão "entre para comprar". A idéia é voltar para a tela de detalhes onde parou
private void redireciona(){
    final Volley volley = new Volley(context, "https://terraviva.curitiba.br/api/listar_compras/"+Session.usuario.getEmail(),activity);
    String[] items = {"compra","produto","nome","desc_curta","desc_longa","subcateg","valor","img","estoque","qtd"};
    volley.getRequest(items, new VolleyCallback() {

        @Override
        public void onSuccess(ArrayList<HashMap<String, String>> response) {
            if(response.size() > 0){
                List<Compra> compras = new ArrayList<>();
                for (HashMap<String,String> hash : response) {

                    Produto p = new Produto();
                    p.setId(Integer.parseInt(hash.get("produto")));
                    p.setCateg(Integer.parseInt(hash.get("subcateg")));
                    p.setNome(hash.get("nome"));
                    p.setCurta(hash.get("desc_curta"));
                    p.setLonga(hash.get("desc_longa"));
                    p.setValor(Float.parseFloat(hash.get("valor")));
                    p.setEstoque(Integer.parseInt(hash.get("estoque")));
                    p.setImg(hash.get("img"));


                    Compra c = new Compra();
                    c.setId(Integer.parseInt(hash.get("compra")));
                    c.setProduto(p);
                    c.setQtd(Integer.parseInt(hash.get("qtd")));
                    c.setUsuario(Session.usuario.getEmail());
                    compras.add(c);
                }
                Session.compras = compras;

            }

            Bundle data = new Bundle();
            data.putSerializable("produto", Session.produto);
            Intent it = new Intent(context,DetalhesActivity.class);
            it.putExtras(data);
            activity.startActivity(it);
        }
    });
}
}
