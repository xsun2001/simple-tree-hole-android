package io.xsun.simpletreehole.android.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import io.xsun.simpletreehole.android.R;
import io.xsun.simpletreehole.android.data.UserProfile;
import io.xsun.simpletreehole.android.service.UserService;

public class UserProfileFragment extends Fragment {
    private UserProfile user;
    private TextView id;
    private EditText email, nickname;
    private Button update, logout;
    private ProgressBar progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        user = UserService.getInstance().getLoggedUser(view.getContext());
        id = view.findViewById(R.id.user_profile_id);
        email = view.findViewById(R.id.user_profile_email);
        nickname = view.findViewById(R.id.user_profile_nickname);
        update = view.findViewById(R.id.user_profile_update_button);
        logout = view.findViewById(R.id.user_profile_logout_button);
        progress = view.findViewById(R.id.user_profile_progress);

        id.setText(Long.toString(user.getId()));
        email.setText(user.getEmail());
        nickname.setText(user.getNickname());
        update.setOnClickListener(this::onUpdate);
        logout.setOnClickListener(this::onLogout);

        return view;
    }

    private void onUpdate(View view) {
        update.setClickable(false);
        progress.setVisibility(View.VISIBLE);
        UserService.getInstance().updateUserProfile(
                user.getId(),
                email.getText().toString(),
                nickname.getText().toString(),
                updateResult -> {
                    if (updateResult.isOk()) {
                        user = updateResult.getResult();
                        Toast.makeText(view.getContext(), "Update Success", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(
                                view.getContext(),
                                "Update profile failed: " + updateResult.getError().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    email.setText(user.getEmail());
                    nickname.setText(user.getNickname());
                    update.setClickable(true);
                    progress.setVisibility(View.GONE);
                });
    }

    private void onLogout(View view) {
        UserService.getInstance().logout(view.getContext());
        Utils.replaceFragment(this, PostListFragment.class);
    }
}