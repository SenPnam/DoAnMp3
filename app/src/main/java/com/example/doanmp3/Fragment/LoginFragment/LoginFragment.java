package com.example.doanmp3.Fragment.LoginFragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.doanmp3.Activity.MainActivity;
import com.example.doanmp3.Model.User;
import com.example.doanmp3.R;
import com.example.doanmp3.Service.APIService;
import com.example.doanmp3.Service.DataService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginFragment extends Fragment {
    ProgressDialog mProgressDialog;
    View view;
    TextInputEditText edtEmail, edtPassword;
    TextInputLayout passwordinput;
    TextView txtRegister, txtForget;
    MaterialButton btnLogin;
    User user;
    public static SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        AnhXa();
        AutoLogin();
        EventClick();
        return view;
    }


    private void AnhXa() {
        edtEmail = view.findViewById(R.id.edt_email_login);
        edtPassword = view.findViewById(R.id.edt_password_login);
        txtForget = view.findViewById(R.id.forget_password);
        txtRegister = view.findViewById(R.id.txt_register_login);
        btnLogin = view.findViewById(R.id.btn_facebook_login);
        passwordinput = view.findViewById(R.id.edt_input_password_login);
        sharedPreferences = getContext().getSharedPreferences("login", Context.MODE_PRIVATE);
    }

    private void AutoLogin() {
        edtEmail.setText(sharedPreferences.getString("username", ""));
        edtPassword.setText(sharedPreferences.getString("password", ""));
        view.setVisibility(View.INVISIBLE);
        if (!edtPassword.getText().toString().equals("") && !edtEmail.getText().toString().equals("")) { {
                Login(edtEmail.getText().toString(), edtPassword.getText().toString());
            }
        } else
            view.setVisibility(View.VISIBLE);
    }

    private void EventClick() {

        // ????ng K??
        txtRegister.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment));

        //????ng Nh???p

        btnLogin.setOnClickListener(v -> {
            btnLogin.setClickable(false);
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            if (email.equals(""))
                edtEmail.setError("Email tr???ng");
            else {
                if (password.equals(""))
                    edtPassword.setError("M???t Kh???u Tr???ng");
                else {
                    Login(email, password);
                    btnLogin.setClickable(true);
                }
            }
        });

        txtForget.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_forgotPasswordFragment));

    }

    private void Login(String email, String password) {
        mProgressDialog = ProgressDialog.show(getContext(), "??ang ????ng Nh???p", "Vui L??ng Ch???...", true, true);
        DataService dataService = APIService.getUserService();
        Call<User> callback = dataService.GetUser(email, password);
        callback.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                user = (User) response.body();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                if (user.getIdUser().equals("-1")) {
                    mProgressDialog.dismiss();
                    edtPassword.setError("T??i kho???n ho???c m???t kh???u kh??ng ????ng");
                    Toast.makeText(getContext(), "T??i Kho???n Ho???c M???t Kh???u Kh??ng ????ng", Toast.LENGTH_SHORT).show();
                    edtPassword.setText("");
                    view.setVisibility(View.VISIBLE);
                    editor.remove("username");
                    editor.remove("password");
                    editor.apply();

                } else {
                    mProgressDialog.dismiss();
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.putExtra("user", user);
                    startActivity(intent);
                    editor.putString("username", email);
                    editor.putString("password", password);
                    editor.commit();
                    Toast.makeText(getContext(), "????ng Nh???p Th??nh C??ng", Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                try {
                    view.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "L???i K???t N???i", Toast.LENGTH_SHORT).show();
                    mProgressDialog.dismiss();
                } catch (Exception ignored){}


            }
        });
    }


}