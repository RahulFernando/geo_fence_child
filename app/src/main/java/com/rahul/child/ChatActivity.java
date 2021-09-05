package com.rahul.child;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import com.rahul.child.Model.Parent;
import com.rahul.child.Remote.IAPI;
import com.rahul.child.Remote.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class ChatActivity extends AppCompatActivity {
    // views
    private RecyclerView recyclerView;

    // network
    private IAPI api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    // variables
    private SharedPreferences preferences;
    private List<Parent> parentList;
    private ChatAdapter adapter;
    private LoadinDialog dialog;

    // static
    private static final String MY_PREFERENCE = "childPref";
    private static final String MY_PARENT = "ParentId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // obj initialization
        api = RetrofitClient.getInstance().create(IAPI.class);
        preferences = getSharedPreferences(MY_PREFERENCE, MODE_PRIVATE);
        dialog = new LoadinDialog(this);

        // hooks
        recyclerView = findViewById(R.id.rv_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dialog.loadingAlertDialog();
        parentDetails();
    }

    // retrieve data
    private void parentDetails() {
        parentList = new ArrayList<>();
        int id = preferences.getInt(MY_PARENT, -1);
        compositeDisposable.add(api.getParent(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Consumer<Parent>() {
                @Override
                public void accept(Parent parent) throws Exception {
                    dialog.dismiss();
                    parentList.add(parent);
                    adapter = new ChatAdapter(ChatActivity.this, parentList);
                    recyclerView.setAdapter(adapter);
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                    dialog.dismiss();
                    Toast.makeText(ChatActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }));
    }
}