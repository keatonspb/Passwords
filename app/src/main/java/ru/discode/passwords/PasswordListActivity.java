package ru.discode.passwords;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import ru.discode.passwords.adapter.PasswordListAdapter;
import ru.discode.passwords.db.PasswordReaderDbHelper;
import ru.discode.passwords.entry.PasswordEntry;

public class PasswordListActivity extends AppCompatActivity {
    public static String CODE_EXTRA = "CODE";
    private String code;
    private RecyclerView listView;
    private PasswordListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        code = getIntent().getStringExtra(CODE_EXTRA);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_password);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listView = (RecyclerView) findViewById(R.id.passwords_list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        getList();
    }

    private void getList() {
        PasswordReaderDbHelper passwordReaderDbHelper = new PasswordReaderDbHelper(this);
        SQLiteDatabase db = passwordReaderDbHelper.getReadableDatabase();
        String[] projection = {
                PasswordEntry._ID,
                PasswordEntry.COLUMN_NAME_TITLE,
                PasswordEntry.COLUMN_NAME_PASSWORD
        };
        Cursor c = db.query(PasswordEntry.TABLE_NAME, projection, null, null, null, null, null);
        adapter = new PasswordListAdapter(this);
        adapter.swapCursor(c);
        listView.setAdapter(adapter);
    }


}
