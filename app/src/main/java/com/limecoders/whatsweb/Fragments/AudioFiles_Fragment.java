package com.limecoders.whatsweb.Fragments;


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

import com.limecoders.whatsweb.Activities.Navigation_Activity;
import com.limecoders.whatsweb.Adapters.Audio.audioExpandableRecyclerViewAdapter;
import com.limecoders.whatsweb.Models.StorageSize;
import com.limecoders.whatsweb.R;
import com.limecoders.whatsweb.Utils.StorageUtil;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 */
public class AudioFiles_Fragment extends Fragment {

    public static audioExpandableRecyclerViewAdapter adapter;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    FloatingActionButton mFloatingActionButton;
    private TextView no_audio_text;

    public AudioFiles_Fragment() {
        // Required empty public constructor
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_audio_files, container, false);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Audio Messages");
        } catch (Exception e) {
            e.printStackTrace();
        }
        no_audio_text = (TextView) view.findViewById(R.id.no_audio_text);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.audioRecyclerView);
        mFloatingActionButton = (FloatingActionButton) view.findViewById(R.id.audioDeleteButton);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        adapter = new audioExpandableRecyclerViewAdapter(getActivity(), Navigation_Activity.sortedAudioMediaFiles);
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
    private void checkEmptyList(){
        if (Navigation_Activity.sortedImageMediaFiles.isEmpty()){
            mFloatingActionButton.setVisibility(View.GONE);
            no_audio_text.setVisibility(View.VISIBLE);
        }
    }

    private boolean checkForFileToDelete() {
        for (int i = 0; i < Navigation_Activity.sortedAudioMediaFiles.size(); i++) {
            for (int j = 0; j < Navigation_Activity.sortedAudioMediaFiles.get(i).media_files.size(); j++) {
                if (Navigation_Activity.sortedAudioMediaFiles.get(i).media_files.get(j).checked)
                    return true;
            }
        }
        return false;
    }

    private void deleteVideoFiles() {
        long totalFileSize = 0;
        int count = 0;
        for (int i = 0; i < Navigation_Activity.sortedAudioMediaFiles.size(); i++) {
            for (int j = 0; j < Navigation_Activity.sortedAudioMediaFiles.get(i).media_files.size(); j++) {
                if (Navigation_Activity.sortedAudioMediaFiles.get(i).media_files.get(j).checked) {
                    File file = Navigation_Activity.sortedAudioMediaFiles.get(i).media_files.get(j).file;
                    long size = file.length();
                    if (file.delete()) {
                        Navigation_Activity.sortedAudioMediaFiles.get(i).size = Navigation_Activity.sortedAudioMediaFiles.get(i).size - size;
                        totalFileSize = totalFileSize + size;
                        count++;
                        Navigation_Activity.sortedAudioMediaFiles.get(i).media_files.remove(j);
                        adapter.notifyChildItemRemoved(i, j);
                    }
                    j--;
                }
            }
        }

        for (int i = 0; i < Navigation_Activity.sortedAudioMediaFiles.size(); i++) {
            if (Navigation_Activity.sortedAudioMediaFiles.get(i).media_files.isEmpty()) {
                Navigation_Activity.sortedAudioMediaFiles.remove(i);
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
        for (int j = 0; j < Navigation_Activity.sortedAudioMediaFiles.size(); j++) {
            if (Navigation_Activity.sortedAudioMediaFiles.get(j).isChecked)
                Navigation_Activity.sortedAudioMediaFiles.get(j).isChecked = false;
            for (int k = 0; k < Navigation_Activity.sortedAudioMediaFiles.get(j).media_files.size(); k++) {
                if (Navigation_Activity.sortedAudioMediaFiles.get(j).media_files.get(k).checked)
                    Navigation_Activity.sortedAudioMediaFiles.get(j).media_files.get(k).checked = false;
            }
        }
    }

}
