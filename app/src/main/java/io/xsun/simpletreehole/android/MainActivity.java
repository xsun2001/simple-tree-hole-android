package io.xsun.simpletreehole.android;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.xsun.simpletreehole.android.databinding.ActivityMainBinding;
import io.xsun.simpletreehole.android.service.UserService;
import io.xsun.simpletreehole.android.ui.LoginFragment;
import io.xsun.simpletreehole.android.ui.PostListFragment;
import io.xsun.simpletreehole.android.ui.UserProfileFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private BottomNavigationView navView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        navView = binding.navView;
        navView.setOnItemSelectedListener(this::onNavItemSelected);
    }

    private void replaceFragment(Class<? extends Fragment> fragmentClass) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragmentClass, null)
                .addToBackStack(null)
                .commit();
    }

    private boolean onNavItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.posts) {
            replaceFragment(PostListFragment.class);
            return true;
        } else if (item.getItemId() == R.id.user) {
            replaceFragment(
                    UserService.getInstance().getLoggedUser(this) == null ?
                            LoginFragment.class : UserProfileFragment.class);
            return true;
        }
        return false;
    }

}