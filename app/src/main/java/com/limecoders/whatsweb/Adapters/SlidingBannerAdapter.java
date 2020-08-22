package com.limecoders.whatsweb.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.limecoders.whatsweb.Models.SlidingBannerModel;
import com.limecoders.whatsweb.R;

import java.util.List;

public class SlidingBannerAdapter extends RecyclerView.Adapter<SlidingBannerAdapter.ViewHolder> {

    private Context context;
    private List<SlidingBannerModel> slidingBannerModels;

    public SlidingBannerAdapter(Context context, List<SlidingBannerModel> slidingBannerModels){
        this.context = context;
        this.slidingBannerModels = slidingBannerModels;
    }


    @NonNull
    @Override
    public SlidingBannerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.slidingbanner_recyclerview,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlidingBannerAdapter.ViewHolder holder, int position) {

        final SlidingBannerModel model = slidingBannerModels.get(position);

        holder.title.setText(model.getTitle());
        Glide.with(context).load(model.getImageURl()).into(holder.imageView);

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final StorageReference photoRef = FirebaseStorage.getInstance().getReferenceFromUrl(model.getImageURl());
                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Banner").child(model.getId());
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Are you sure want to delete it?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                reference.removeValue();
                                photoRef.delete();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return slidingBannerModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView imageView;
        Button delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageBanner);
            title = itemView.findViewById(R.id.title);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
