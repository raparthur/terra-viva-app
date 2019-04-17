package br.curitiba.terraviva.terra_viva_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import br.curitiba.terraviva.terra_viva_app.view.HomeActivity;

public class MenuActions {
    private Context context;
    private Activity activity;

    public MenuActions(Context context,Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void goHome(){
        Intent it = new Intent(context,HomeActivity.class);
        context.startActivity(it);
        activity.finish();
    }

}
