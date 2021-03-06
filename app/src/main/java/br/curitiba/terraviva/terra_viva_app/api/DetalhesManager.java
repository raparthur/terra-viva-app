package br.curitiba.terraviva.terra_viva_app.api;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.Session;
import br.curitiba.terraviva.terra_viva_app.adapter.ProdListCell;
import br.curitiba.terraviva.terra_viva_app.model.Compra;
import br.curitiba.terraviva.terra_viva_app.model.Produto;
import br.curitiba.terraviva.terra_viva_app.activities.DetalhesFragment;
import br.curitiba.terraviva.terra_viva_app.volley.Volley;
import br.curitiba.terraviva.terra_viva_app.volley.VolleyCallback;

import static br.curitiba.terraviva.terra_viva_app.Util.formatCurrency;

public class DetalhesManager {
    private Context ctx;
    private Activity activity;
    private ListView lv_relacionados;
    private List<Produto> produtos;
    private static final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    private ProgressDialog pDialog;

    public DetalhesManager(final Context ctx, Activity activity, View view) {
        this.ctx = ctx;
        this.activity = activity;
        lv_relacionados = view.findViewById(R.id.lv_relacionados);
    }

    //apresenta a lista de produto recomendados (nesse caso, simplesmente aqueles que estão na mesma subcategoria)
    public void getRecomendados(final Produto produto){

        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Buscando produtos...");
        pDialog.setCancelable(false);
        pDialog.show();

        final Volley volley = new Volley(ctx, "https://terraviva.curitiba.br/api/prod_por_subcat/" + produto.getCateg());
        String[] items = {"id","nome","desc_curta","desc_longa","subcateg","valor","img","estoque"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {

                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();

                produtos = new ArrayList<>();
                for (HashMap<String,String> hash : response) {
                    //evitar que o próprio produto em foco apareça na lista de recomendados
                    if(hash != null && Integer.parseInt(hash.get("id")) != produto.getId()){
                        Produto p = new Produto();
                        p.setId(Integer.parseInt(hash.get("id")));
                        p.setNome(hash.get("nome"));
                        p.setCurta(hash.get("desc_curta"));
                        p.setLonga(hash.get("desc_longa"));
                        p.setValor(Float.parseFloat(hash.get("valor")));
                        p.setCateg(Integer.parseInt(hash.get("subcateg")));
                        p.setImg(hash.get("img"));
                        p.setEstoque(Integer.parseInt(hash.get("estoque")));

                        produtos.add(p);
                    }
                }

                final Integer[] ids_prod = new Integer[produtos.size()];
                final String[] nomes = new String[produtos.size()];
                final String[] curtas = new String[produtos.size()];
                final String[] images = new String[produtos.size()];
                final String[] valores = new String[produtos.size()];

                int i = 0;
                for (Produto produto : produtos) {
                    if(produto != null){
                        ids_prod[i] = produto.getId();
                        nomes[i] = produto.getNome();
                        curtas[i] = produto.getCurta();
                        images[i] = produto.getImg();

                        if(produto.getEstoque() > 0){
                            valores[i] = formatCurrency(produto.getValor());
                        }else
                            valores[i] = "esgotado";


                        i++;
                    }
                }
                ProdListCell adapter = new ProdListCell(activity,nomes,curtas,valores,images);
                lv_relacionados.setAdapter(adapter);

                //definir tamanho listview
                ViewGroup.LayoutParams lp = lv_relacionados.getLayoutParams();
                lp.height = getItemHeightofListView(lv_relacionados,produtos.size())/2;
                lv_relacionados.setLayoutParams(lp);

                lv_relacionados.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        AppCompatActivity activity = (AppCompatActivity) ctx;
                        final FragmentManager fragmentManager = activity.getSupportFragmentManager();

                        //quando novo produto é clicado nos recomendados, pega o produto clicado, seta na sessao, atualiza o estoque e chama novo fragmento
                        final Fragment argumentFragment = new DetalhesFragment();//Get Fragment Instance
                        Bundle data = new Bundle();//Use bundle to pass data
                        data.putSerializable("produto", produtos.get(position));
                        getEstoque(produto);
                        argumentFragment.setArguments(data);
                        fragmentManager.beginTransaction().replace(R.id.frame_container, argumentFragment).commit();
                    }
                });
            }

            @Override
            public void onError(String error) {

                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        });
    }
/*
    //atualiza o carrino no servidor
    public void setCompras(){
        final Volley volley = new Volley(ctx, "https://terraviva.curitiba.br/api/listar_compras/"+Session.usuario.getEmail(),activity);
        String[] items = {"compra","produto","nome","desc_curta","desc_longa","subcateg","valor","img","estoque","qtd"};
        volley.getRequest(items, new VolleyCallback() {

            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {
                if(response.size() > 0){
                    Session.compras = new ArrayList<>();
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
                        Session.compras.add(c);
                    }
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }
    */
    //busca o estoque atualizado do produto no servidor a fim de manter o sistema atualizado se ainda é possivel comprar o produto
    public void getEstoque(final Produto produto){
        Volley volley = new Volley(ctx,"https://terraviva.curitiba.br/api/produto/"+produto.getId());
        String[] items = {"estoque"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {
                if (response.size() > 0) {
                    produto.setEstoque(Integer.parseInt(response.get(0).get("estoque")));
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    // To calculate the total height of all items in ListView call with items = adapter.getCount()
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
