package com.example.cineroom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.PostHolder> {


    private ArrayList<String> filmeImageList;

    public RecyclerViewAdapter(ArrayList<String> filmeImageList) {
        this.filmeImageList = filmeImageList;
    }

    @NonNull
    @Override
    public PostHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.layout_listitem,parent,false);

        return new PostHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostHolder holder, int position) {


        Picasso.get().load(filmeImageList.get(position)).into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return filmeImageList.size();
    }

    class PostHolder extends RecyclerView.ViewHolder{

        ImageView imageView;

        public PostHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.image);

        }
    }
}
