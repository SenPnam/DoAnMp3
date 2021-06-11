package com.example.doanmp3.Fragment.SearchFragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmp3.Adapter.AllSingerAdapter;
import com.example.doanmp3.Model.CaSi;
import com.example.doanmp3.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class SearchcasiFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private MaterialButton textView;
    private AllSingerAdapter adapter;
    public static ArrayList<CaSi> arrayList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_searchcasi, container, false);
        AnhXa();
        SetRCV();
        return view;
    }

    private void SetRCV() {
        if (arrayList != null) {
            if (arrayList.size() == 0)
                textView.setVisibility(View.VISIBLE);
            else {
                recyclerView.removeAllViews();
                adapter = new AllSingerAdapter(getContext(), arrayList);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                textView.setVisibility(View.INVISIBLE);
            }
        } else
            textView.setVisibility(View.VISIBLE);
    }

    private void AnhXa() {

        recyclerView = view.findViewById(R.id.rv_search_casi);
        textView = view.findViewById(R.id.txt_search_casi_noinfo);
    }


    public void SetRV() {
        if (recyclerView != null)
            recyclerView.removeAllViews();

        Handler handler = new Handler();

        Runnable run = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 300);
                if (SearchFragment.albums != null) {
                    arrayList = SearchFragment.caSis;
                    if (SearchFragment.albums.size() > 0) {
                        adapter = new AllSingerAdapter(getContext(), SearchFragment.caSis);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
                        textView.setVisibility(View.INVISIBLE);
                    } else {
                        textView.setText("Không Tìm Thấy Kết Quả");
                        textView.setVisibility(View.VISIBLE);
                    }
                    handler.removeCallbacks(this);
                }
            }
        };

        Runnable runnable = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                handler.postDelayed(this, 500);
                if (recyclerView != null) {
                    recyclerView.removeAllViews();
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("Đang Lấy Dữ Liệu...!");
                    handler.postDelayed(run, 300);
                    handler.removeCallbacks(this);
                }

            }
        };
        handler.postDelayed(runnable, 500);

    }

}

//                      if (SearchFragment.caSis != null) {
//                        arrayList = SearchFragment.caSis;
//                        if (SearchFragment.caSis.size() > 0) {
//                            adapter = new AllSingerAdapter(getContext(), SearchFragment.caSis);
//                            recyclerView.setAdapter(adapter);
//                            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//                            textView.setVisibility(View.INVISIBLE);
//                        } else {
//                            textView.setVisibility(View.VISIBLE);
//                            textView.setText("Không Tìm Thấy Kết Quả");
//                        }
//                        handler.removeCallbacks(this);
//                    } else {
//                        textView.setVisibility(View.VISIBLE);
//                        textView.setText("Đang Lấy Dữ Liệu...!");
//                    }