package br.curitiba.terraviva.terra_viva_app.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class HistListCell extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] nome;
    private final String[] valor;
    private final String[] qtd;
    private final String[] data;

    public HistListCell(Activity context, String[] nome, String[] valor, String[] qtd, String[] data){
        super(context,R.layout.activity_hist_list_cell,nome);
        this.context = context;
        this.nome = nome;
        this.valor = valor;
        this.qtd = qtd;
        this.data = data;
    }

    @NonNull
    @Override
    public View getView(final int position, View view, ViewGroup parent){
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.activity_hist_list_cell,null,true);

        TextView tv_produto = rowView.findViewById(R.id.hist_prod);
        TextView tv_qtd = rowView.findViewById(R.id.hist_qtd);
        TextView tv_valor = rowView.findViewById(R.id.hist_valor);
        TextView tv_data = rowView.findViewById(R.id.hist_data);

        tv_produto.setText(nome[position]);
        tv_qtd.setText(qtd[position]);
        tv_valor.setText(valor[position]);
        tv_data.setText(data[position]);

        return rowView;
    }
}
