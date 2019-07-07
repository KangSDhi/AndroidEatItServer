package com.example.androideatitserver.ViewHolder;

import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.androideatitserver.Common.Common;
import com.example.androideatitserver.Interface.ItemClickListener;
import com.example.androideatitserver.R;

public class DasborViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {

    public ImageView ImageKategori;
    public TextView NameKategori;

    private ItemClickListener itemClickListener;

    public DasborViewHolder(@NonNull View itemView) {
        super(itemView);
        ImageKategori = itemView.findViewById(R.id.imgKategori);
        NameKategori = itemView.findViewById(R.id.nameKategori);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition());
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Pilih");
        menu.add(0,0, getAdapterPosition(), Common.Update);
        menu.add(0,1, getAdapterPosition(), Common.Delete);
    }
}
