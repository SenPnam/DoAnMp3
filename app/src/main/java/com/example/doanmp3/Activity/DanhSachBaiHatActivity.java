package com.example.doanmp3.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanmp3.Adapter.AllSongAdapter;
import com.example.doanmp3.Model.Album;
import com.example.doanmp3.Model.BaiHat;
import com.example.doanmp3.Model.ChuDeTheLoai;
import com.example.doanmp3.Model.Playlist;
import com.example.doanmp3.R;
import com.example.doanmp3.Service.APIService;
import com.example.doanmp3.Service.DataService;
import com.example.doanmp3.Service.MusicService;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DanhSachBaiHatActivity extends AppCompatActivity {

    // Mặc định
    CoordinatorLayout coordinatorLayout;
    CollapsingToolbarLayout collapsingToolbarLayout;
    Toolbar toolbar;
    RecyclerView recyclerView;
    FloatingActionButton button;
    LinearLayoutManager linearLayoutManager;
    ImageView imageView;
    ArrayList<BaiHat> arrayList;
    public static String category = "";
    public static String TenCategoty = "";
    ProgressBar progressBar;
    // Tùy Biến
    Album album;
    Playlist playlist;
    ChuDeTheLoai cdtl;
    LayoutAnimationController animlayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_danh_sach_bai_hat);
        overridePendingTransition(R.anim.from_right, R.anim.to_left);

        AnhXa();
        GetIntent();
        init();
        eventClick();
    }

    private void eventClick() {
        button.setOnClickListener(v -> {
            if (arrayList != null) {
                if (arrayList.size() > 0) {
                    Intent intentt = new Intent(DanhSachBaiHatActivity.this, PlayNhacActivity.class);
                    Random rd = new Random();
                    intentt.putExtra("mangbaihat", arrayList);
                    intentt.putExtra("position", rd.nextInt(arrayList.size()));
                    MusicService.random = true;
                    startActivity(intentt);
                }
            }

        });
    }

    private void init() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

    }

    private void GetIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("album")) {
                album = (Album) intent.getSerializableExtra("album");
                category = "Album";
                TenCategoty = album.getTenAlbum();
                SetValueView(album.getTenAlbum(), album.getHinhAlbum());
                GetDataAlbum(album.getIdAlbum());
            } else {
                if (intent.hasExtra("playlist")) {
                    playlist = (Playlist) intent.getSerializableExtra("playlist");
                    category = "Playlist";
                    TenCategoty = playlist.getTen();
                    SetValueView(playlist.getTen(), playlist.getHinhAnh());
                    GetDataPlaylist(playlist.getIdPlaylist());
                } else {
                    if (intent.hasExtra("ChuDe")) {
                        cdtl = (ChuDeTheLoai) intent.getSerializableExtra("ChuDe");
                        category = "Chủ Đề";
                        TenCategoty = cdtl.getTen();
                        SetValueView(cdtl.getTen(), cdtl.getHinh());
                        GetDataChuDe(cdtl.getId());
                    } else {
                        if (intent.hasExtra("TheLoai")) {
                            cdtl = (ChuDeTheLoai) intent.getSerializableExtra("TheLoai");
                            category = "Thể Loại";
                            TenCategoty = cdtl.getTen();
                            SetValueView(cdtl.getTen(), cdtl.getHinh());
                            GetDataTheLoai(cdtl.getId());
                        }

                    }
                }
            }
        }

    }


    private void AnhXa() {
        imageView = findViewById(R.id.img_tieudedanhsach);
        coordinatorLayout = findViewById(R.id.coordinatorlayout);
        collapsingToolbarLayout = findViewById(R.id.collapsingtoolbarlayout);
        toolbar = findViewById(R.id.toolbar_dsbh);
        recyclerView = findViewById(R.id.rv_dsbh);
        button = findViewById(R.id.btn_action);
        progressBar = findViewById(R.id.progress_load_baihat);
        linearLayoutManager = new LinearLayoutManager(DanhSachBaiHatActivity.this, LinearLayoutManager.VERTICAL, false);
        animlayout = AnimationUtils.loadLayoutAnimation(this, R.anim.layout_anim_left_to_right);
    }


    private void SetValueView(String Ten, String Hinh) {
        collapsingToolbarLayout.setTitle(Ten);
        Glide.with(DanhSachBaiHatActivity.this).load(Hinh).into(imageView);
    }

    private void GetDataAlbum(String id) {

        DataService dataService = APIService.getService();
        Call<List<BaiHat>> callback = dataService.GetBaiHatAlbum(id);

        callback.enqueue(new Callback<List<BaiHat>>() {
            @Override
            public void onResponse(Call<List<BaiHat>> call, Response<List<BaiHat>> response) {
                arrayList = (ArrayList<BaiHat>) response.body();
                if(arrayList == null)
                    return;
                AllSongAdapter adapter = new AllSongAdapter(DanhSachBaiHatActivity.this, arrayList);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutAnimation(animlayout);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<List<BaiHat>> call, Throwable t) {
                Toast.makeText(DanhSachBaiHatActivity.this, "Lỗi Kết Nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void GetDataPlaylist(String id) {
        DataService dataService = APIService.getService();
        Call<List<BaiHat>> call = dataService.GetBaiHatPlaylist(id);
        call.enqueue(new Callback<List<BaiHat>>() {
            @Override
            public void onResponse(Call<List<BaiHat>> call, Response<List<BaiHat>> response) {
                arrayList = (ArrayList<BaiHat>) response.body();
                if(arrayList == null)
                    return;
                AllSongAdapter adapter = new AllSongAdapter(DanhSachBaiHatActivity.this, arrayList);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutAnimation(animlayout);
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onFailure(Call<List<BaiHat>> call, Throwable t) {
                Toast.makeText(DanhSachBaiHatActivity.this, "Lỗi Kết Nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void GetDataChuDe(String id) {
        DataService dataService = APIService.getService();
        Call<List<BaiHat>> callback = dataService.GetBaiHatChuDe(id);
        callback.enqueue(new Callback<List<BaiHat>>() {
            @Override
            public void onResponse(Call<List<BaiHat>> call, Response<List<BaiHat>> response) {
                arrayList = (ArrayList<BaiHat>) response.body();
                if(arrayList == null)
                    return;
                AllSongAdapter adapter = new AllSongAdapter(DanhSachBaiHatActivity.this, arrayList);
                recyclerView.setLayoutAnimation(animlayout);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<List<BaiHat>> call, Throwable t) {
                Toast.makeText(DanhSachBaiHatActivity.this, "Lỗi Kết Nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void GetDataTheLoai(String id) {
        DataService dataService = APIService.getService();
        Call<List<BaiHat>> callback = dataService.GetBaiHatTheLoai(id);
        callback.enqueue(new Callback<List<BaiHat>>() {
            @Override
            public void onResponse(Call<List<BaiHat>> call, Response<List<BaiHat>> response) {
                arrayList = (ArrayList<BaiHat>) response.body();
                if(arrayList == null)
                    return;
                AllSongAdapter adapter = new AllSongAdapter(DanhSachBaiHatActivity.this, arrayList);
                recyclerView.setLayoutManager(linearLayoutManager);
                recyclerView.setLayoutAnimation(animlayout);
                recyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onFailure(Call<List<BaiHat>> call, Throwable t) {
                Toast.makeText(DanhSachBaiHatActivity.this, "Lỗi Kết Nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.from_left, R.anim.to_right);
    }
}