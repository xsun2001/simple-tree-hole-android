package io.xsun.simpletreehole.android.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import io.xsun.simpletreehole.android.R;
import io.xsun.simpletreehole.android.service.UserService;

public class RegisterFragment extends Fragment {

    private EditText email, nickname, password;
    private Button register;

    public RegisterFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_register, container, false);

        email = view.findViewById(R.id.register_email);
        nickname = view.findViewById(R.id.register_nickname);
        password = view.findViewById(R.id.register_password);
        register = view.findViewById(R.id.register_button);

        register.setOnClickListener(this::onRegister);

        return view;
    }

    private void onRegister(View view) {
        register.setClickable(false);
        UserService.getInstance().register(
                email.getText().toString(),
                nickname.getText().toString(),
                password.getText().toString(),
                result -> {
                    register.setClickable(true);
                    if (result.isOk()) {
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, LoginFragment.class, null)
                                .addToBackStack(null)
                                .commit();
                        Toast.makeText(getContext(), "Register success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Register failed: " + result.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

}