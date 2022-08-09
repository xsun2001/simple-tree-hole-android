package io.xsun.simpletreehole.android.ui;

import android.view.View;

import androidx.fragment.app.Fragment;

import io.xsun.simpletreehole.android.R;

public final class Utils {

    public static void replaceFragment(Fragment fragment, Class<? extends Fragment> fragmentClass) {
        fragment.getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragmentClass, null)
                .addToBackStack(null)
                .commit();
    }

}
