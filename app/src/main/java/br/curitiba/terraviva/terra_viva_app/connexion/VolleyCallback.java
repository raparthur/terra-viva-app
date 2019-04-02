package br.curitiba.terraviva.terra_viva_app.connexion;

import java.util.ArrayList;
import java.util.HashMap;

public interface VolleyCallback {
    void onSuccess(ArrayList<HashMap<String, String>> response);
}
