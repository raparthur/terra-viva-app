package br.curitiba.terraviva.terra_viva_app.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.Session;
import br.curitiba.terraviva.terra_viva_app.Util;
import br.curitiba.terraviva.terra_viva_app.adapter.CarrinhoListCell;
import br.curitiba.terraviva.terra_viva_app.model.Compra;
import br.curitiba.terraviva.terra_viva_app.activities.DetalhesActivity;
import br.curitiba.terraviva.terra_viva_app.volley.Volley;
import br.curitiba.terraviva.terra_viva_app.volley.VolleyCallback;

public class CarrinhoManager {
    private Context context;
    private Activity activity;
    private ListView lv_carrinho;
    private static final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

    public CarrinhoManager(Context context,Activity activity){
        this.context =context;
        this.activity = activity;
        lv_carrinho = activity.findViewById(R.id.lv_carrinho);

        setAdapter(Session.compras);
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
        CarrinhoListCell adapter = new CarrinhoListCell(activity,nomes,valores,qtd,images);
        lv_carrinho = activity.findViewById(R.id.lv_carrinho);
        lv_carrinho.setAdapter(adapter);

        //definir tamanho listview
        ViewGroup.LayoutParams lp = lv_carrinho.getLayoutParams();
        lp.height = getItemHeightofListView(lv_carrinho,compras.size());
        lv_carrinho.setLayoutParams(lp);

        lv_carrinho.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                Intent it = new Intent(context,DetalhesActivity.class);
                Bundle data = new Bundle();
                data.putSerializable("produto",compras.get(position).getProduto());
                it.putExtras(data);
                context.startActivity(it);
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
