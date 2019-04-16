package br.curitiba.terraviva.terra_viva_app.view;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;

import br.curitiba.terraviva.terra_viva_app.MenuActions;
import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.connexion.JsonDetailsManager;
import br.curitiba.terraviva.terra_viva_app.connexion.PostManager;
import br.curitiba.terraviva.terra_viva_app.model.Compra;
import br.curitiba.terraviva.terra_viva_app.model.Produto;
import br.curitiba.terraviva.terra_viva_app.model.Usuario;

import static br.curitiba.terraviva.terra_viva_app.Session.compras;
import static br.curitiba.terraviva.terra_viva_app.Session.produto;
import static br.curitiba.terraviva.terra_viva_app.Session.usuario;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment extends Fragment {

    private Handler handler = new Handler();
    NumberFormat formato;
    View view;
    TextView tv_nome, tv_curta, tv_longa, tv_valor, tv_qtd, tv_total;
    Button btn_cart, btn_vermais, btn_finalizar;
    ImageButton btn_up, btn_down;
    ImageView img_prod;
    ListView lv_relacionados;
    ProgressBar progressBar;
    JsonDetailsManager manager;
    PostManager postManager;
    int qtd = 1;
    float total;
    boolean comprado = false;
    boolean estaNoCarrinho = false;


    public DetailsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_details, container, false);//Inflate Layout

        if (getArguments() != null){
            produto = (Produto) getArguments().getSerializable("produto");
        }



        manager = new JsonDetailsManager(getContext(), getActivity(), view);
        manager.getRecomendados(produto);
        postManager = new PostManager(getContext(), getActivity());

        // }

        progressBar = view.findViewById(R.id.progressBarDetails);
        tv_nome = view.findViewById(R.id.tv_nome_details);
        tv_curta = view.findViewById(R.id.tv_curta_details);
        tv_longa = view.findViewById(R.id.tv_longa_details);
        tv_valor = view.findViewById(R.id.tv_valor_details);
        tv_qtd = view.findViewById(R.id.tv_qtd);
        tv_total = view.findViewById(R.id.tv_total_details);
        btn_up = view.findViewById(R.id.btn_up);
        btn_down = view.findViewById(R.id.btn_down);
        btn_cart = view.findViewById(R.id.btn_cart);
        btn_vermais = view.findViewById(R.id.btn_ver_mais);
        btn_finalizar = view.findViewById(R.id.btn_finalizar);
        img_prod = view.findViewById(R.id.img_prod_details);
        lv_relacionados = view.findViewById(R.id.lv_relacionados);

        tv_longa.setVisibility(View.GONE);
        img_prod.setVisibility(View.INVISIBLE);

        if (usuario == null) {

            btn_cart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(getContext(),LoginActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("produto",produto);
                    it.putExtras(bundle);
                    startActivity(it);
                }
            });

        } else {
            if (compras != null && compras.size() > 0) {
                for (Compra c : compras) {
                    if (c.getUsuario().equals(usuario.getEmail())) {
                        estaNoCarrinho = true;
                        break;
                    }
                }
                if (estaNoCarrinho) {
                    for (Compra c : compras)
                        if (c.getProduto().getId() == produto.getId()) {
                            btn_cart.setText("Remover do\ncarrinho");
                            btn_cart.setBackgroundColor(0xffcc0000);
                            qtd = c.getQtd();
                            comprado = true;
                            break;
                        }
                }
            }
            if (!comprado) {
                btn_cart.setText("Comprar");
                btn_cart.setBackgroundColor(0xff8b4513);
            }

            if (produto.getEstoque() > 0 || estaNoCarrinho) {
                btn_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!comprado) {
                            postManager.comprar(produto, qtd, btn_cart);
                            //apenas para garantir que o estoque nunca seja negativo
                            if (produto.getEstoque() < qtd)
                                produto.setEstoque(0);

                            comprado = true;
                        } else {
                            int i = 0;
                            for (Compra c : compras) {
                                if (c.getProduto().getId() == produto.getId()) {
                                    postManager.desfazCompra(c, btn_cart, tv_qtd);
                                    qtd = 1;
                                    compras.remove(i);
                                    comprado = false;
                                    break;
                                }
                                i++;
                            }
                        }
                    }
                });
            } else {
                btn_cart.setText("Indisponível");
                btn_cart.setBackgroundColor(0xffcc0000);
            }

        }

        tv_nome.setText(produto.getNome());
        tv_curta.setText(produto.getCurta());
        tv_longa.setText(produto.getLonga());
        tv_qtd.setText("x " + qtd);

        Locale ptBr = new Locale("pt", "BR");
        formato = NumberFormat.getCurrencyInstance(ptBr);
        String valorFormatado = formato.format(produto.getValor());
        String totalFormatado = formato.format(qtd * produto.getValor());

        tv_valor.setText(valorFormatado);
        tv_total.setText(totalFormatado);

        //aumenta quantidade e recalcula total
        btn_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!comprado) {
                    if (qtd < 15) {
                        if (produto.getEstoque() > qtd) {
                            tv_qtd.setText("x " + (++qtd));
                            total = produto.getValor() * qtd;
                            tv_total.setText(formato.format(total));
                        } else
                            Toast.makeText(getContext(), "Desculpe, dispomos apenas de " + produto.getEstoque() +
                                    " unidades em estoque no momento", Toast.LENGTH_LONG).show();
                    } else
                        Toast.makeText(getContext(), "Limitado a 15 unidades por compra", Toast.LENGTH_LONG).show();
                }
            }
        });
        //diminui quantidade e recalcula total
        btn_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!comprado) {
                    if (qtd > 1) {
                        tv_qtd.setText("x " + (--qtd));
                        total = produto.getValor() * qtd;
                        tv_total.setText(formato.format(total));
                    }
                }
            }
        });

        //mostrar descrição longa
        btn_vermais.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btn_vermais.setVisibility(view.GONE);
                tv_longa.setVisibility(view.VISIBLE);
            }
        });

        new Thread() {
            public void run() {
                Bitmap img = null;

                try {
                    URL url = new URL(produto.getImg());
                    HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
                    InputStream input = conexao.getInputStream();
                    img = BitmapFactory.decodeStream(input);
                } catch (IOException e) {
                }

                final Bitmap imgAux = img;
                handler.post(new Runnable() {
                    public void run() {

                        img_prod.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                        img_prod.setImageBitmap(imgAux);

                    }
                });
            }
        }.start();

        return view;
    }

}