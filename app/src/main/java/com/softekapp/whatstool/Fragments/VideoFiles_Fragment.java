package com.softekapp.whatstool.Fragments;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.softekapp.whatstool.Activities.Navigation_Activity;
import com.softekapp.whatstool.Adapters.Video.VideoExpandableRecyclerViewAdapter;
import com.softekapp.whatstool.Models.StorageSize;
import com.softekapp.whatstool.R;
import com.softekapp.whatstool.Utils.StorageUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class VideoFiles_Fragment extends Fragment {

    public static VideoExpandableRecyclerViewAdapter adapter;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    FloatingActionButton mFloatingActionButton;
    private TextView no_video_text;

    public VideoFiles_Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_files, container, false);

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Video Messages");
        } catch (Exception e) {
            e.printStackTrace();
        }
        no_video_text = (TextView) view.findViewById(R.id.no_video_text);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.VideoRecyclerView);
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.videoDeleteButton);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new VideoExpandableRecyclerViewAdapter(getActivity(), Navigation_Activity.sortedVideoMediaFiles);
        mRecyclerView.setAdapter(adapter);
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkForFileToDelete()) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_layout, null);
                    mBuilder.setView(dialogView);
                    Button cancel_button = (Button) dialogView.findViewById(R.id.cancel_button);
                    Button continue_button = (Button) dialogView.findViewById(R.id.continue_button);
                    final AlertDialog mAlertDialog = mBuilder.create();
                    mAlertDialog.show();
                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mAlertDialog.dismiss();
                        }
                    });
                    continue_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteVideoFiles();
                            mAlertDialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "Select a file to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
        checkEmptyList();
        return view;
    }

    @SuppressLint("RestrictedApi")
    private void checkEmptyList() {
        if (Navigation_Activity.sortedImageMediaFiles.isEmpty()) {
            mFloatingActionButton.setVisibility(View.GONE);
            no_video_text.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        //  eraseChecks();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private boolean checkForFileToDelete() {
        for (int i = 0; i < Navigation_Activity.sortedVideoMediaFiles.size(); i++) {
            for (int j = 0; j < Navigation_Activity.sortedVideoMediaFiles.get(i).media_files.size(); j++) {
                if (Navigation_Activity.sortedVideoMediaFiles.get(i).media_files.get(j).checked)
                    return true;
            }
        }
        return false;
    }

    private void deleteVideoFiles() {
        long totalFileSize = 0;
        int count = 0;
        for (int i = 0; i < Navigation_Activity.sortedVideoMediaFiles.size(); i++) {
            for (int j = 0; j < Navigation_Activity.sortedVideoMediaFiles.get(i).media_files.size(); j++) {
                if (Navigation_Activity.sortedVideoMediaFiles.get(i).media_files.get(j).checked) {
                    File file = Navigation_Activity.sortedVideoMediaFiles.get(i).media_files.get(j).file;
                    long size = file.length();
                    if (file.delete()) {
                        Navigation_Activity.sortedVideoMediaFiles.get(i).size = Navigation_Activity.sortedVideoMediaFiles.get(i).size - size;
                        totalFileSize = totalFileSize + size;
                        count++;
                        Navigation_Activity.sortedVideoMediaFiles.get(i).media_files.remove(j);
                        adapter.notifyChildItemRemoved(i, j);
                    }
                    j--;
                }
            }
        }

        for (int i = 0; i < Navigation_Activity.sortedVideoMediaFiles.size(); i++) {
            if (Navigation_Activity.sortedVideoMediaFiles.get(i).media_files.isEmpty()) {
                Navigation_Activity.sortedVideoMediaFiles.remove(i);
                adapter.notifyParentItemRemoved(i);
            } else {
                adapter.notifyParentItemChanged(i);
            }
        }
        showCustomToast(totalFileSize, count);
        checkEmptyList();
    }

    private void showCustomToast(long totalSize, int count) {
        View toastView = LayoutInflater.from(getActivity()).inflate(R.layout.custom_toast_layout, null);
        Toast customToast = new Toast(getActivity());
        customToast.setView(toastView);
        TextView deletedSize = (TextView) toastView.findViewById(R.id.tv_deleted_size);
        TextView deleteCount = (TextView) toastView.findViewById(R.id.tv_delete_count);
        StorageSize mStorageSize = StorageUtil.convertStorageSize(totalSize);
        deleteCount.setText(count + " " + "files cleaned");
        deletedSize.setText("" + String.format("%.2f", mStorageSize.value) + mStorageSize.suffix);
        customToast.setDuration(Toast.LENGTH_LONG);
        customToast.show();
    }

    private void eraseChecks() {
        for (int j = 0; j < Navigation_Activity.sortedVideoMediaFiles.size(); j++) {
            if (Navigation_Activity.sortedVideoMediaFiles.get(j).isChecked)
                Navigation_Activity.sortedVideoMediaFiles.get(j).isChecked = false;
            for (int k = 0; k < Navigation_Activity.sortedVideoMediaFiles.get(j).media_files.size(); k++) {
                if (Navigation_Activity.sortedVideoMediaFiles.get(j).media_files.get(k).checked)
                    Navigation_Activity.sortedVideoMediaFiles.get(j).media_files.get(k).checked = false;
            }
        }
    }

}
