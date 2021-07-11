package com.rahul.child;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.rahul.child.Model.Child;
import com.rahul.child.Remote.IAPI;
import com.rahul.child.Remote.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
    // static variables
    private static final String TAG = "LoginActivity";
    private static final String MY_PREFERENCE = "childPref";
    private static final String MY_ID = "Id";
    private static final String MY_USERNAME = "UserName";
    private static final String MY_EMAIL = "Email";
    private static final String MY_PHONE = "PhoneNumber";
    private static final String MY_LAT = "Lat";
    private static final String MY_LNG = "Lng";

    // views
    private Button btn_login;
    private EditText edt_username, edt_password;

    // db variables
    private IAPI api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // hooks
        api = RetrofitClient.getInstance().create(IAPI.class);
        edt_username = findViewById(R.id.edt_username);
        edt_password = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_login);

        // event listener
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String username = edt_username.getText().toString();
        String password = edt_password.getText().toString();
        Child child = new Child(username, password, "");

        if (isValid(username, password)) {
            compositeDisposable.add(api.login(child)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            SharedPreferences preferences = getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();

                            Gson gson = new Gson();
                            Child me = new Child();
                            me = gson.fromJson(s, Child.class);

                            editor.putInt(MY_ID, me.getId());
                            editor.putString(MY_USERNAME, me.getUserName());
                            editor.putString(MY_EMAIL, me.getEmail());
                            editor.putString(MY_PHONE, me.getPhoneNumber());
                            editor.putFloat(MY_LAT, (float) me.getLat());
                            editor.putFloat(MY_LNG, (float) me.getLng());
                            editor.apply();

                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));

                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(LoginActivity.this, "Check your credentials", Toast.LENGTH_SHORT).show();
                        }
                    }));
        }
    }

    // validate form
    private boolean isValid(String username, String password) {

        if (username.isEmpty() && password.isEmpty()) {
            edt_username.setError("Enter username");
            edt_password.setError("Enter password");
            return false;
        }else if (username.isEmpty()) {
            edt_username.setError("Enter username");
            return false;
        }else if (password.isEmpty()) {
            edt_password.setError("Enter password");
            return false;
        }

        return true;
    }
}