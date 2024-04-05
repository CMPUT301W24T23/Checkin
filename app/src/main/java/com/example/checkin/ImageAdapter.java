//package com.example.checkin;
//
//import android.content.Context;
//import android.media.Image;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.List;
//

// ImageAdapter to embed the images into a list and this adapter will be used in AdministratorProfileList.java.

//public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
//
//
//    private List<Image> images;
//    private Context context;
//
//    public ImageAdapter(Context context, List<Image> images) {
//        this.context = context;
//        this.images = images;
//    }
//
//    @NonNull
//    @Override
//    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admin_profileImglist, parent, false);
//        return new ViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        Image image = images.get(position);
//        holder.imageView.setImageResource(image.getResourceId()); // Assuming you have a method to get the resource ID of each image
//    }
//
//    @Override
//    public int getItemCount() {
//        return images.size();
//    }
//
//    public class ViewHolder extends RecyclerView.ViewHolder {
//        ImageView imageView;
//
//        public ViewHolder(@NonNull View itemView) {
//            super(itemView);
//            imageView = itemView.findViewById(R.id.imageView);
//        }
//    }
//}
