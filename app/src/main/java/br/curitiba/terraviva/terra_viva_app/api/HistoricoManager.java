package br.curitiba.terraviva.terra_viva_app.api;

import android.app.Activity;
import android.content.Context;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.Util;
import br.curitiba.terraviva.terra_viva_app.adapter.HistListCell;
import br.curitiba.terraviva.terra_viva_app.model.Usuario;
import br.curitiba.terraviva.terra_viva_app.volley.Volley;
import br.curitiba.terraviva.terra_viva_app.volley.VolleyCallback;

public class HistoricoManager {
    private Activity activity;
    private Context context;
    private ListView list;

    public HistoricoManager(Context context,Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void getVendas(Usuario usuario){
        Volley volley = new Volley(context,"https://terraviva.curitiba.br/api/vendas/"+usuario.getEmail()+"/"+25);
        String[] items = {"produto","valor","qtd","datavenda"};
        volley.getRequest(items, new VolleyCallback() {

            @Override
            public void onSuccess(ArrayList<HashMap<String, String>> response) {
                if(response.size() > 0){

                    String[] produtos = new String[response.size()];
                    String[] qtds = new String[response.size()];
                    String[] valores = new String[response.size()];
                    String[] datas = new String[response.size()];

                    int i = 0;
                    for (HashMap<String,String> hash : response) {
                        if (hash != null) {
                            produtos[i] = hash.get("produto");
                            qtds[i] = hash.get("qtd");
                            valores[i] = Util.formatCurrency(Float.parseFloat(hash.get("valor")));
                            Date date = Util.strToDate(hash.get("datavenda"),"yyyy-MM-dd");
                            datas[i] = Util.dateToStr(date,"dd/MM/yyyy");
                            i++;
                        }
                    }
                    HistListCell adapter = new HistListCell(activity,produtos,valores,qtds,datas);
                    list = activity.findViewById(R.id.lv_hist);
                    list.setAdapter(adapter);
                }
            }
            @Override
            public void onError(String error) {

            }
        });
    }
}
