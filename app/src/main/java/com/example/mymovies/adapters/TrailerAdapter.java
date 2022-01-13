package com.example.mymovies.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymovies.R;
import com.example.mymovies.data.Trailer;
import com.example.mymovies.databinding.TrailerItemBinding;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private List<Trailer> trailerList;
    private OnTrailerClickListener onTrailerClickListener;

    public void setTrailerList(List<Trailer> trailerList) {
        this.trailerList = trailerList;
        notifyDataSetChanged();
    }

    public void setOnTrailerClickListener(OnTrailerClickListener onTrailerClickListener) {
        this.onTrailerClickListener = onTrailerClickListener;
    }

    @NonNull
    @Override
    public TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.trailer_item, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        holder.binding.textViewNameOfVideo.setText(trailerList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return trailerList.size();
    }

    public interface OnTrailerClickListener {
        void onTrailerClick(String url);
    }

    public class TrailerViewHolder extends RecyclerView.ViewHolder {

        TrailerItemBinding binding;

        public TrailerViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = TrailerItemBinding.bind(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onTrailerClickListener != null) {
                        onTrailerClickListener.onTrailerClick(trailerList.get(getAdapterPosition()).getKey());
                    }
                }
            });
        }
    }
}
