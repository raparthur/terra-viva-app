package br.curitiba.terraviva.terra_viva_app.api;

import android.app.Activity;
import android.content.Context;

import br.curitiba.terraviva.terra_viva_app.model.Usuario;

public class CadastroManager {
    private Context context;
    private Activity activity;
    private Usuario usuario;
    private boolean existsUsuario;

    public CadastroManager(Context context,Activity activity){
        this.context = context;
        this.activity = activity;
    }

    public void setUsuario(Usuario usuario){
        this.usuario = usuario;
    }

    public boolean existsUsuario(Usuario usuario){
        return existsUsuario;
    }
}
