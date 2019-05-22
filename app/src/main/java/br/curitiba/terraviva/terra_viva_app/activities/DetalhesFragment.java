package br.curitiba.terraviva.terra_viva_app.activities;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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

import br.curitiba.terraviva.terra_viva_app.api.DetalhesManager;
import br.curitiba.terraviva.terra_viva_app.api.EstoqueController;
import br.curitiba.terraviva.terra_viva_app.model.Compra;
import br.curitiba.terraviva.terra_viva_app.model.Produto;


import static br.curitiba.terraviva.terra_viva_app.Session.compras;
import static br.curitiba.terraviva.terra_viva_app.Session.produto;
import static br.curitiba.terraviva.terra_viva_app.Session.usuario;
import static br.curitiba.terraviva.terra_viva_app.Util.formatCurrency;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetalhesFragment extends Fragment {

    private Handler handler;
    private TextView tv_longa;
    private TextView tv_qtd;
    private TextView tv_total;
    private Button btn_cart;
    private Button btn_vermais;
    private ImageView img_prod;
    private ProgressBar progressBar;
    private DetalhesManager manager;
    private EstoqueController controller;
    private MenuActions action;
    private int qtd = 1;
    private float total;
    private boolean comprado = false;
    private boolean estaNoCarrinho = false;

    public DetalhesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        handler = new Handler();
        if (getArguments() != null){
            produto = (Produto) getArguments().getSerializable("produto");
        }

        action = new MenuActions(getContext(),getActivity());
        manager = new DetalhesManager(getContext(), getActivity(), view);
        manager.getRecomendados(produto);
        controller = new EstoqueController(getContext(), getActivity());

        progressBar = view.findViewById(R.id.progressBarDetails);
        TextView tv_nome = view.findViewById(R.id.tv_nome_details);
        TextView tv_curta = view.findViewById(R.id.tv_curta_details);
        tv_longa = view.findViewById(R.id.tv_longa_details);
        TextView tv_valor = view.findViewById(R.id.tv_valor_details);
        tv_qtd = view.findViewById(R.id.tv_qtd);
        tv_total = view.findViewById(R.id.tv_total_details);
        ImageButton btn_up = view.findViewById(R.id.btn_up);
        ImageButton btn_down = view.findViewById(R.id.btn_down);
        btn_cart = view.findViewById(R.id.btn_cart);
        btn_vermais = view.findViewById(R.id.btn_ver_mais);
        Button btn_finalizar = view.findViewById(R.id.btn_finalizar);
        img_prod = view.findViewById(R.id.img_prod_details);
        ListView lv_relacionados = view.findViewById(R.id.lv_relacionados);

        //inicialmente a descrição curta é invisivel
        tv_longa.setVisibility(View.GONE);
        //inicialmente o produto é invisivel, somente o progressDialog (imagem de "carregando") é visivel
        img_prod.setVisibility(View.INVISIBLE);

        //se não logado vai para a tela de login
        if (usuario == null) {
            btn_finalizar.setVisibility(View.GONE);
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
            //percorre a lista de compras salva na sessao e informa o sistema e verifica produto já está no carrinho
            if (compras != null && compras.size() > 0) {
                for (Compra c : compras) {
                    if (c.getProduto().getId() == produto.getId()){
                        estaNoCarrinho = true;
                        break;
                    }
                }
                if (estaNoCarrinho){
                    for (Compra c : compras)
                        //substitui a escrita no botão "comprar", caso positivo
                        if (c.getProduto().getId() == produto.getId()){
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

            //verifica se há estoque disponivel pra ser possivel comprar (acrescentar no carrinho)
            if (produto.getEstoque() > 0 || estaNoCarrinho) {
                btn_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!comprado) {
                            controller.comprar(produto, qtd, btn_cart);
                            //apenas para garantir que o estoque nunca seja negativo
                            if (produto.getEstoque() < qtd)
                                produto.setEstoque(0);

                            comprado = true;
                        } else {
                            int i = 0;
                            for (Compra c : compras) {
                                if (c.getProduto().getId() == produto.getId()) {
                                    controller.desfazCompra(c, btn_cart, tv_qtd);
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

        String valorFormatado = formatCurrency(produto.getValor());
        String totalFormatado = formatCurrency(qtd * produto.getValor());

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
                            tv_total.setText(formatCurrency(total));
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
                        tv_total.setText(formatCurrency(total));
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

        btn_finalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                action.goCarrinho();
            }
        });

        //baixa a imagem do endereço retornado de produto.getImg(), ecsconde o progressDialog e a apresenta no ImageView
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