package io.xsun.simpletreehole.android.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import io.xsun.simpletreehole.android.R;

public class CreatePostFragment extends Fragment {
    public CreatePostFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_create_post, container, false);
    }
}