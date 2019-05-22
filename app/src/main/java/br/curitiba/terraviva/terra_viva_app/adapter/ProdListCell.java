package br.curitiba.terraviva.terra_viva_app.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.curitiba.terraviva.terra_viva_app.R;

public class ProdListCell extends ArrayAdapter<String> {
    private Handler handler = new Handler();
    private final Activity context;
    private final String[] nomeProd;
    private final String[] curtaProd;
    private final String[] valorProd;
    private final String[] imgProd;
    private ProgressDialog pDialog;

    public ProdListCell(Activity context, String[] nomeProd, String[] curtaProd, String[] valorProd, String[] imgProd){
        super(context,R.layout.list_cell_prod,nomeProd);
        this.context = context;
        this.nomeProd = nomeProd;
        this.curtaProd = curtaProd;
        this.valorProd = valorProd;
        this.imgProd = imgProd;
    }

    @NonNull
    @Override
    public View getView(final int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_cell_prod,null,true);
        TextView tv_nome = rowView.findViewById(R.id.tv_nome_prod);

        final ProgressBar progressBar = rowView.findViewById(R.id.progressBar);
        final ImageView imageView = rowView.findViewById(R.id.img_prod);
        imageView.setVisibility(View.INVISIBLE);

        TextView tv_curta = rowView.findViewById(R.id.tv_curta_prod);
        TextView tv_valor = rowView.findViewById(R.id.tv_valor_prod);
        tv_nome.setText(nomeProd[position]);
        tv_curta.setText(curtaProd[position]);
        tv_valor.setText(valorProd[position]);

        new Thread(){
            public void run(){
                Bitmap img = null;

                try{
                    URL url = new URL(imgProd[position]);
                    HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                    InputStream input = conexao.getInputStream();
                    img = BitmapFactory.decodeStream(input);
                }
                catch(IOException e){}

                final Bitmap imgAux = img;
                handler.post(new Runnable(){
                    public void run(){

                        progressBar.setVisibility(View.INVISIBLE);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(imgAux);
                        if (pDialog != null && pDialog.isShowing())
                            pDialog.dismiss();

                    }
                });
            }
        }.start();

        return rowView;
    }
}
