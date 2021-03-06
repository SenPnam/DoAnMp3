package com.example.doanmp3.Fragment.SearchFragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmp3.Adapter.AlbumAdapter;
import com.example.doanmp3.Adapter.AllSingerAdapter;
import com.example.doanmp3.Adapter.PlaylistAdapter;
import com.example.doanmp3.Adapter.SearchSongAdapter;
import com.example.doanmp3.Model.Album;
import com.example.doanmp3.Model.BaiHat;
import com.example.doanmp3.Model.CaSi;
import com.example.doanmp3.Model.Playlist;
import com.example.doanmp3.R;
import com.example.doanmp3.Service.APIService;
import com.example.doanmp3.Service.DataService;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AllSearchFragment extends Fragment {

    View view;
    public int countResult;
    int progress = 0;
    // Layout Kết Quả
    LinearLayout linearLayout;
    RelativeLayout layoutBaihat, layoutCasi, layoutAlbum, layoutPlaylist, layoutNoinfo;


    // Recyclerview
    RecyclerView rvBaiHat, rvAlbum, rvCaSi, rvPlaylist;

    // btn Xem Thêm
    MaterialButton btnBaiHat, btnAlbum, btnCaSi, btnPlaylist;

    // Adapter
    SearchSongAdapter adapterSong;
    AlbumAdapter adapterAlbum;
    AllSingerAdapter adapterSinger;
    PlaylistAdapter adapterPlaylist;

    //Arraylist
    static ArrayList<BaiHat> baiHats;
    static ArrayList<Album> albums;
    static ArrayList<CaSi> caSis;
    static ArrayList<Playlist> playlists;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_all_search, container, false);
        AnhXa();
        ClickViewMore();
        linearLayout.setVisibility(View.VISIBLE);
        return view;
    }


    private void AnhXa() {
        linearLayout = view.findViewById(R.id.layout_all_result);
        layoutNoinfo = view.findViewById(R.id.txt_search_result_noinfo);
        layoutBaihat = view.findViewById(R.id.layout_result_baihat);
        layoutAlbum = view.findViewById(R.id.layout_result_album);
        layoutCasi = view.findViewById(R.id.layout_result_casi);
        layoutPlaylist = view.findViewById(R.id.layout_result_playlist);
        rvBaiHat = view.findViewById(R.id.rv_result_baihat);
        rvAlbum = view.findViewById(R.id.rv_result_album);
        rvCaSi = view.findViewById(R.id.rv_result_casi);
        rvPlaylist = view.findViewById(R.id.rv_result_playlist);
        btnBaiHat = view.findViewById(R.id.btn_viewmore_baihat_result);
        btnAlbum = view.findViewById(R.id.btn_viewmore_album_result);
        btnCaSi = view.findViewById(R.id.btn_viewmore_casi_result);
        btnPlaylist = view.findViewById(R.id.btn_viewmore_playlist_result);

    }

    private void ClickViewMore() {
        btnBaiHat.setOnClickListener(v -> {
            if (SearchFragment.viewPager != null) {
                SearchFragment.viewPager.setCurrentItem(1);
            }
        });

        btnAlbum.setOnClickListener(v -> {
            if (SearchFragment.viewPager != null) {
                SearchFragment.viewPager.setCurrentItem(2);
            }
        });

        btnCaSi.setOnClickListener(v -> {
            if (SearchFragment.viewPager != null) {
                SearchFragment.viewPager.setCurrentItem(3);
            }
        });

        btnPlaylist.setOnClickListener(v -> {
            if (SearchFragment.viewPager != null) {
                SearchFragment.viewPager.setCurrentItem(4);
            }
        });
    }

    public void Search(String query) {
        progress = 0;
        countResult = 0;
        linearLayout.setVisibility(View.INVISIBLE);
        layoutNoinfo.setVisibility(View.GONE);
        SearchBaiHat(query);
        SearchAlbum(query);
        SearchCaSi(query);
        SearchPlaylist(query);
    }

    private void SearchBaiHat(String query) {
        rvBaiHat.removeAllViews();
        DataService dataService = APIService.getService();
        Call<List<BaiHat>> callback = dataService.GetSearchBaiHat(query);
        callback.enqueue(new Callback<List<BaiHat>>() {
            @Override
            public void onResponse(@NotNull Call<List<BaiHat>> call, Response<List<BaiHat>> response) {
                baiHats = (ArrayList<BaiHat>) response.body();
                if (baiHats != null) {
                    if (baiHats.size() > 0) {
                        layoutBaihat.setVisibility(View.VISIBLE);
                        SetRecycleViewBaiHat();
                        CountResult(true);
                        return;
                    }
                }
                CountResult(false);
                layoutBaihat.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<List<BaiHat>> call, @NotNull Throwable t) {
            }
        });
    }

    private void SearchAlbum(String query) {
        rvAlbum.removeAllViews();
        DataService dataService = APIService.getService();
        Call<List<Album>> callback = dataService.GetSearchAlbum(query);
        callback.enqueue(new Callback<List<Album>>() {
            @Override
            public void onResponse(Call<List<Album>> call, Response<List<Album>> response) {
                albums = (ArrayList<Album>) response.body();
                if (albums != null) {
                    if (albums.size() > 0) {
                        layoutAlbum.setVisibility(View.VISIBLE);
                        CountResult(true);
                        SetRecycleViewAlbum();
                        return;
                    }
                }
                CountResult(false);
                layoutAlbum.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Album>> call, Throwable t) {

            }
        });
    }

    private void SearchCaSi(String query) {
        rvCaSi.removeAllViews();
        DataService dataService = APIService.getService();
        Call<List<CaSi>> callback = dataService.GetSearchCaSi(query);
        callback.enqueue(new Callback<List<CaSi>>() {
            @Override
            public void onResponse(Call<List<CaSi>> call, Response<List<CaSi>> response) {
                caSis = (ArrayList<CaSi>) response.body();
                if (caSis != null) {
                    if (caSis.size() > 0) {
                        layoutCasi.setVisibility(View.VISIBLE);
                        CountResult(true);
                        SetRecycleViewCaSi();
                        return;
                    }
                }
                CountResult(false);
                layoutCasi.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<CaSi>> call, Throwable t) {

            }
        });
    }

    private void SearchPlaylist(String query) {
        rvPlaylist.removeAllViews();
        DataService dataService = APIService.getService();
        Call<List<Playlist>> callback = dataService.GetSearchPlaylist(query);
        callback.enqueue(new Callback<List<Playlist>>() {
            @Override
            public void onResponse(Call<List<Playlist>> call, Response<List<Playlist>> response) {
                playlists = (ArrayList<Playlist>) response.body();
                SearchFragment.CountResult();
                if (playlists != null) {
                    if (playlists.size() > 0) {
                        layoutPlaylist.setVisibility(View.VISIBLE);
                        CountResult(true);
                        SetRecycleViewPlaylists();
                        return;
                    }
                }
                CountResult(false);
                layoutPlaylist.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NotNull Call<List<Playlist>> call, @NotNull Throwable t) {

            }
        });
    }


    private void SetRecycleViewBaiHat() {
        adapterSong = new SearchSongAdapter(getContext(), baiHats, true, true);
        rvBaiHat.setAdapter(adapterSong);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        LayoutAnimationController animlayout = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_anim_left_to_right);
        rvBaiHat.setLayoutAnimation(animlayout);
        rvBaiHat.setLayoutManager(linearLayoutManager);
    }

    private void SetRecycleViewAlbum() {
        adapterAlbum = new AlbumAdapter(getContext(), albums, true);
        rvAlbum.setAdapter(adapterAlbum);
        rvAlbum.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    private void SetRecycleViewCaSi() {
        adapterSinger = new AllSingerAdapter(getContext(), caSis, true);
        rvCaSi.setAdapter(adapterSinger);
        rvCaSi.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }

    private void SetRecycleViewPlaylists() {
        adapterPlaylist = new PlaylistAdapter(getContext(), playlists, true);
        rvPlaylist.setAdapter(adapterPlaylist);
        rvPlaylist.setLayoutManager(new GridLayoutManager(getContext(), 2));
    }


    private void CountResult(boolean haveResult) {
        progress++;
        if (haveResult)
            countResult++;
        if (progress >= 2) {

            if (countResult >= 1) {
                linearLayout.setVisibility(View.VISIBLE);
                layoutNoinfo.setVisibility(View.GONE);
            }

            if (progress >= 4) {
                SearchFragment.CountResult();
                if (countResult == 0) {
                    linearLayout.setVisibility(View.GONE);
                    layoutNoinfo.setVisibility(View.VISIBLE);
                }
            }
        }
    }

}