package com.limecoders.whatsweb.Activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.limecoders.whatsweb.Adapters.SlidingBannerAdapter;
import com.limecoders.whatsweb.Models.SlidingBannerModel;
import com.limecoders.whatsweb.R;

import java.util.ArrayList;
import java.util.List;

public class SlidingBannerActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private RecyclerView recyclerView;
    private SlidingBannerAdapter adapter;
    private List<SlidingBannerModel> slidingBannerModels = new ArrayList<>();

    private ProgressDialog pDialog;
    private TextView noData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_banner);

        initpDialog();

        noData = findViewById(R.id.no_data);
        recyclerView = findViewById(R.id.recycler_view);
        reference = FirebaseDatabase.getInstance().getReference().child("Banner");

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayout.VERTICAL));

        getBannersData();
    }

    private void getBannersData() {
        showpDialog();

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(slidingBannerModels.size()>0){
                    slidingBannerModels.clear();
                }
                for(DataSnapshot ds: snapshot.getChildren()){
                    slidingBannerModels.add(new SlidingBannerModel(ds.getKey().toString(),ds.child("url").getValue().toString(),
                            ds.child("title").getValue().toString(),ds.child("imageURL").getValue().toString()));
                }
                adapter = new SlidingBannerAdapter(SlidingBannerActivity.this,slidingBannerModels);
                recyclerView.setAdapter(adapter);

                hidepDialog();
                if(slidingBannerModels.size()==0){
                    noData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(SlidingBannerActivity.this, "Error"+ error.getMessage(), Toast.LENGTH_SHORT).show();
                hidepDialog();
            }
        });
    }

    protected void initpDialog() {
        pDialog = new ProgressDialog(SlidingBannerActivity.this);
        pDialog.setMessage(getString(R.string.loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing()) pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing()) pDialog.dismiss();
    }
}