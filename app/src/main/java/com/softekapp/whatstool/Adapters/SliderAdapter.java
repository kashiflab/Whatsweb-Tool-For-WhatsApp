package com.softekapp.whatstool.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.softekapp.whatstool.Activities.FirstActivity;
import com.softekapp.whatstool.Activities.WebViewActivity;
import com.softekapp.whatstool.Models.SlidingBannerModel;
import com.softekapp.whatstool.R;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapter extends
        SliderViewAdapter<SliderAdapter.SliderAdapterVH> {

    public String getURLID(){
        return URLID;
    }

    public static String URLID;
    private Context context;
    private List<SlidingBannerModel> mSliderItems;

    public SliderAdapter(Context context, List<SlidingBannerModel> mSliderItems) {
        this.context = context;
        this.mSliderItems = mSliderItems;
    }

    public void renewItems(List<SlidingBannerModel> sliderItems) {
        this.mSliderItems = sliderItems;
        notifyDataSetChanged();
    }

    public void deleteItem(int position) {
        this.mSliderItems.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(SlidingBannerModel sliderItem) {
        this.mSliderItems.add(sliderItem);
        notifyDataSetChanged();
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {

        final SlidingBannerModel sliderItem = mSliderItems.get(position);

        viewHolder.textViewDescription.setText(sliderItem.getTitle());
        viewHolder.textViewDescription.setTextSize(16);
        viewHolder.textViewDescription.setTextColor(Color.WHITE);
        Glide.with(viewHolder.itemView)
                .load(sliderItem.getImageURl())
                .fitCenter()
                .into(viewHolder.imageViewBackground);

//        viewHolder.imageViewBackground.setImageURI(Uri.parse(sliderItem.getImageURl()));

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<mSliderItems.size();i++){
                    if(i==position){
//                        Intent intent = new Intent();
//                        intent.setAction(Intent.ACTION_VIEW);
//                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
//                        intent.setData(Uri.parse(sliderItem.getUrl()));
//                        context.startActivity(intent);

                        URLID = sliderItem.getUrl();

                            FirstActivity.isSlideAdShown = false;
                            context.startActivity(new Intent(context, WebViewActivity.class).putExtra("url",sliderItem.getUrl()));

                    }
                }
//                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }

}