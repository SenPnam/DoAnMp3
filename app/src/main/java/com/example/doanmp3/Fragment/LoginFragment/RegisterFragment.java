package com.example.doanmp3.Fragment.LoginFragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.example.doanmp3.Model.Email;
import com.example.doanmp3.R;
import com.example.doanmp3.Service.APIService;
import com.example.doanmp3.Service.DataService;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterFragment extends Fragment {

    ProgressDialog progressDialog;
    View view;
    TextInputEditText edtEmail, edtUsername, edtPassword, edtCpassword;
    MaterialButton btnRegister;
    TextView txtlogin;
    TextView haveacount;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_register, container, false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        AnhXa();
        EventClick();
        return view;
    }

    private void AnhXa() {
        edtEmail = view.findViewById(R.id.edt_email_register);
        edtUsername = view.findViewById(R.id.edt_username_register);
        edtPassword = view.findViewById(R.id.edt_password_register);
        edtCpassword = view.findViewById(R.id.edt_cpassword_register);
        btnRegister = view.findViewById(R.id.btn_register);
        txtlogin = view.findViewById(R.id.txt_register_login);
        haveacount = view.findViewById(R.id.txt_haveac);
    }

    private void EventClick() {
        btnRegister.setOnClickListener(v -> {
            btnRegister.setClickable(false);
            String email = edtEmail.getText().toString().trim();
            String username = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString();

            if (isValid()) {
                progressDialog = ProgressDialog.show(getContext(), "??ang Th???c Hi???n", "Vui L??ng Ch???...", true, true);
                CheckExisit(email, username, password);
            } else {
                btnRegister.setClickable(true);

            }
        });

        txtlogin.setOnClickListener(v -> Navigation.findNavController(view).navigateUp());
    }


    private boolean isValid() {
        if (edtEmail.getText().toString().trim().equals("")) {
            edtEmail.setError("Email tr???ng");
            Toast.makeText(getContext(), "Email tr???ng", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (!EmailIsValid(edtEmail.getText().toString())) {
                edtEmail.setError("Email kh??ng h???p l???");
                Toast.makeText(getContext(), "Email kh??ng h???p l???", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (edtUsername.getText().toString().trim().equals("")) {
            edtUsername.setError("Username tr???ng");
            Toast.makeText(getContext(), "Username tr???ng", Toast.LENGTH_SHORT).show();
            return false;
        } else {
            if (edtUsername.getText().toString().length() < 5) {
                edtUsername.setError("Username c?? t???i thi???u 6 k?? t???");
                Toast.makeText(getContext(), "Username c?? t???i thi???u 6 k?? t???", Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        if (edtPassword.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Vui L??ng Nh???p M???t Kh???u", Toast.LENGTH_SHORT).show();
            edtPassword.setError("Vui L??ng Nh???p M???t Kh???u");
            return false;
        } else {
            if (edtPassword.getText().toString().length() < 6) {
                Toast.makeText(getContext(), "M???t Kh???u ph???i c?? t???i thi???u 6 k?? t???", Toast.LENGTH_SHORT).show();
                edtPassword.setError("M???t Kh???u ph???i c?? t???i thi???u 6 k?? t???");
                return false;
            } else {
                if (edtCpassword.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Vui l??ng x??c nh???n m???t kh???u", Toast.LENGTH_SHORT).show();
                    edtCpassword.setError("Vui l??ng x??c nh???n m???t kh???u");
                    return false;
                } else {
                    if (!edtCpassword.getText().toString().equals(edtPassword.getText().toString())) {
                        edtCpassword.setError("M???t Kh???u Kh??ng Tr??ng Kh???p");
                        Toast.makeText(getContext(), "M???t Kh???u Kh??ng Tr??ng Kh???p", Toast.LENGTH_SHORT).show();
                        edtCpassword.setText("");
                        return false;
                    }
                }
            }
        }


        return true;
    }

    public static boolean EmailIsValid(String email) {
        if (email == null)
            return false;

        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public void CheckExisit(String email, String username, String password) {
        DataService dataService = APIService.getUserService();
        Call<String> callback = dataService.CheckEmailExist(email);
        callback.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                btnRegister.setClickable(true);
                progressDialog.dismiss();
                String exist = response.body();
                if (exist.equals("F")) {
                    CreateCode(email, username, password);
                } else {
                    edtEmail.setError("Email ???? t???n t???i");
                    Toast.makeText(getContext(), "Email ???? t???n t???i", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(getActivity(), "L???i K???t N???i", Toast.LENGTH_SHORT).show();
                btnRegister.setClickable(true);
                progressDialog.dismiss();
            }
        });
    }

    private void CreateCode(String email, String username, String password) {
        Random random = new Random();
        int code = random.nextInt(99999 - 10000) + 10000;
        Email mEmail = new Email();
        if (mEmail.Sendto(edtEmail.getText().toString(), "Ho??n T???t ????ng K??", "M?? X??c Nh???n T??i Kh???n MP C???a B???n l??:" + code)) {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "M?? X??c Nh???n ???? ???????c G???i ?????n Email C???a B???n", Toast.LENGTH_SHORT).show();
            Bundle bundle = new Bundle();
            bundle.putInt("code", code);
            bundle.putString("email", email);
            bundle.putString("username", username);
            bundle.putString("password", password);
            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_confirmEmailFragment, bundle);
        } else {
            progressDialog.dismiss();
            Toast.makeText(getContext(), "H??? Th???ng L???i! Vui L??ng Th??? L???i Sau", Toast.LENGTH_LONG).show();
        }
    }

}