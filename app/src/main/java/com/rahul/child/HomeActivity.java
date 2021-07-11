package com.rahul.child;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {
    // views
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // hooks
        bottomNav = findViewById(R.id.bottonNav);

        bottomNav.setOnNavigationItemSelectedListener(bottomNavMethod);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
    }

    // click event listener
    private BottomNavigationView.OnNavigationItemSelectedListener bottomNavMethod =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment fragment =null;
                    switch (item.getItemId()) {
                        case R.id.home:
                            fragment = new HomeFragment();
                            break;
                    }
                    getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment).commit();
                    return true;
                }
            };
}