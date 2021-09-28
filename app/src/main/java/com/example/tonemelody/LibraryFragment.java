package com.example.tonemelody;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class LibraryFragment extends Fragment{
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private LibraryFragment() {
        // Required empty public constructor
    }
    public static LibraryFragment newInstance() {
       return new LibraryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main_track, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = Objects.requireNonNull(getView()).findViewById(R.id.topTabLayout);
        viewPager = getView().findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager);

        FragmentAdapter adapter = new FragmentAdapter(Objects.requireNonNull(getFragmentManager()), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        adapter.addFragment(FavouriteFragment.newInstance(),"Favourites");
        adapter.addFragment(PlaylistFragment.newInstance(),"Recently Added");
        adapter.addFragment(TrackFragment.newInstance(),"Tracks");
        adapter.notifyDataSetChanged();

        viewPager.setAdapter(adapter);
    }
}