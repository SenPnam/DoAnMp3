package com.example.doanmp3.Adapter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.doanmp3.Activity.DanhSachBaiHatActivity;
import com.example.doanmp3.Activity.MainActivity;
import com.example.doanmp3.Activity.PlayNhacActivity;
import com.example.doanmp3.Fragment.UserFragment.UserBaiHatFragment;
import com.example.doanmp3.Fragment.UserFragment.UserPlaylistFragment;
import com.example.doanmp3.Model.BaiHat;
import com.example.doanmp3.Model.Playlist;
import com.example.doanmp3.R;
import com.example.doanmp3.Service.APIService;
import com.example.doanmp3.Service.DataService;
import com.example.doanmp3.Service.MusicService;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.DOWNLOAD_SERVICE;

public class SearchSongAdapter extends RecyclerView.Adapter<SearchSongAdapter.ViewHolder> {

    Context context;
    ArrayList<BaiHat> arrayList;
    boolean IsSearch;
    public boolean ViewMore;

    public SearchSongAdapter(Context context, ArrayList<BaiHat> arrayList, boolean isSearch) {
        this.context = context;
        this.arrayList = arrayList;
        IsSearch = isSearch;
        ViewMore = false;
    }

    public SearchSongAdapter(Context context, ArrayList<BaiHat> arrayList, boolean isSearch, boolean viewMore) {
        this.context = context;
        this.arrayList = arrayList;
        IsSearch = isSearch;
        ViewMore = viewMore;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.dong_search_song, parent, false);
        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
        BaiHat baiHat = arrayList.get(position);
        Glide.with(context).load(baiHat.getHinhBaiHat()).error(R.drawable.song).into(holder.imageView);
        holder.TenBaiHat.setText(baiHat.getTenBaiHat());
        holder.TenCaSi.setText(baiHat.getTenAllCaSi());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayNhacActivity.class);
            if (!IsSearch) {
                intent.putExtra("mangbaihat", arrayList);
                intent.putExtra("position", position);
                DanhSachBaiHatActivity.category ="Playlist";
                DanhSachBaiHatActivity.TenCategoty ="B??i h??t g???n ????y";
                intent.putExtra("recent", true);
                context.startActivity(intent);
            }
            else{
                ArrayList<BaiHat> BaiHatSearch = new ArrayList<>();
                BaiHatSearch.add(arrayList.get(position));
                DanhSachBaiHatActivity.category ="Danh M???c";
                DanhSachBaiHatActivity.TenCategoty ="Unknown";
                intent.putExtra("mangbaihat", BaiHatSearch);
                intent.putExtra("position", 0);
                context.startActivity(intent);
            }
        });

        holder.btnOptions.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(context,  holder.btnOptions);
            setupPopupMenu(position, popupMenu);
            popupMenu.show();
        });

    }
    @SuppressLint({"NewApi", "NonConstantResourceId"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setupPopupMenu(int postion, PopupMenu popupMenu) {
        popupMenu.getMenuInflater().inflate(R.menu.menu_options_baihat, popupMenu.getMenu());
        popupMenu.setGravity(Gravity.RIGHT);
        popupMenu.setForceShowIcon(true);

        if (UserBaiHatFragment.checkLiked(arrayList.get(postion).getIdBaiHat())) {
            popupMenu.getMenu().getItem(0).setTitle("B??? Th??ch");
            popupMenu.getMenu().getItem(0).setIcon(R.drawable.ic_love);
        } else {
            popupMenu.getMenu().getItem(0).setTitle("Th??ch");
            popupMenu.getMenu().getItem(0).setIcon(R.drawable.ic_hate);
        }
        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.baihat_like:
                    if (UserBaiHatFragment.checkLiked(arrayList.get(postion).getIdBaiHat())) {
                        BoThich(MainActivity.user.getIdUser(), arrayList.get(postion).getIdBaiHat(), postion);
                    } else
                        Thich(MainActivity.user.getIdUser(), arrayList.get(postion).getIdBaiHat(), postion);
                    break;
                case R.id.baihat_add:
                    BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
                    setupBottomSheetMenu(bottomSheetDialog, postion);
                    break;
                case R.id.add_to_queue:
                    StartService(postion);
                    popupMenu.dismiss();
                    break;
                case R.id.download:
                    Download(postion);
                    popupMenu.dismiss();
                    break;
            }

            return true;
        });
    }

    private void setupBottomSheetMenu(BottomSheetDialog dialog, int position) {
        dialog.setContentView(R.layout.dialog_add_baihat_playlist);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        RoundedImageView imgBaiHat;
        TextView txtBaiHat, txtCaSi;
        MaterialButton btnAddPlaylist;
        RecyclerView recyclerView;

        // ??nh X???
        imgBaiHat = dialog.findViewById(R.id.img_baihat_bottomsheet_add);
        txtBaiHat = dialog.findViewById(R.id.txt_tenbaihat_bottomsheet_add);
        txtCaSi = dialog.findViewById(R.id.txt_tencasi_bottomsheet_add);
        btnAddPlaylist = dialog.findViewById(R.id.add_playlist_bottomsheet_add);
        recyclerView = dialog.findViewById(R.id.rv_bottomsheet_add);

        // Set ???nh c???ng t??n
        Glide.with(context).load(arrayList.get(position).getHinhBaiHat()).error(R.drawable.song).into(imgBaiHat);
        if (txtBaiHat != null) {
            txtBaiHat.setText(arrayList.get(position).getTenBaiHat());
        }
        if (txtCaSi != null) {
            txtCaSi.setText(arrayList.get(position).getTenAllCaSi());
        }

        // Set RV
        ArrayList<Playlist> playlists = MainActivity.userPlaylist;
        UserPlaylistAdapter adapter = new UserPlaylistAdapter(playlists, context);
        adapter.setAddbaihat(true);
        adapter.setIdBaiHat(arrayList.get(position).getIdBaiHat());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        if (recyclerView != null) {
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(linearLayoutManager);
        }

        // bat su kien click tren recycleview de tat dialog

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (adapter.isResponse()) {
                    dialog.dismiss();
                    handler.removeCallbacks(this);
                }
                handler.postDelayed(this, 100);
            }
        };
        handler.postDelayed(runnable, 100);

        // taoplaylist moi
        assert btnAddPlaylist != null;
        btnAddPlaylist.setOnClickListener(v -> {
            OpenCreateDialog(position);
            dialog.dismiss();
        });

        // show dialog
        dialog.show();
    }

    private void OpenCreateDialog(int position) {
        Dialog dialog = new Dialog(context);
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
                int i = 0;
                if (MainActivity.userPlaylist != null) {
                    for (i = 0; i < MainActivity.userPlaylist.size(); i++) {
                        if (MainActivity.userPlaylist.get(i).getTen().equals(tenplaylist))
                            return;
                    }
                    if (i >= MainActivity.userPlaylist.size() || i == 0) {
                        ProgressDialog progressDialog;
                        progressDialog = ProgressDialog.show(context, "??ang T???o Playlist", "Loading...!", false, false);
                        DataService dataService = APIService.getUserService();
                        Call<String> callback = dataService.TaoPlaylist(MainActivity.user.getIdUser(), tenplaylist);
                        callback.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {
                                String Idplaylist = (String) response.body();
                                if (Idplaylist.equals("That Bai"))
                                    Toast.makeText(context, "L???i H??? Th???ng", Toast.LENGTH_SHORT).show();
                                else {
                                    // Th??m Playlist sau khi t???o
                                    Playlist playlist = new Playlist();
                                    playlist.setIdPlaylist(Idplaylist);
                                    playlist.setTen(tenplaylist);
                                    playlist.setHinhAnh("https://tiendung352001.000webhostapp.com/Client/image/ic_user_playlist.png");
                                    UserPlaylistFragment.userPlaylist.add(playlist);
                                    UserPlaylistFragment.CheckArrayListEmpty();
                                    // Th??m B??i H??t V??o Playlist M???i t???o
                                    Call<String> callBack = dataService.ThemBaiHatPlaylist(Idplaylist, arrayList.get(position).getIdBaiHat());
                                    callBack.enqueue(new Callback<String>() {
                                        @Override
                                        public void onResponse(Call<String> call, Response<String> response) {
                                            String result = (String) response.body();
                                            if (result != null) {
                                                if (result.equals("Thanh Cong"))
                                                    Toast.makeText(context, "???? C???p Nh???t Playlist", Toast.LENGTH_SHORT).show();
                                                else
                                                    Toast.makeText(context, "C???p Nh???t Th???t B???i", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<String> call, Throwable t) {

                                        }
                                    });
                                    UserPlaylistFragment.adapter.notifyDataSetChanged();
                                    dialog.dismiss();
                                }
                                progressDialog.dismiss();

                            }

                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                progressDialog.dismiss();
                                Toast.makeText(context, "L???i K???t N???i", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        edtTenPlaylist.setError("Playlist ???? T???n t???i");
                    }
                }
            }
        });

        dialog.show();
    }

    public void BoThich(String iduser, String idbaihat, int position) {
        DataService dataService = APIService.getUserService();
        Call<String> callback = dataService.BoThichBaiHat(idbaihat, iduser);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String result = (String) response.body();
                if (result != null) {
                    if (result.equals("Thanh Cong")) {
                        Toast.makeText(context, "???? B??? Th??ch", Toast.LENGTH_SHORT).show();
                        UserBaiHatFragment.BoThichBaiHat(arrayList.get(position).getIdBaiHat());
                        if (UserBaiHatFragment.arrayList.size() <= 0)
                            UserBaiHatFragment.textView.setVisibility(View.VISIBLE);

                    } else
                        Toast.makeText(context, "L???i H??? Th???ng", Toast.LENGTH_SHORT).show();
                }
                UserBaiHatFragment.adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {

            }
        });
    }

    public void Thich(String iduser, String idbaihat, int position) {
        DataService dataService = APIService.getUserService();
        Call<String> callback = dataService.YeuThichBaiHat(idbaihat, iduser);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                String result = (String) response.body();
                if (result != null) {
                    if (result.equals("Thanh Cong")) {
                        if (!UserBaiHatFragment.arrayList.contains(arrayList.get(position)))
                            UserBaiHatFragment.arrayList.add(arrayList.get(position));
                        if (UserBaiHatFragment.adapter.getItemCount() >= 0)
                            UserBaiHatFragment.textView.setVisibility(View.GONE);

                        UserBaiHatFragment.adapter.notifyDataSetChanged();
                        Toast.makeText(context, "???? Th??ch", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(context, "L???i H??? Th???ng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                Toast.makeText(context, "L???i M???ng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void StartService(int Pos) {
        Intent intent = new Intent(context, com.example.doanmp3.Service.MusicService.class);
        if (MusicService.arrayList == null) {
            ArrayList<BaiHat> baiHats = new ArrayList<>();
            baiHats.add(arrayList.get(Pos));
            intent.putExtra("mangbaihat", baiHats);
            intent.putExtra("audio", false);
            intent.putExtra("pos", 0);
            intent.putExtra("recent", false);
            DanhSachBaiHatActivity.category ="Playlist";
            DanhSachBaiHatActivity.TenCategoty="Ng???u Nhi??n";
            context.startService(intent);
            Toast.makeText(context, "???? Th??m", Toast.LENGTH_SHORT).show();
        } else {
            MusicService.AddtoPlaylist(context, arrayList.get(Pos));
        }
    }

    private void Download(int Pos) {
        try{
            String link = arrayList.get(Pos).getLinkBaiHat();
            String title = URLUtil.guessFileName(link, null, null);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link));
            request.setDescription("T???i xu???ng " + title);
            request.setTitle(title);
            request.allowScanningByMediaScanner();
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, title);
            DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);
            Toast.makeText(context, "??ang T???i Xu???ng", Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(context, "T???i Xu???ng Th???t B???i", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public int getItemCount() {
        if (arrayList != null) {
            if (ViewMore) {
                if (arrayList.size() < 5)
                    return arrayList.size();
                else
                    return 5;
            }
            else
                return arrayList.size();
        }

        return 0;
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView TenBaiHat, TenCaSi;
        MaterialButton btnOptions;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_song_search);
            TenBaiHat = itemView.findViewById(R.id.txt_song_search);
            TenCaSi = itemView.findViewById(R.id.txt_song_casi_search);
            btnOptions = itemView.findViewById(R.id.btn_options_baihat);
        }
    }

    public boolean isViewMore() {
        return ViewMore;
    }

    public void setViewMore(boolean viewMore) {
        ViewMore = viewMore;
        notifyDataSetChanged();
    }

}
