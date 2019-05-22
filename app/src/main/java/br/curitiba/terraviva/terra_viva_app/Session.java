package br.curitiba.terraviva.terra_viva_app;

import java.util.List;

import br.curitiba.terraviva.terra_viva_app.model.Compra;
import br.curitiba.terraviva.terra_viva_app.model.Produto;
import br.curitiba.terraviva.terra_viva_app.model.Usuario;

public final class Session {
    public static Usuario usuario;
    public static List<Compra> compras;
    public static Produto produto;
    public static float frete;
    public static String prazo;
}
