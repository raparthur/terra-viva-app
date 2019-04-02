package br.curitiba.terraviva.terra_viva_app.connexion;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.adapter.ProdAdapter;
import br.curitiba.terraviva.terra_viva_app.adapter.RecyclerViewAdapter;
import br.curitiba.terraviva.terra_viva_app.model.Categoria;
import br.curitiba.terraviva.terra_viva_app.model.Produto;

public class JSONManager {
    private Volley volley;
    private List<Produto> produtos;
    private List<Categoria> categorias;
    private ListView list;
    TextView tv_titulo;
    private Context ctx;
    private Activity activity;
    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    private ArrayList<Integer> mIds = new ArrayList<>();
    private View view;
    private static final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
    private static final String TAG = "HomeFragment";

    public JSONManager(Context ctx, Activity activity, View view){
        this.ctx = ctx;
        this.activity = activity;
        this.view = view;
        tv_titulo = view.findViewById(R.id.tv_titulo);
    }

   public void getCategorias(){
       volley = new Volley(ctx,"https://terraviva.curitiba.br/api/categorias");
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
                   //todo
                   initRecycler(categorias);
                   getDestaques();
               }
           }
       });
   }

    public void getDestaques(){
        volley = new Volley(ctx,"https://terraviva.curitiba.br/api/destaques");
        String[] items = {"id","nome","desc_curta","desc_longa","subcateg","valor","img"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {

                final int total = response.size();
                tv_titulo.setText("Destaques ("+total+")");
                if(response.size() > 0){
                    final Integer[] ids_prod = new Integer[response.size()];
                    final String[] nomes = new String[response.size()];
                    final String[] curtas = new String[response.size()];
                    final String[] images = new String[response.size()];
                    final String[] valores = new String[response.size()];
                    int i = 0;
                    produtos = new ArrayList<>();
                    for (HashMap<String,String> hash : response) {
                        if(hash != null){
                            Produto p = new Produto();
                            p.setId(Integer.parseInt(hash.get("id")));
                            p.setNome(hash.get("nome"));
                            p.setCurta(hash.get("desc_curta"));
                            p.setLonga(hash.get("desc_longa"));
                            p.setValor(Float.parseFloat(hash.get("valor")));
                            p.setImg(hash.get("img"));

                            ids_prod[i] = Integer.parseInt(hash.get("id"));
                            nomes[i] = hash.get("nome");
                            curtas[i] = hash.get("desc_curta");
                            images[i] = hash.get("img");
                            valores[i] = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"))
                                    .format(Float.parseFloat(hash.get("valor")));

                            i++;
                            produtos.add(p);
                        }
                    }

                    ProdAdapter adapter = new ProdAdapter(activity,nomes,curtas,valores,images);
                    list = view.findViewById(R.id.lv_prod);

                    list.setAdapter(adapter);

                    //definir tamanho listview
                    ViewGroup.LayoutParams lp = list.getLayoutParams();
                    lp.height = getItemHeightofListView(list,total);
                    list.setLayoutParams(lp);

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(ctx,"Id:  "+ids_prod[+position],Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    public void getProdutos(int id_categoria){

        volley = new Volley(ctx,"https://terraviva.curitiba.br/api/prod_por_categ/"+id_categoria);
        String[] items = {"id","nome","desc_curta","desc_longa","subcateg","valor","img"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {

                final int total = response.size();
                if(response.size() > 0){
                    final Integer[] ids_prod = new Integer[response.size()];
                    final String[] nomes = new String[response.size()];
                    final String[] curtas = new String[response.size()];
                    final String[] images = new String[response.size()];
                    final String[] valores = new String[response.size()];
                    int i = 0;
                    produtos = new ArrayList<>();
                    for (HashMap<String,String> hash : response) {
                        if(hash != null){
                            Produto p = new Produto();
                            p.setId(Integer.parseInt(hash.get("id")));
                            p.setNome(hash.get("nome"));
                            p.setCurta(hash.get("desc_curta"));
                            p.setLonga(hash.get("desc_longa"));
                            p.setValor(Float.parseFloat(hash.get("valor")));
                            p.setImg(hash.get("img"));

                            ids_prod[i] = Integer.parseInt(hash.get("id"));
                            nomes[i] = hash.get("nome");
                            curtas[i] = hash.get("desc_curta");
                            images[i] = hash.get("img");
                            valores[i] = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"))
                                    .format(Float.parseFloat(hash.get("valor")));

                            i++;
                            produtos.add(p);
                        }
                    }


                    //todo
                    ProdAdapter adapter = new ProdAdapter(activity,nomes,curtas,valores,images);
                    list = view.findViewById(R.id.lv_prod);

                    list.setAdapter(adapter);

                    //definir tamanho listview
                    ViewGroup.LayoutParams lp = list.getLayoutParams();
                    lp.height = getItemHeightofListView(list,total);
                    list.setLayoutParams(lp);
                    tv_titulo.setText(tv_titulo.getText()+" ("+total+")");

                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(ctx,"Id:  "+ids_prod[+position],Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    String[] nomes = {},curtas = {},valores = {}, images = {};
                    ProdAdapter adapter = new ProdAdapter(activity,nomes,curtas,valores,images);
                    list = view.findViewById(R.id.lv_prod);

                    list.setAdapter(adapter);
                    Toast.makeText(ctx,"Nenhum produto ainda cadastrado",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void getProdutosPesquisa(String termo){

        volley = new Volley(ctx,"https://terraviva.curitiba.br/api/pesquisa/"+termo);
        String[] items = {"id","nome","desc_curta","desc_longa","subcateg","valor","img"};
        volley.getRequest(items, new VolleyCallback() {
            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {

                final int total = response.size();
                if(response.size() > 0){
                    final Integer[] ids_prod = new Integer[response.size()];
                    final String[] nomes = new String[response.size()];
                    final String[] curtas = new String[response.size()];
                    final String[] images = new String[response.size()];
                    final String[] valores = new String[response.size()];
                    int i = 0;
                    produtos = new ArrayList<>();
                    for (HashMap<String,String> hash : response) {
                        if(hash != null){
                            Produto p = new Produto();
                            p.setId(Integer.parseInt(hash.get("id")));
                            p.setNome(hash.get("nome"));
                            p.setCurta(hash.get("desc_curta"));
                            p.setLonga(hash.get("desc_longa"));
                            p.setValor(Float.parseFloat(hash.get("valor")));
                            p.setImg(hash.get("img"));

                            ids_prod[i] = Integer.parseInt(hash.get("id"));
                            nomes[i] = hash.get("nome");
                            curtas[i] = hash.get("desc_curta");
                            images[i] = hash.get("img");
                            valores[i] = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"))
                                    .format(Float.parseFloat(hash.get("valor")));

                            i++;
                            produtos.add(p);
                        }
                    }

                    //todo
                    ProdAdapter adapter = new ProdAdapter(activity,nomes,curtas,valores,images);
                    list = view.findViewById(R.id.lv_prod);
                    list.setAdapter(adapter);

                    //definir tamanho listview
                    ViewGroup.LayoutParams lp = list.getLayoutParams();
                    lp.height = getItemHeightofListView(list,total);
                    list.setLayoutParams(lp);
                    tv_titulo.setText(tv_titulo.getText()+" ("+total+")");
                    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(ctx,"TotaL: "+total+" - Id:  "+ids_prod[+position],Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    String[] nomes = {},curtas = {},valores = {}, images = {};
                    ProdAdapter adapter = new ProdAdapter(activity,nomes,curtas,valores,images);
                    list = view.findViewById(R.id.lv_prod);

                    list.setAdapter(adapter);
                    Toast.makeText(ctx,"Nenhum produto encontrado",Toast.LENGTH_LONG).show();
                }
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

    private void initRecycler(List<Categoria> categorias){
        Log.d(TAG, "initImageBitmaps: preparing bitmaps.");

        for(Categoria c : categorias){
            mImageUrls.add(c.getImg());
            mNames.add(c.getNome());
            mIds.add(c.getId());
        }

        Log.d(TAG, "initRecyclerView: init recyclerview");

        LinearLayoutManager layoutManager = new LinearLayoutManager(ctx, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(ctx, mNames, mImageUrls,mIds,activity,view);
        recyclerView.setAdapter(adapter);
    }
}
