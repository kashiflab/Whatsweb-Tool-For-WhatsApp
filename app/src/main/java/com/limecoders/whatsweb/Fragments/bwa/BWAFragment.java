package com.limecoders.whatsweb.Fragments.bwa;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.limecoders.whatsweb.Adapters.ViewPagerWAAdapter;
import com.limecoders.whatsweb.Fragments.wa.WAVideoFragment;
import com.limecoders.whatsweb.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class BWAFragment extends Fragment {
    ViewPager viewPager;
    TabLayout tabLayout;


    public BWAFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_bwa, container, false);
        viewPager = (ViewPager) v.findViewById(R.id.viewPager_bwa);
        tabLayout = (TabLayout) v.findViewById(R.id.tab_layout_bwa);
        viewPager.setOffscreenPageLimit(2);
        ViewPagerWAAdapter adapter = new ViewPagerWAAdapter(getChildFragmentManager());
        adapter.addTabs("Images",new BWAImageFragment());
        adapter.addTabs("Videos",new WAVideoFragment());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return v;
    }

}
