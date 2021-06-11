package com.example.doanmp3.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmp3.Fragment.UserFragment.Added_AddFragment;
import com.example.doanmp3.Model.BaiHat;
import com.example.doanmp3.R;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AddBaiHatAdapter extends RecyclerView.Adapter<AddBaiHatAdapter.ViewHolder> implements Filterable {
    Context context;
    ArrayList<BaiHat> arrayList;
    ArrayList<BaiHat> mArrayList;
    boolean isAddedFragment;

    public AddBaiHatAdapter(Context context, ArrayList<BaiHat> arrayList, boolean isAddedFragment) {
        this.context = context;
        this.arrayList = arrayList;
        this.isAddedFragment = isAddedFragment;
    }

    public AddBaiHatAdapter(Context context, ArrayList<BaiHat> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        mArrayList = arrayList;
        isAddedFragment = false;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dong_add_baihat, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        Picasso.with(context).load(arrayList.get(position).getHinhBaiHat()).into(holder.Avatar);
        holder.txtBaiHat.setText(arrayList.get(position).getTenBaiHat());
        holder.txtCaSi.setText(arrayList.get(position).getTenAllCaSi());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Added_AddFragment.arrayList.remove(Added_AddFragment.position);
                Added_AddFragment.adapter.notifyItemRemoved(position);
                holder.Status.setImageResource(R.drawable.icon_add);
                notifyItemChanged(position);
            }
        });
        if (isAddedFragment) {
            holder.Status.setImageResource(R.drawable.ic_delete);
            holder.Status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Added_AddFragment.arrayList.remove(position);
                    notifyItemRemoved(position);
                }
            });

        } else {
            if (Added_AddFragment.chechAddedBefore(arrayList.get(position).getIdBaiHat()))
                holder.Status.setImageResource(R.drawable.ic_check);
            else
                holder.Status.setImageResource(R.drawable.icon_add);

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Added_AddFragment.chechAddedBefore(arrayList.get(position).getIdBaiHat())) {
                        Added_AddFragment.arrayList.remove(Added_AddFragment.position);
                        Added_AddFragment.adapter.notifyItemRemoved(Added_AddFragment.position);
                        holder.Status.setImageResource(R.drawable.icon_add);
                    }else{
                        Added_AddFragment.arrayList.add(arrayList.get(position));
                        Added_AddFragment.adapter.notifyDataSetChanged();
                        holder.Status.setImageResource(R.drawable.ic_check);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (arrayList != null)
            return arrayList.size();
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        RoundedImageView Avatar;
        ImageView Status;
        TextView txtCaSi, txtBaiHat;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            relativeLayout = itemView.findViewById(R.id.dong_add_baihat);
            Avatar = itemView.findViewById(R.id.img_add_baihat);
            Status = itemView.findViewById(R.id.img_add_or_cancel_add_baihat);
            txtCaSi = itemView.findViewById(R.id.txt_tencasi_add_baihat);
            txtBaiHat = itemView.findViewById(R.id.txt_tenbaihat_add_baihat);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String query = (String) constraint;
                if (query.equals("")) {
                    arrayList = mArrayList;
                } else {
                    List<BaiHat> baiHats = new ArrayList<>();
                    for (BaiHat baiHat : mArrayList) {
                        if (baiHat.getTenBaiHat().toString().toLowerCase().contains(query.toLowerCase())) {
                            baiHats.add(baiHat);
                        }
                    }
                    arrayList = (ArrayList<BaiHat>) baiHats;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = arrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrayList = (ArrayList<BaiHat>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
