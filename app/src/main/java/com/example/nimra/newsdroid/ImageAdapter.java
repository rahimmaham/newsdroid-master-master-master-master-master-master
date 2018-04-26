package com.example.nimra.newsdroid;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private Context mContext;
    private List<newsUpload> mUploads;

    public ImageAdapter(Context context, List<newsUpload> uploads){
        mContext = context;
        mUploads = uploads;
    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        newsUpload uploadCurrent = mUploads.get(position);
        holder.ntitle.setText(uploadCurrent.getNewstitle());
        holder.ndescription.setText(uploadCurrent.getNewsdescription());
        holder.ntime.setText(uploadCurrent.getNewstime());
        Picasso.with(mContext).load(uploadCurrent.getNewsimage()).placeholder(R.mipmap.ic_launcher).into(holder.nimage);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        public TextView ntitle;
        public TextView ndescription;
        public TextView ntime;
        public ImageView nimage;

        public ImageViewHolder(View itemView) {
            super(itemView);

            ntitle = itemView.findViewById(R.id.newstitle);
            ndescription = itemView.findViewById(R.id.newsdescription);
            ntime = itemView.findViewById(R.id.newstime);
            nimage = itemView.findViewById(R.id.newsimage);



        }
    }
}
