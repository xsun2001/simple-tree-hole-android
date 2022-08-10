package io.xsun.simpletreehole.android.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import io.xsun.simpletreehole.android.R;

public final class Utils {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT);

    public static void replaceFragment(Fragment fragment, Class<? extends Fragment> fragmentClass) {
        fragment.getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragmentClass, null)
                .addToBackStack(null)
                .commit();
    }

    public static void replaceFragment(Fragment fragment, Class<? extends Fragment> fragmentClass, Bundle data) {
        fragment.getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragmentClass, data)
                .addToBackStack(null)
                .commit();
    }

}
