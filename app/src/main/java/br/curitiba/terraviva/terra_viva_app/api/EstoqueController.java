package br.curitiba.terraviva.terra_viva_app.api;

import android.app.Activity;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.curitiba.terraviva.terra_viva_app.Session;
import br.curitiba.terraviva.terra_viva_app.model.Compra;
import br.curitiba.terraviva.terra_viva_app.model.Produto;
import br.curitiba.terraviva.terra_viva_app.volley.Volley;
import br.curitiba.terraviva.terra_viva_app.volley.VolleyCallback;

public class EstoqueController {
    private Context context;
    private Activity activity;

    public EstoqueController(Context context, Activity activity){
        this.context = context;
        this.activity = activity;
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
                    HomeManager manager = new HomeManager(context,activity);
                    atualizaListaCompra();
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
                    HomeManager manager = new HomeManager(context,activity);
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
                    atualizaListaCompra();
                }
                else
                    Toast.makeText(context,"Problemas! produto: "+produto+" - qtd: "+qtd,Toast.LENGTH_LONG).show();
            }
        });
    }

    //salva o status atual do carrinho de compras no servidor
    public void atualizaListaCompra(){
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
            }
        });
    }

}
