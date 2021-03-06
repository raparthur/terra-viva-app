package br.curitiba.terraviva.terra_viva_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import br.curitiba.terraviva.terra_viva_app.activities.CarrinhoActivity;
import br.curitiba.terraviva.terra_viva_app.activities.HomeActivity;
import br.curitiba.terraviva.terra_viva_app.activities.PessoaisActivity;

public class MenuActions {
    private Context context;
    private Activity activity;

    public MenuActions(Context context,Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void goHome(){
        ProgressDialog pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Aguarde...");
        pDialog.setCancelable(false);
        pDialog.show();
        Intent it = new Intent(context,HomeActivity.class);
        context.startActivity(it);
        activity.finish();
    }

    public void goCarrinho(){
        if(Session.compras != null && Session.compras.size() > 0) {
            Intent it = new Intent(context, CarrinhoActivity.class);
            context.startActivity(it);
        }else
            Toast.makeText(context,"Seu carrinho está vazio!",Toast.LENGTH_LONG).show();
    }

    public void partiuCadastro(){
        Intent it = new Intent(context,PessoaisActivity.class);
        activity.startActivity(it);
    }

    public void logout(){
        new android.support.v7.app.AlertDialog.Builder(activity)
                .setTitle("Sair da sua conta?")
                .setMessage("Se houver compras em aberto, seu carrinho será salvo")
                .setPositiveButton("SIM", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Session.usuario = null;
                        Session.compras = null;
                        Session.produto = null;
                        goHome();
                    }
                })
                .setNegativeButton("NÃO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(R.drawable.ic_dialog_alert)
                .show();

    }

}
