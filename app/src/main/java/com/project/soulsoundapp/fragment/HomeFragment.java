package com.project.soulsoundapp.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//
//import com.denzcoskun.imageslider.ImageSlider;
//import com.denzcoskun.imageslider.constants.ScaleTypes;
//import com.denzcoskun.imageslider.models.SlideModel;
import com.project.soulsoundapp.domain.SliderItems;
import com.project.soulsoundapp.R;
import com.project.soulsoundapp.adapter.PlaylistHorizontalAdpater;
import com.project.soulsoundapp.adapter.SliderAdapter;
import com.project.soulsoundapp.adapter.SongAdapter;
import com.project.soulsoundapp.helper.DatabaseHelper;
import com.project.soulsoundapp.model.Playlist;
import com.project.soulsoundapp.model.Song;
import com.project.soulsoundapp.service.ApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import com.project.soulsoundapp.adapter.ImageSliderAdapter;
public class HomeFragment extends Fragment {
    private RecyclerView rvDiscover, rvHitSong, rvTop100;
    private ViewPager2 viewPager2;
    private Handler slideHandler = new Handler();
    private DatabaseHelper db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        addControl(view);
        initView(view);
        banner();
    }

    private void banner() {
        List<SliderItems> sliderItems = new ArrayList<>();
        sliderItems.add(new SliderItems(R.drawable.item1));
        sliderItems.add(new SliderItems(R.drawable.item2));
        sliderItems.add(new SliderItems(R.drawable.item3));
        sliderItems.add(new SliderItems(R.drawable.item4));
        viewPager2.setAdapter(new SliderAdapter(sliderItems, viewPager2));
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        compositePageTransformer.addTransformer(new MarginPageTransformer(40));
        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                float r = 1-Math.abs(position);
                page.setScaleY(0.85f+r*0.15f);
            }
        });
        viewPager2.setPageTransformer(compositePageTransformer);
        viewPager2.setCurrentItem(3);
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                slideHandler.removeCallbacks(slideRunnable);
            }
        });
    }

    private  Runnable slideRunnable = new Runnable() {
        @Override
        public void run() {
            viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        slideHandler.removeCallbacks(slideRunnable);
    }

    @Override
    public void onResume() {
        super.onResume();
        slideHandler.postDelayed(slideRunnable, 2000);
    }

    private void initView(View view) {
        viewPager2=view.findViewById(R.id.viewpagerSlider);
    }


    private void addControl(View view) {
        db = DatabaseHelper.getInstance(view.getContext());
//  SETUP DISCOVER
        rvDiscover = view.findViewById(R.id.rvDiscover);
        ApiService.apiService.getPlaylistDiscover()
                .enqueue(new Callback<ApiService.ApiResponse<List<String>>>() {
                    @Override
                    public void onResponse(Call<ApiService.ApiResponse<List<String>>> call, Response<ApiService.ApiResponse<List<String>>> response) {
                        if(response.isSuccessful()) {
                            assert response.body() != null;
                            List<String> playlistDiscover = response.body().getData();
                            if(playlistDiscover != null && playlistDiscover.size() > 0) {
                                PlaylistHorizontalAdpater playlistHorizontalAdpater = new PlaylistHorizontalAdpater(getContext());
                                playlistHorizontalAdpater.setPlaylists(db.getPlaylistsByIds(playlistDiscover));
                                LinearLayoutManager managerDiscover = new LinearLayoutManager(getContext());
                                managerDiscover.setOrientation(LinearLayoutManager.HORIZONTAL);
                                rvDiscover.setLayoutManager(managerDiscover);
                                rvDiscover.setAdapter(playlistHorizontalAdpater);
                            } else {
                                rvDiscover.setVisibility(View.GONE);
                            }
                        } else {
                            rvDiscover.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.ApiResponse<List<String>>> call, Throwable throwable) {
                        rvDiscover.setVisibility(View.GONE);
                    }
                });


        rvHitSong = view.findViewById(R.id.rvHitSong);
        ApiService.apiService.getHitSong()
                .enqueue(new Callback<ApiService.ApiResponse<List<String>>>() {
                    @Override
                    public void onResponse(Call<ApiService.ApiResponse<List<String>>> call, Response<ApiService.ApiResponse<List<String>>> response) {
                        if(response.isSuccessful()) {
                            assert response.body() != null;
                            List<String> hitSong = response.body().getData();
                            if(hitSong != null && hitSong.size() > 0) {
                                SongAdapter songAdapter = new SongAdapter(getContext());
                                songAdapter.setSongs(db.getSongByIds(hitSong));
                                GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
                                gridLayoutManager.setOrientation(RecyclerView.HORIZONTAL);

                                rvHitSong.setLayoutManager(gridLayoutManager);
                                rvHitSong.setAdapter(songAdapter);
                            } else {
                                rvHitSong.setVisibility(View.GONE);
                            }
                        } else {
                            rvHitSong.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiService.ApiResponse<List<String>>> call, Throwable throwable) {
                        rvHitSong.setVisibility(View.GONE);
                    }
                });

        rvTop100 = view.findViewById(R.id.rvTop100);
        ApiService.apiService.getPlaylistTop100()
                .enqueue(new Callback<ApiService.ApiResponse<List<String>>>() {
                    @Override
                    public void onResponse(Call<ApiService.ApiResponse<List<String>>> call, Response<ApiService.ApiResponse<List<String>>> response) {
                        if(response.isSuccessful()) {
                            assert response.body() != null;
                            List<String> top100 = response.body().getData();
                            if(top100 != null && top100.size() > 0) {
                                PlaylistHorizontalAdpater playlistHorizontalAdpaterTop100 = new PlaylistHorizontalAdpater(getContext());
                                playlistHorizontalAdpaterTop100.setPlaylists(db.getPlaylistsByIds(top100));
                                LinearLayoutManager managerTop100 = new LinearLayoutManager(getContext());
                                managerTop100.setOrientation(LinearLayoutManager.HORIZONTAL);
                                rvTop100.setLayoutManager(managerTop100);
                                rvTop100.setAdapter(playlistHorizontalAdpaterTop100);
                            } else {
                                rvTop100.setVisibility(View.GONE);
                            }
                        } else {
                            rvTop100.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onFailure(Call<ApiService.ApiResponse<List<String>>> call, Throwable throwable) {
                        rvTop100.setVisibility(View.GONE);
                    }
                });
    }
}