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
import io.xsun.simpletreehole.android.service.PostCommentService;
import io.xsun.simpletreehole.android.service.UserService;

public class CreatePostFragment extends Fragment {

    private EditText content;
    private Button button;

    public CreatePostFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        var view = inflater.inflate(R.layout.fragment_create_post, container, false);

        content = view.findViewById(R.id.create_post_content);
        button = view.findViewById(R.id.create_post_button);

        button.setOnClickListener(this::onCreatePost);

        return view;
    }

    private void onCreatePost(View view) {
        button.setClickable(false);
        PostCommentService.getInstance().createPost(
                UserService.getInstance().getLoggedUser(view.getContext()).getId(),
                content.getText().toString(),
                res -> {
                    button.setClickable(true);
                    if (res.isOk()) {
                        Utils.replaceFragment(this, PostListFragment.class);
                        Toast.makeText(view.getContext(), "Post created", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(view.getContext(), "Cannot create Post: " + res.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}