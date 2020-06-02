/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright (c) Meimo 2020. Let's Get AWESOME!                                                   ~
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_Order.Fragment_User_Home_Order;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_Kidung.Fragment_User_Home_Kidung;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_News.Fragment_User_Home_News;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_Tatacara.Fragment_User_Home_Tatacara;
import com.meimodev.sitouhandler.Dashboard.NavFragment.NavFragment_User.Fragment_User_Warta.Fragment_User_Home_Warta;
import com.meimodev.sitouhandler.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Fragment_User_Home extends Fragment {

    private static final String TAG = "Fragment_User_Home";
    private View rootView;
    private Context context;


    @BindView(R.id.layoutContentFragment)
    FrameLayout layoutFragmentContainer;
    private BottomNavigationView bottomNavigationView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.nav_fragment_user_home, container, false);
        context = rootView.getContext();
        ButterKnife.bind(this, rootView);

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavBar);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;

            switch (item.getItemId()) {
                case R.id.bottomNavBar_kidung:
                    fragment = new Fragment_User_Home_Kidung();
                    break;
                case R.id.bottomNavBar_news:
                    fragment = new Fragment_User_Home_News();
                    break;
                case R.id.bottomNavBar_tatacara:
                    fragment = new Fragment_User_Home_Tatacara();
                    break;
                case R.id.bottomNavBar_warta:
                    fragment = new Fragment_User_Home_Warta();
                    break;
                case R.id.bottomNavBar_activity:
                    fragment = new Fragment_User_Home_Order();
                    break;
            }

            if (fragment != null) {
                getFragmentManager()
                        .beginTransaction().replace(R.id.layoutContentFragment, fragment)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(null)
                        .commit();
            }
            else {
                Log.e(TAG, "setupBottomNavBar: fragment is null");
            }

            return true;
        });
        bottomNavigationView.setSelectedItemId(R.id.bottomNavBar_news);
        bottomNavigationView.setVisibility(View.VISIBLE);

        rootView.setPaddingRelative(0,0,0, 140);
        return rootView;
    }


    @Override
    public void onPause() {
        super.onPause();
        if (bottomNavigationView.getVisibility() == View.VISIBLE) {
            bottomNavigationView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bottomNavigationView != null && bottomNavigationView.getVisibility() != View.VISIBLE) {
            bottomNavigationView.setVisibility(View.VISIBLE);
        }
    }
}
