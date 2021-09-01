package com.rahul.child;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.rahul.child.Model.StaticRvModel;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {
    // views
    private StaticRvAdapter staticRvAdapter;
    private TextView txt_username;
    private RecyclerView menu;

    // static
    private static final String MY_PREFERENCE = "childPref";
    private static final String MY_USERNAME = "UserName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // hooks
        menu = findViewById(R.id.rv_1);
        ArrayList<StaticRvModel> item = new ArrayList<>();
        txt_username = findViewById(R.id.txt_username);
        SharedPreferences preferences = getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);

        txt_username.setText(preferences.getString(MY_USERNAME, "User"));
        item.add(new StaticRvModel(R.drawable.speak, "Chat"));
        staticRvAdapter = new StaticRvAdapter(this, item);

        menu.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        menu.setAdapter(staticRvAdapter);

        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
    }

}