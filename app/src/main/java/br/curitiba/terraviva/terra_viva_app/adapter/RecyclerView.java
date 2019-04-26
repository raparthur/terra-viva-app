package br.curitiba.terraviva.terra_viva_app.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import br.curitiba.terraviva.terra_viva_app.R;
import br.curitiba.terraviva.terra_viva_app.api.HomeManager;
import de.hdodenhof.circleimageview.CircleImageView;

public class RecyclerView extends android.support.v7.widget.RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private HomeManager manager;
    private TextView tv_titulo;

    private static final String TAG = "RecyclerView";

    //vars
    private ArrayList<String> mNames;
    private ArrayList<String> mImageUrls;
    private ArrayList<Integer> mIds;
    private Activity activity;
    private Context mContext;

    public RecyclerView(Context context, ArrayList<String> names, ArrayList<String> imageUrls, ArrayList<Integer> ids, Activity activity) {
        mNames = names;
        mImageUrls = imageUrls;
        mContext = context;
        this.activity = activity;
        mIds = ids;
        manager = new HomeManager(context,activity);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_listitem, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Log.d(TAG, "onBindViewHolder: called.");

        Glide.with(mContext)
                .asBitmap()
                .load(mImageUrls.get(position))
                .into(holder.image);

        holder.name.setText(mNames.get(position));

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked on a category: " + mNames.get(position));
                tv_titulo = activity.findViewById(R.id.tv_titulo);
                tv_titulo.setText(mNames.get(position));
                manager.getProdutosCateg(mIds.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return mImageUrls.size();
    }

    public class ViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder{

        CircleImageView image;
        TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image_view);
            name = itemView.findViewById(R.id.name);
        }
    }


}
