package br.com.devzone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import br.com.devzone.R;
import br.com.devzone.classes.Category;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<Category> mData;
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context mContext;

    public RecyclerViewAdapter(Context context, List<Category> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_layout_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Category category = mData.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * Método para configurar o clique nos itens do RecyclerView
     */
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // ViewHolder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView mTextView;
        private ImageView category_image;

        public ViewHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.text_view);
            category_image = itemView.findViewById(R.id.category_image);
            itemView.setOnClickListener(this);
        }

        public void bind(Category category) {
            mTextView.setText(category.getNome());
            Glide.with(mContext)
                    .load(category.getUrlImagem())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(category_image);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) {
                mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    /**
     * Interface para lidar com o clique nos itens do RecyclerView
     */
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
}
