package br.curitiba.terraviva.terra_viva_app.adapter;

import android.app.Activity;
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
import java.text.NumberFormat;
import java.util.Locale;

import br.curitiba.terraviva.terra_viva_app.R;

public class CarrinhoListCell extends ArrayAdapter<String> {
    private Handler handler = new Handler();
    private final Activity context;
    private final String[] nomeProd;
    private final String[] qtdProd;
    private final String[] imgProd;
    private final float[] valor;

    public CarrinhoListCell(Activity context, String[] nomeProd, float[] valorProd, String[] qtdProd, String[] imgProd){
        super(context,R.layout.list_cell_prod,nomeProd);
        this.context = context;
        this.nomeProd = nomeProd;
        this.qtdProd = qtdProd;
        this.imgProd = imgProd;
        this.valor = valorProd;
    }

    @NonNull
    @Override
    public View getView(final int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.list_cell_carrinho,null,true);

        TextView tv_nome = rowView.findViewById(R.id.tv_adapter_nome);
        TextView tv_total = rowView.findViewById(R.id.tv_adapter_total);

        final ProgressBar progressBar = rowView.findViewById(R.id.adapter_progressBar);
        final ImageView imageView = rowView.findViewById(R.id.adapter_img);
        imageView.setVisibility(View.INVISIBLE);

        TextView tv_qtd = rowView.findViewById(R.id.tv_adapter_qtd);
        TextView tv_valor = rowView.findViewById(R.id.tv_adapter_valor);
        tv_nome.setText(nomeProd[position]);
        tv_qtd.setText(qtdProd[position]);
        Locale ptBr = new Locale("pt", "BR");
        NumberFormat formato = NumberFormat.getCurrencyInstance(ptBr);
        String valor = formato.format(this.valor[position]);
        tv_valor.setText(valor);

        String qtd = tv_qtd.getText().toString();

        qtd = qtd.replace(",",".");

        float total = (float)Integer.parseInt(qtd)*this.valor[position];
        valor = formato.format(total);
        tv_total.setText(valor);

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

                    }
                });
            }
        }.start();

        return rowView;
    }
}
