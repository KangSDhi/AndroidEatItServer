package com.example.androideatitserver.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.androideatitserver.Common.Common;
import com.example.androideatitserver.FoodListManagement;
import com.example.androideatitserver.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

public class BottomNavigationDrawerFragment extends BottomSheetDialogFragment {

    private TextView TextNama, TextTelepon;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bottomsheet, container, false);

        TextNama = (TextView)view.findViewById(R.id.textName);
        TextNama.setText(Common.currentModelPengguna.getNama_Depan());

        TextTelepon = (TextView)view.findViewById(R.id.textPhone);
        TextTelepon.setText(Common.currentModelPengguna.getTelepon());

        NavigationView nav_view = view.findViewById(R.id.navigation_view);

        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_menu_management:
                        startActivity(new Intent(getContext(), FoodListManagement.class));
                        return true;
                    case R.id.nav_order_management:
                        Toast.makeText(getContext(), "Order Di Klik", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_profile:
                        Toast.makeText(getContext(), "Profile Di Klik", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_exit:
                        Toast.makeText(getContext(), "Exit Di Klik", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return true;
            }
        });

        return view;
    }
}
