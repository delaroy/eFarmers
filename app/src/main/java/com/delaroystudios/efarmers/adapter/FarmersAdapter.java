package com.delaroystudios.efarmers.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.delaroystudios.efarmers.R;
import com.delaroystudios.efarmers.database.FarmersEntity;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class FarmersAdapter extends RecyclerView.Adapter<FarmersAdapter.FarmersViewHolder> {

    // Member variable to handle item clicks
    final private ItemClickListener mItemClickListener;
    private List<FarmersEntity> farmers;
    private Context mContext;

    public FarmersAdapter(Context context, ItemClickListener listener) {
        mContext = context;
        mItemClickListener = listener;
    }

    @NonNull
    @Override
    public FarmersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext)
                .inflate(R.layout.farmers_item, parent, false);

        return new FarmersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FarmersViewHolder holder, int position) {
        // Determine the values of the wanted data
        FarmersEntity farmersEntity = farmers.get(position);
        String name = farmersEntity.getName();
        String m_age = farmersEntity.getAge();
        String type_farming = farmersEntity.getType_farming();
        String image_url = farmersEntity.getProfile_img();

        //Set values
        holder.name.setText(name);
        holder.age.setText(m_age + "yrs");
        holder.type_farming.setText(type_farming);

        Glide.with(mContext)
                .load(image_url)
                .into(holder.imageView);
    }

    /**
     * Returns the number of items to display.
     */
    @Override
    public int getItemCount() {
        if (farmers == null) {
            return 0;
        }
        return farmers.size();
    }

    public List<FarmersEntity> getFarmers() {
        return farmers;
    }

    public void setFarmer(List<FarmersEntity> farmersEntities) {
        farmers = farmersEntities;
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        farmers.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(FarmersEntity item, int position) {
        farmers.add(position, item);
        // notify item added by position
        notifyItemInserted(position);
    }

    public interface ItemClickListener {
        void onItemClickListener(int itemId);
    }

    // Inner class for creating ViewHolders
    public class FarmersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView type_farming;
        TextView age;
        CircleImageView imageView;

        FarmersViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            type_farming = itemView.findViewById(R.id.type_farming);
            age = itemView.findViewById(R.id.age);
            imageView = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int elementId = farmers.get(getAdapterPosition()).getId();
            mItemClickListener.onItemClickListener(elementId);
        }
    }
}