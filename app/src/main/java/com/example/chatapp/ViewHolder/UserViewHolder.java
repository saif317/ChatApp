package com.example.chatapp.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class UserViewHolder extends RecyclerView.ViewHolder {
    @BindView(R.id.avatar_image)
    public ImageView avatar_image;
    @BindView(R.id.name_text)
    public TextView name_text;
    @BindView(R.id.bio_text)
    public TextView bio_text;

    private Unbinder unbinder;

    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        unbinder= ButterKnife.bind(this,itemView);
    }
}
