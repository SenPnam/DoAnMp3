package com.example.doanmp3.Fragment.UserFragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doanmp3.Activity.MainActivity;
import com.example.doanmp3.Adapter.UserPlaylistAdapter;
import com.example.doanmp3.Model.Playlist;
import com.example.doanmp3.R;
import com.example.doanmp3.Service.APIService;
import com.example.doanmp3.Service.DataService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserPlaylistFragment extends Fragment {

    View view;
    public RelativeLayout btnAddPlaylist;
    public RecyclerView recyclerView;
    public static ArrayList<Playlist> userPlaylist;
    @SuppressLint("StaticFieldLeak")
    public static UserPlaylistAdapter adapter;
    @SuppressLint("StaticFieldLeak")
    public static RelativeLayout Noinfo;
    ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user_playlist, container, false);
        AnhXa();
        SetUpRecycleview();
        EventClick();
        return view;
    }


    private void AnhXa() {
        btnAddPlaylist = view.findViewById(R.id.relative_btn_add_playlist);
        recyclerView = view.findViewById(R.id.rv_user_playlist);
        Noinfo = view.findViewById(R.id.txt_user_baihat_yeuthich);
    }


    private void SetUpRecycleview() {
        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 100);
                if (MainActivity.userPlaylist != null) {
                    userPlaylist = MainActivity.userPlaylist;
                    CheckArrayListEmpty();
                    adapter = new UserPlaylistAdapter(userPlaylist, getContext());
                    recyclerView.setAdapter(adapter);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                    linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
                    LayoutAnimationController animlayout = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_anim_left_to_right);
                    recyclerView.setLayoutAnimation(animlayout);
                    recyclerView.setLayoutManager(linearLayoutManager);
                    handler.removeCallbacks(this);
                }
            }
        };
        handler.postDelayed(runnable, 100);
    }

    private void EventClick() {
        btnAddPlaylist.setOnClickListener(v -> OpenCreateDialog());
    }

    private void OpenCreateDialog() {
        Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_playlist);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = dialog.getWindow();

        if (window == null)
            return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.CENTER;
        window.setAttributes(layoutParams);
        dialog.setCancelable(true);

        TextInputEditText edtTenPlaylist;
        MaterialButton btnConfirm, btnCancel;

        edtTenPlaylist = dialog.findViewById(R.id.edt_add_playlist);
        btnCancel = dialog.findViewById(R.id.btnCancel);
        btnConfirm = dialog.findViewById(R.id.btnAdd);


        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnConfirm.setOnClickListener(v -> {
            String tenplaylist = edtTenPlaylist.getText().toString().trim();
            if (tenplaylist.equals(""))
                edtTenPlaylist.setError("T??n Playlist Tr???ng");
            else {
                if (userPlaylist != null) {
                    if (!CheckUserPlaylistExist(tenplaylist)) {
                        progressDialog = ProgressDialog.show(getContext(), "??ang T???o Playlist", "Loading...!", false, false);
                        DataService dataService = APIService.getUserService();
                        Call<String> callback = dataService.TaoPlaylist(MainActivity.user.getIdUser(), tenplaylist);
                        callback.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                String Idplaylist = (String) response.body();
                                if (Idplaylist.equals("That Bai"))
                                    Toast.makeText(getContext(), "L???i H??? Th???ng", Toast.LENGTH_SHORT).show();
                                else {
                                    Playlist playlist = new Playlist();
                                    playlist.setIdPlaylist(Idplaylist);
                                    playlist.setTen(tenplaylist);
                                    playlist.setHinhAnh("https://tiendung352001.000webhostapp.com/Client/image/ic_user_playlist.png");
                                    userPlaylist.add(playlist);
                                    CheckArrayListEmpty();
                                    adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "T???o Th??nh C??ng", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "L???i K???t N???i", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        edtTenPlaylist.setError("Playlist ???? T???n t???i");
                        Toast.makeText(getContext(), "Playlist ???? T???n T???i", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        dialog.show();
    }

    public static boolean CheckUserPlaylistExist(String TenPlaylist) {

        int i = 0;
        for (i = 0; i < userPlaylist.size(); i++) {
            if (userPlaylist.get(i).getTen().equals(TenPlaylist)) {
                return true;
            }
        }
        return false;
    }

    public static void ChangeNamePlaylist(String IdPlaylist, String TenPlaylist) {
        for (int i = 0; i < userPlaylist.size(); i++) {
            if (userPlaylist.get(i).getIdPlaylist().equals(IdPlaylist)) {
                userPlaylist.get(i).setTen(TenPlaylist);
                adapter.notifyItemChanged(i);
                return;
            }
        }

    }

    public static void RemovePlaylist(String IdPlaylist) {
        for (int i = 0; i < userPlaylist.size(); i++) {
            if (userPlaylist.get(i).getIdPlaylist().equals(IdPlaylist)) {
                userPlaylist.remove(i);
                adapter.notifyItemRemoved(i);
                CheckArrayListEmpty();
                return;
            }
        }
    }


    public static void CheckArrayListEmpty() {
        if (userPlaylist != null) {
            if (userPlaylist.size() > 0) {
                Noinfo.setVisibility(View.GONE);
                return;
            }
        }
        Noinfo.setVisibility(View.VISIBLE);

    }
}