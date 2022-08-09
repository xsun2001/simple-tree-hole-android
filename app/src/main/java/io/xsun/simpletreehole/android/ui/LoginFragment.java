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
import io.xsun.simpletreehole.android.service.TaskRunner;
import io.xsun.simpletreehole.android.service.UserService;

public class LoginFragment extends Fragment {

    private EditText email, password;
    private Button login, register;

    public LoginFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_login, container, false);

        email = view.findViewById(R.id.login_email);
        password = view.findViewById(R.id.login_password);
        login = view.findViewById(R.id.login_login_button);
        register = view.findViewById(R.id.login_register_button);

        login.setOnClickListener(this::onLoginClicked);

        register.setOnClickListener(v -> getParentFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, RegisterFragment.class, null)
                .addToBackStack(null)
                .commit());

        return view;
    }

    private void onLoginClicked(View v) {
        UserService.getInstance().login(
                v.getContext(),
                email.getText().toString(),
                password.getText().toString(),
                this::onLoginResult);
    }

    private void onLoginResult(TaskRunner.Result<Long> callback) {
        if (callback.isOk()) {
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, PostListFragment.class, null)
                    .addToBackStack(null)
                    .commit();
            Toast.makeText(getContext(), "Logged in", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Cannot log in: " + callback.getError().getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}