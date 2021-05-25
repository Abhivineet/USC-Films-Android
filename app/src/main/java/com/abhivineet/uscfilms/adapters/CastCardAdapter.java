package com.abhivineet.uscfilms.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abhivineet.uscfilms.models.CastCard;
import com.abhivineet.uscfilms.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class CastCardAdapter extends RecyclerView.Adapter {
    private final ArrayList<CastCard> cardList;

    public CastCardAdapter(ArrayList<CastCard> cardList) {
        super();
        this.cardList = cardList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_cast_card_item, parent, false);
        return new CastHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final CastCard castCardItem = this.cardList.get(position);
        if (castCardItem.getProfilePath().equals("")){
            ((CastHolder) holder).getCastProfileView().setImageResource(R.drawable.profile_path_placeholder);
        }
        else {
            Glide.with(holder.itemView)
                    .load(castCardItem.getProfilePath())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            ((CastHolder) holder).getCastProfileView().setImageResource(R.drawable.profile_path_placeholder);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .fitCenter()
                    .into(((CastCardAdapter.CastHolder) holder).getCastProfileView());
        }

        ((CastHolder) holder).getCastNameView().setText(castCardItem.getName());

    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }


    static class CastHolder extends RecyclerView.ViewHolder{
        private final CircleImageView castProfileView;

        public CircleImageView getCastProfileView() {
            return castProfileView;
        }

        public TextView getCastNameView() {
            return castNameView;
        }

        private final TextView castNameView;

        public CastHolder(@NonNull View itemView) {
            super(itemView);
            this.castProfileView = itemView.findViewById(R.id.cast_image_view);
            this.castNameView = itemView.findViewById(R.id.cast_name_view);
        }
    }
}

//
//    ImageView castImage = (ImageView) view.findViewById(R.id.cast_image_view);
//    TextView castName = (TextView) view.findViewById(R.id.cast_name_view);