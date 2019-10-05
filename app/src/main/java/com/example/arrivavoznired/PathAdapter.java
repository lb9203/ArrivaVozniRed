package com.example.arrivavoznired;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class PathAdapter extends RecyclerView.Adapter<PathAdapter.PathViewHolder> {

    private List<String> pathList;

    PathAdapter(List<String> pathList){
        this.pathList = pathList;
    }

    @NonNull
    @Override
    public PathViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_path_item,parent,false);
        return new PathViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PathViewHolder holder, int position) {
        holder.pathTextView.setText(pathList.get(position));
    }

    @Override
    public int getItemCount() {
        return pathList.size();
    }


    class PathViewHolder extends RecyclerView.ViewHolder{

        MaterialTextView pathTextView;

        PathViewHolder(@NonNull View itemView) {
            super(itemView);
            pathTextView = itemView.findViewById(R.id.path_text_view);
        }
    }
}
