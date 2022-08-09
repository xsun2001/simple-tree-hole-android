package io.xsun.simpletreehole.android;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.xsun.simpletreehole.android.databinding.ActivityMainBinding;
import io.xsun.simpletreehole.android.ui.post.PostDetailFragment;
import io.xsun.simpletreehole.android.ui.post.PostListFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PostListFragment postList = new PostListFragment();
    private PostDetailFragment postDetail;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        navView = binding.navView;
        navView.setOnItemSelectedListener(this::onNavItemSelected);
    }

    private boolean onNavItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.posts) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, postList).commit();
            return true;
        }
        return false;
    }

}