package com.rahul.child;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.rahul.child.Model.Message;
import com.rahul.child.Remote.IAPI;
import com.rahul.child.Remote.RetrofitClient;

import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    // static
    private static final String MY_PREFERENCE = "childPref";
    private static final String MY_SERIAL = "Serial";
    private static final String PARENT_SERIAL = "ParentSerial";

    // variables
    private MessageAdapter messageAdapter;
    private SharedPreferences preferences;
    private LoadinDialog dialog;
    private String childSerial = "";
    private String parentSerial = "";
    private Timer timer;

    // views
    private RecyclerView recyclerView;
    private ImageButton send;
    private EditText message;

    // network
    private IAPI api;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        parentSerial = getIntent().getStringExtra(PARENT_SERIAL);

        timer = new Timer();
        dialog = new LoadinDialog(this);
        api = RetrofitClient.getInstance().create(IAPI.class);
        preferences = getSharedPreferences(MY_PREFERENCE, MODE_PRIVATE);
        recyclerView = findViewById(R.id.rv_message);
        message = findViewById(R.id.edt_message);
        send = findViewById(R.id.btn_send);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        childSerial = preferences.getString(MY_SERIAL, "");

        dialog.loadingAlertDialog();
        readMessages();

        // send button onClick
        send.setOnClickListener(this);

        // retrieve messages every 10 seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                readMessages();
            }
        }, 0, 1000);
    }

    @Override
    public void onClick(View view) {
        sendMessage(message.getText().toString());
    }

    private void readMessages() {
        compositeDisposable.add(api.getMessages(parentSerial, childSerial)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<Message>>() {
                    @Override
                    public void accept(List<Message> messages) throws Exception {
                        dialog.dismiss();
                        messageAdapter = new MessageAdapter(messages,MessageActivity.this);
                        recyclerView.setAdapter(messageAdapter);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        dialog.dismiss();
                        Toast.makeText(MessageActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void sendMessage(String txt_message) {
        if (!txt_message.isEmpty()) {
            System.out.println(txt_message);
            String timestamp = Calendar.getInstance().getTime().toString();
            Message msgObj = new Message(txt_message, timestamp, childSerial, parentSerial);

            System.out.println(parentSerial);
            System.out.println(childSerial);

            compositeDisposable.add(api.sendMessages(msgObj)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(String s) throws Exception {
                            Gson gson = new Gson();
                            Toast.makeText(MessageActivity.this, gson.fromJson(s, String.class), Toast.LENGTH_SHORT).show();
                            message.setText("");
                            readMessages();
                        }
                    }, new Consumer<Throwable>() {
                        @Override
                        public void accept(Throwable throwable) throws Exception {
                            Toast.makeText(MessageActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }));
        }
    }
}