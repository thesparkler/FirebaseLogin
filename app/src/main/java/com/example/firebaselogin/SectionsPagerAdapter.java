package com.example.firebaselogin;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.firebaselogin.ChatFragment;
import com.example.firebaselogin.ContactsFragment;
import com.example.firebaselogin.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class SectionsPagerAdapter extends FragmentPagerAdapter {

   // @StringRes
   // private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;

    public SectionsPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position){
            case 0:
                ChatFragment chatFragment = new ChatFragment();
                return  chatFragment;
            case 1:
                ContactsFragment contactsFragment = new ContactsFragment();
                return contactsFragment;
        }
        return null;


    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "CONTACTS";
            case 1:
                return "CHATS";
        }

        return null;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }
}