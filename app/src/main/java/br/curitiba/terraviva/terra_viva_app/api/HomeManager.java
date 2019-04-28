package br.curitiba.terraviva.terra_viva_app.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.adapter.ProdListCell;
import br.curitiba.terraviva.terra_viva_app.adapter.RecyclerView;
import br.curitiba.terraviva.terra_viva_app.model.Categoria;
import br.curitiba.terraviva.terra_viva_app.model.Produto;
import br.curitiba.terraviva.terra_viva_app.model.Subcateg;
import br.curitiba.terraviva.terra_viva_app.activities.DetalhesActivity;

import br.curitiba.terraviva.terra_viva_app.volley.Volley;
import br.curitiba.terraviva.terra_viva_app.volley.VolleyCallback;

import static br.curitiba.terraviva.terra_viva_app.Util.formatCurrency;

public class HomeManager {
    private Volley volley;
    private List<Produto> produtos;
    private List<Categoria> categorias;
    private List<Subcateg> subcategs;
    private ListView list;
    private TextView tv_titulo;
    private Spinner dropdown,orderer;
    private TextView tv_subcateg;
    private Context ctx;
    private Activity activity;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<Integer> mIds = new ArrayList<>();
    private static final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    private static final String TAG = "HomeFragment";
    private Categoria selectedCateg;
    private ImageView banner;

    public HomeManager(final Context ctx, Activity activity) {
        this.ctx = ctx;
        this.activity = activity;
        tv_titulo = activity.findViewById(R.id.tv_titulo);
        dropdown = activity.findViewById(R.id.dropdown);
        orderer = activity.findViewById(R.id.ordenar);
        tv_subcateg = activity.findViewById(R.id.tv_subcateg);
        selectedCateg = new Categoria();
        banner = activity.findViewById(R.id.imageView2);
    }


   public void getCategorias(){
       volley = new Volley(ctx,"https://terraviva.curitiba.br/api/categorias",activity);
       String[] items = {"id","nome","img"};
       volley.getRequest(items, new VolleyCallback() {
           @Override
           public void onSuccess(ArrayList<HashMap<String, String>> response) {

               if(response.size() > 0){
                   categorias = new ArrayList<>();
                   for (HashMap<String,String> hash : response) {
                       if(hash != null){
                           Categoria c = new Categoria();
                           c.setId(Integer.parseInt(hash.get("id")));
                           c.setNome(hash.get("nome"));
                           c.setImg(hash.get("img"));

                           categorias.add(c);
                       }
                   }
                   initRecycler(categorias);
                   getDestaques();
               }
           }

           @Override
           public void onError(String error) {

           }
       });
   }

    private void getSubCategorias(final int categoria){
        volley = new Volley(ctx,"https://terraviva.curitiba.br/api/subcategorias/"+categoria,activity);
        String[] items = {"id","nome"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(final ArrayList<HashMap<String, String>> response) {
                subcategs = new ArrayList<>();
                final List<String> nomes = new ArrayList<>();
                if(response.size() > 0){
                    if(response.size() == 1){
                        Subcateg s = new Subcateg();
                        s.setId(Integer.parseInt(response.get(0).get("id")));
                        s.setNome(response.get(0).get("nome"));
                        s.setCat(categoria);

                        subcategs.add(s);
                        nomes.add(response.get(0).get("nome"));
                    }else{
                        nomes.add("SELECIONE");
                        for (HashMap<String,String> hash : response) {
                            if(hash != null){
                                Subcateg s = new Subcateg();
                                s.setId(Integer.parseInt(hash.get("id")));
                                s.setNome(hash.get("nome"));
                                s.setCat(categoria);

                                subcategs.add(s);
                                nomes.add(hash.get("nome"));
                            }
                        }
                    }
                }

                dropdown.setVisibility(View.VISIBLE);
                tv_subcateg.setVisibility(View.VISIBLE);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_dropdown_item, nomes);
                dropdown.setAdapter(adapter);


                dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    boolean carregouPrimeiraVez = false;

                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        if(i != 0){
                            getProdutosSubcat(subcategs.get(i-1));
                            carregouPrimeiraVez = true;

                        }else if(carregouPrimeiraVez)
                          getSubCategorias(subcategs.get(i).getCat());

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void getDestaques(){
        volley = new Volley(ctx,"https://terraviva.curitiba.br/api/destaques",activity);
        String[] items = {"id","nome","desc_curta","desc_longa","subcateg","valor","img","estoque","peso","altura","largura","comprimento"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {


                if(response.size() > 0){

                    produtos = new ArrayList<>();
                    for (HashMap<String,String> hash : response) {
                        if(hash != null){
                            Produto p = new Produto();
                            p.setId(Integer.parseInt(hash.get("id")));
                            p.setNome(hash.get("nome"));
                            p.setCurta(hash.get("desc_curta"));
                            p.setLonga(hash.get("desc_longa"));
                            p.setValor(Float.parseFloat(hash.get("valor")));
                            p.setCateg(Integer.parseInt(hash.get("subcateg")));
                            p.setImg(hash.get("img"));
                            p.setPeso(Float.parseFloat(hash.get("peso")));
                            p.setAltura(Integer.parseInt(hash.get("altura")));
                            p.setLargura(Integer.parseInt(hash.get("largura")));
                            p.setComprimento(Integer.parseInt(hash.get("comprimento")));
                            p.setEstoque(Integer.parseInt(hash.get("estoque")));

                            produtos.add(p);
                        }
                    }

                    orderer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            banner.setVisibility(View.GONE);
                            if(pos == 0){
                                setAdapter(produtos,"Destaques");
                            }
                            if(pos == 1){
                                List<Produto> temp = new ArrayList<>();
                                for (Produto p:produtos) {
                                    if(p.getEstoque() != 0)
                                        temp.add(p);
                                }
                                Collections.sort(temp);
                                List<Produto> inverse = new ArrayList<>();
                                for(int i=0;i<temp.size();i++){
                                    inverse.add(temp.get(temp.size() -1 -i));
                                }
                                setAdapter(inverse,"Destaques");
                            }
                            if(pos == 2){
                                List<Produto> temp = new ArrayList<>();
                                for (Produto p:produtos) {
                                    if(p.getEstoque() != 0)
                                        temp.add(p);
                                }
                                Collections.sort(temp);
                                setAdapter(temp,"Destaques");
                            }
                            if(pos == 3){
                                List<Produto> temp = new ArrayList<>();
                                for (Produto p:produtos) {
                                    if(p.getEstoque() == 0)
                                        temp.add(p);
                                }
                                setAdapter(temp,"Destaques");
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    orderer.setSelection(0);
                    setAdapter(produtos,"Destaques");
                    banner.setVisibility(View.VISIBLE);
                    dropdown.setVisibility(View.GONE);
                    tv_subcateg.setVisibility(View.GONE);

                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }


    public void getProdutosCateg(final int id_categoria){

        volley = new Volley(ctx,"https://terraviva.curitiba.br/api/prod_por_categ/"+id_categoria,activity);
        String[] items = {"id","nome","desc_curta","desc_longa","subcateg","valor","img","estoque","peso","altura","largura","comprimento"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {

                if(response.size() > 0){
                    banner.setVisibility(View.GONE);
                    produtos = new ArrayList<>();
                    for (HashMap<String,String> hash : response) {
                        if(hash != null){
                            Produto p = new Produto();
                            p.setId(Integer.parseInt(hash.get("id")));
                            p.setNome(hash.get("nome"));
                            p.setCurta(hash.get("desc_curta"));
                            p.setLonga(hash.get("desc_longa"));
                            p.setValor(Float.parseFloat(hash.get("valor")));
                            p.setCateg(Integer.parseInt(hash.get("subcateg")));
                            p.setImg(hash.get("img"));
                            p.setPeso(Float.parseFloat(hash.get("peso")));
                            p.setAltura(Integer.parseInt(hash.get("altura")));
                            p.setLargura(Integer.parseInt(hash.get("largura")));
                            p.setComprimento(Integer.parseInt(hash.get("comprimento")));
                            p.setEstoque(Integer.parseInt(hash.get("estoque")));

                            produtos.add(p);
                        }
                    }

                    orderer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            if(pos == 0){
                                setAdapter(produtos,selectedCateg.getNome());
                            }
                            if(pos == 1){
                                List<Produto> temp = new ArrayList<>();
                                for (Produto p:produtos) {
                                    if(p.getEstoque() != 0)
                                        temp.add(p);
                                }
                                Collections.sort(temp);
                                List<Produto> inverse = new ArrayList<>();
                                for(int i=0;i<temp.size();i++){
                                    inverse.add(temp.get(temp.size() -1 -i));
                                }
                                setAdapter(inverse,selectedCateg.getNome());
                            }
                            if(pos == 2){
                                List<Produto> temp = new ArrayList<>();
                                for (Produto p:produtos) {
                                    if(p.getEstoque() != 0)
                                        temp.add(p);
                                }
                                Collections.sort(temp);
                                setAdapter(temp,selectedCateg.getNome());
                            }
                            if(pos == 3){
                                List<Produto> temp = new ArrayList<>();
                                for (Produto p:produtos) {
                                    if(p.getEstoque() == 0)
                                        temp.add(p);
                                }
                                setAdapter(temp,selectedCateg.getNome());
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    selectedCateg.setNome(tv_titulo.getText().toString());

                    setAdapter(produtos,selectedCateg.getNome());

                    dropdown.setVisibility(View.GONE);
                    tv_subcateg.setVisibility(View.GONE);
                    orderer.setSelection(0);


                    selectedCateg.setId(id_categoria);
                    getSubCategorias(id_categoria);
                }else{
                    clearListView("Nenhum produto ainda cadastrado");
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }


    private void getProdutosSubcat(final Subcateg subcateg){
        tv_titulo.setText(selectedCateg.getNome());
        volley = new Volley(ctx,"https://terraviva.curitiba.br/api/prod_por_subcat/"+subcateg.getId(),activity);
        String[] items = {"id","nome","desc_curta","desc_longa","subcateg","valor","img","estoque","peso","altura","largura","comprimento"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {

                if(response.size() > 0){
                    produtos = new ArrayList<>();
                    for (HashMap<String,String> hash : response) {
                        if(hash != null){
                            Produto p = new Produto();
                            p.setId(Integer.parseInt(hash.get("id")));
                            p.setNome(hash.get("nome"));
                            p.setCurta(hash.get("desc_curta"));
                            p.setLonga(hash.get("desc_longa"));
                            p.setValor(Float.parseFloat(hash.get("valor")));
                            p.setCateg(Integer.parseInt(hash.get("subcateg")));
                            p.setImg(hash.get("img"));
                            p.setPeso(Float.parseFloat(hash.get("peso")));
                            p.setAltura(Integer.parseInt(hash.get("altura")));
                            p.setLargura(Integer.parseInt(hash.get("largura")));
                            p.setComprimento(Integer.parseInt(hash.get("comprimento")));
                            p.setEstoque(Integer.parseInt(hash.get("estoque")));

                            produtos.add(p);
                        }
                    }

                    orderer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            if(pos == 0){
                                setAdapter(produtos,selectedCateg.getNome()+"\n - "+subcateg.getNome());
                            }
                            if(pos == 1){
                                List<Produto> temp = new ArrayList<>();
                                for (Produto p:produtos) {
                                    if(p.getEstoque() != 0)
                                        temp.add(p);
                                }
                                Collections.sort(temp);
                                List<Produto> inverse = new ArrayList<>();
                                for(int i=0;i<temp.size();i++){
                                    inverse.add(temp.get(temp.size() -1 -i));
                                }
                                setAdapter(inverse,selectedCateg.getNome()+"\n - "+subcateg.getNome());
                            }
                            if(pos == 2){
                                List<Produto> temp = new ArrayList<>();
                                for (Produto p:produtos) {
                                    if(p.getEstoque() != 0)
                                        temp.add(p);
                                }
                                Collections.sort(temp);
                                setAdapter(temp,selectedCateg.getNome()+"\n - "+subcateg.getNome());
                            }
                            if(pos == 3){
                                List<Produto> temp = new ArrayList<>();
                                for (Produto p:produtos) {
                                    if(p.getEstoque() == 0)
                                        temp.add(p);
                                }
                                setAdapter(temp,selectedCateg.getNome()+"\n - "+subcateg.getNome());
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    selectedCateg.setNome(tv_titulo.getText().toString());

                    setAdapter(produtos,selectedCateg.getNome()+"\n - "+subcateg.getNome());
                    orderer.setSelection(0);

                    getSubCategorias(subcateg.getCat());

                }else{
                    clearListView("Nenhum produto ainda cadastrado");
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void getProdutosPesquisa(final String termo){
        volley = new Volley(ctx,"https://terraviva.curitiba.br/api/pesquisa/"+termo,activity);
        String[] items = {"id","nome","desc_curta","desc_longa","subcateg","valor","img","estoque","peso","altura","largura","comprimento"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {

                if(response.size() > 0){
                    banner.setVisibility(View.GONE);
                    produtos = new ArrayList<>();
                    for (HashMap<String,String> hash : response) {
                        if(hash != null){
                            Produto p = new Produto();
                            p.setId(Integer.parseInt(hash.get("id")));
                            p.setNome(hash.get("nome"));
                            p.setCurta(hash.get("desc_curta"));
                            p.setLonga(hash.get("desc_longa"));
                            p.setValor(Float.parseFloat(hash.get("valor")));
                            p.setCateg(Integer.parseInt(hash.get("subcateg")));
                            p.setImg(hash.get("img"));
                            p.setPeso(Float.parseFloat(hash.get("peso")));
                            p.setAltura(Integer.parseInt(hash.get("altura")));
                            p.setLargura(Integer.parseInt(hash.get("largura")));
                            p.setComprimento(Integer.parseInt(hash.get("comprimento")));
                            p.setEstoque(Integer.parseInt(hash.get("estoque")));

                            produtos.add(p);
                        }
                    }

                    orderer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
                            if(pos == 0){
                                setAdapter(produtos,"Pesquisa por \""+termo+"\"");
                            }
                            if(pos == 1){
                                List<Produto> temp = new ArrayList<>();
                                for (Produto p:produtos) {
                                    if(p.getEstoque() != 0)
                                        temp.add(p);
                                }
                                Collections.sort(temp);
                                List<Produto> inverse = new ArrayList<>();
                                for(int i=0;i<temp.size();i++){
                                    inverse.add(temp.get(temp.size() -1 -i));
                                }
                                setAdapter(inverse,"Pesquisa por \""+termo+"\"");
                            }
                            if(pos == 2){
                                List<Produto> temp = new ArrayList<>();
                                for (Produto p:produtos) {
                                    if(p.getEstoque() != 0)
                                        temp.add(p);
                                }
                                Collections.sort(temp);
                                setAdapter(temp,"Pesquisa por \""+termo+"\"");
                            }
                            if(pos == 3){
                                List<Produto> temp = new ArrayList<>();
                                for (Produto p:produtos) {
                                    if(p.getEstoque() == 0)
                                        temp.add(p);
                                }
                                setAdapter(temp,"Pesquisa por \""+termo+"\"");
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                    setAdapter(produtos,tv_titulo.getText().toString());
                    orderer.setSelection(0);

                    dropdown.setVisibility(View.GONE);
                    tv_subcateg.setVisibility(View.GONE);

                }else{
                    clearListView("Nenhum produto para o termo pesquisado");
                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void setAdapter(final List<Produto> produtos, String titulo){
        final Integer[] ids_prod = new Integer[produtos.size()];
        final String[] nomes = new String[produtos.size()];
        final String[] curtas = new String[produtos.size()];
        final String[] images = new String[produtos.size()];
        final String[] valores = new String[produtos.size()];

        tv_titulo.setText(titulo+" ("+produtos.size()+")");

        int i = 0;
        for (Produto produto : produtos) {
            if(produto != null){
                ids_prod[i] = produto.getId();
                nomes[i] = produto.getNome();
                curtas[i] = produto.getCurta();
                images[i] = produto.getImg();
                if(produto.getEstoque() == 0)
                    valores[i] = "esgotado";
                else{
                    valores[i] = formatCurrency(produto.getValor());
                }
                i++;
            }
        }
        ProdListCell adapter = new ProdListCell(activity,nomes,curtas,valores,images);
        list = activity.findViewById(R.id.lv_prod);
        list.setAdapter(adapter);

        //definir tamanho listview
        ViewGroup.LayoutParams lp = list.getLayoutParams();
        lp.height = getItemHeightofListView(list,produtos.size());
        list.setLayoutParams(lp);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Bundle data = new Bundle();//Use bundle to pass data
                data.putSerializable("produto", produtos.get(position));
                Intent it = new Intent(ctx,DetalhesActivity.class);
                it.putExtras(data);
                activity.startActivity(it);

            }
        });
    }

    //busca o estoque do produto naquele momento (é uma forma de manter mais ou menos o estoque atualizado,
    // informa o sistema caso algum usuário tenha eventualmente comprado este produto e o estoque tenha se esgotado -
    // o sistema tem verificação antes de cada compra pra mostrar "indisponivel" se o estoque zerou)
    public void getEstoque(final Produto produto){
        Volley volley = new Volley(ctx,"https://terraviva.curitiba.br/api/produto/"+produto.getId(),activity);
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


    private void clearListView(String message){
        String[] nomes = {},curtas = {},valores = {}, images = {};
        ProdListCell adapter = new ProdListCell(activity,nomes,curtas,valores,images);
        list = activity.findViewById(R.id.lv_prod);

        dropdown.setVisibility(View.GONE);
        tv_subcateg.setVisibility(View.GONE);

        list.setAdapter(adapter);
        Toast.makeText(ctx,message,Toast.LENGTH_LONG).show();
    }


    // calcular o tamanho que vai ter a listview que ira ocupar a tela com base na qtd de itens
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

    private void initRecycler(List<Categoria> categorias){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        for(Categoria c : categorias){
            mImageUrls.add(c.getImg());
            mNames.add(c.getNome());
            mIds.add(c.getId());
        }

        Log.d(TAG, "initRecyclerView: init recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
        android.support.v7.widget.RecyclerView recyclerView = activity.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerView adapter = new RecyclerView(ctx, mNames, mImageUrls,mIds,activity);
        recyclerView.setAdapter(adapter);
    }

}
