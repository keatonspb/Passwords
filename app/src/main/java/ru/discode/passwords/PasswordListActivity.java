package ru.discode.passwords;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import java.security.GeneralSecurityException;

import ru.discode.passwords.adapter.PasswordListAdapter;
import ru.discode.passwords.db.PasswordReaderDbHelper;
import ru.discode.passwords.entry.PasswordEntry;
import ru.discode.passwords.helper.AESCrypt;
import ru.discode.passwords.util.SLog;

public class PasswordListActivity extends AppCompatActivity implements PasswordListAdapter.onTouchListener {
    public static String CODE_EXTRA = "CODE";
    private String code;
    private RecyclerView listView;
    private PasswordListAdapter adapter;
    private ProgressBar progressBar;
    private ScrollView scrollingView;
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
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                showAddDialog();
            }
        });

        listView = (RecyclerView) findViewById(R.id.passwords_list);
        listView.setLayoutManager(new LinearLayoutManager(this));
        listView.setHasFixedSize(true);
        adapter = new PasswordListAdapter(this, code);
        ItemTouchHelper.Callback callback = new PasswordTouchHelper(adapter);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(listView);

        scrollingView = (ScrollView) findViewById(R.id.list_content);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        getList();
    }

    private void getList() {
        PasswordReaderDbHelper passwordReaderDbHelper = new PasswordReaderDbHelper(this);
        SQLiteDatabase db = passwordReaderDbHelper.getReadableDatabase();
        String[] projection = {
                PasswordEntry._ID,
                PasswordEntry.COLUMN_NAME_TITLE,
                PasswordEntry.COLUMN_NAME_CONTENT
        };
        Cursor c = db.query(PasswordEntry.TABLE_NAME, projection, null, null, null, null, null);

        adapter.swapCursor(c);
        adapter.setOnTouchListener(this);
        listView.setAdapter(adapter);
        showProgress(false);
    }

    private void showAddDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_password, null);
        final EditText nameEditText = (EditText) view.findViewById(R.id.title);
        final EditText contentEditText = (EditText) view.findViewById(R.id.content);
        builder.setView(view);
        builder.setPositiveButton(R.string.promt_add_password, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addPassword(nameEditText.getText().toString(), contentEditText.getText().toString());
            }
        });
        builder.setNeutralButton(R.string.promt_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public void showEditDialog(PasswordEntry passwordEntry) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_password, null);
        final EditText nameEditText = (EditText) view.findViewById(R.id.title);
        nameEditText.setText(passwordEntry.title);
        final EditText contentEditText = (EditText) view.findViewById(R.id.content);
        contentEditText.setText(passwordEntry.content);
        final Long id = passwordEntry.id;
        builder.setView(view);
        builder.setPositiveButton(R.string.promt_add_password, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                savePassword(id, nameEditText.getText().toString(), contentEditText.getText().toString());
            }
        });
        builder.setNeutralButton(R.string.promt_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }

    public void showDoDeleteDialog(final Long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.realy_delete);

        builder.setPositiveButton(R.string.promt_delete_password, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletePassword(id);
            }
        });
        builder.setNeutralButton(R.string.promt_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                getList();
            }
        });
        builder.create().show();
    }

    private void addPassword(String name, String content) {
        SLog.d("addPassword", "name"+name);
        SLog.d("addPassword", "content"+content);
        showProgress(true);
        try {
            name = AESCrypt.encrypt(code, name);
            content = AESCrypt.encrypt(code, content);
        } catch (GeneralSecurityException e) {
            SLog.d("PS", e.getMessage());
        }
        AddPassword task = new AddPassword();
        task.execute(name, content);
    }

    private void savePassword(Long id, String name, String content) {
        showProgress(true);

        try {
            name = AESCrypt.encrypt(code, name);
            content = AESCrypt.encrypt(code, content);
        } catch (GeneralSecurityException e) {
            SLog.d("PS", e.getMessage());
        }

        SavePassword task = new SavePassword();
        task.execute(id.toString(), name, content);


    }

    private void deletePassword(Long id) {
        showProgress(true);
        DeletePassword task = new DeletePassword();
        task.execute(id);


    }

    @Override
    public void onClick(Long id) {
        new GetPassword().execute(id);
    }

    @Override
    public void onSwipe(Long id) {
        showDoDeleteDialog(id);
    }

    private class AddPassword extends AsyncTask<String, Void, Long> {
        @Override
        protected Long doInBackground(String... parameter) {
            PasswordReaderDbHelper passwordReaderDbHelper = new PasswordReaderDbHelper(PasswordListActivity.this);
            SQLiteDatabase db = passwordReaderDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PasswordEntry.COLUMN_NAME_TITLE, (java.lang.String) parameter[0]);
            values.put(PasswordEntry.COLUMN_NAME_CONTENT, (java.lang.String) parameter[1]);
            long newRowId;
            newRowId = db.insert(PasswordEntry.TABLE_NAME, null, values);
            return newRowId;
        }
        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);
            makeMessage("Пароль сохранен");
            getList();
        }
    }

    private class SavePassword extends AsyncTask<String, Void, Integer> {
        @Override
        protected Integer doInBackground(String... parameter) {
            PasswordReaderDbHelper passwordReaderDbHelper = new PasswordReaderDbHelper(PasswordListActivity.this);
            SQLiteDatabase db = passwordReaderDbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PasswordEntry.COLUMN_NAME_TITLE, parameter[1]);
            values.put(PasswordEntry.COLUMN_NAME_CONTENT, parameter[2]);
            String selection = PasswordEntry._ID + " = ?";
            String[] selectionArgs = { parameter[0] };
            int count = db.update(
                    PasswordEntry.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
            return count;
        }
        @Override
        protected void onPostExecute(Integer count) {
            super.onPostExecute(count);
            makeMessage("Пароль сохранен");
            getList();
        }
    }

    private class DeletePassword extends AsyncTask<Long, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Long... parameter) {
            PasswordReaderDbHelper passwordReaderDbHelper = new PasswordReaderDbHelper(PasswordListActivity.this);
            SQLiteDatabase db = passwordReaderDbHelper.getWritableDatabase();

            String selection = PasswordEntry._ID + " = ?";
            String[] selectionArgs = { String.valueOf(parameter[0]) };
            int count = db.delete(
                    PasswordEntry.TABLE_NAME,
                    selection,
                    selectionArgs);
            return count > 0;
        }
        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            if(success) {
                makeMessage("Пароль удален");
            } else {
                makeMessage(R.string.cant_delete);
            }
            showProgress(true);
            getList();
        }
    }

    private class GetPassword extends AsyncTask<Long, Void, PasswordEntry> {

        @Override
        protected PasswordEntry doInBackground(Long... parameter) {
            PasswordReaderDbHelper passwordReaderDbHelper = new PasswordReaderDbHelper(PasswordListActivity.this);
            SQLiteDatabase db = passwordReaderDbHelper.getReadableDatabase();

            String[] projection = {
                    PasswordEntry._ID,
                    PasswordEntry.COLUMN_NAME_TITLE,
                    PasswordEntry.COLUMN_NAME_CONTENT,
            };

            String selection = PasswordEntry._ID + " = ?";
            String[] selectionArgs = { String.valueOf(parameter[0]) };

            Cursor c = db.query(PasswordEntry.TABLE_NAME,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    null
                    );
            PasswordEntry pe = new PasswordEntry();
            if(c.getCount() == 1) {
                c.moveToFirst();
                try {
                    pe.id = c.getLong(c.getColumnIndex(PasswordEntry._ID));
                    pe.title = AESCrypt.decrypt(code, c.getString(c.getColumnIndex(PasswordEntry.COLUMN_NAME_TITLE)));
                    pe.content = AESCrypt.decrypt(code, c.getString(c.getColumnIndex(PasswordEntry.COLUMN_NAME_CONTENT)));
                } catch (GeneralSecurityException e) {

                    return null;
                }

            }
            return pe;
        }

        @Override
        protected void onPostExecute(PasswordEntry entry) {
            super.onPostExecute(entry);
            if(entry == null) {
                makeMessage(getResources().getString(R.string.decrypt_error_string));
            } else {
                showEditDialog(entry);
            }


        }
    }
    protected void makeMessage(String text) {
        Snackbar.make(scrollingView, text, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
    }
    protected void makeMessage(int textId) {
        Snackbar.make(scrollingView, textId, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
    /**
     * Показывае загрузчик
     */
    public void showProgress(boolean show) {
        if(show) {
            progressBar.setVisibility(View.VISIBLE);
            scrollingView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            scrollingView.setVisibility(View.VISIBLE);
        }

    }

    public class PasswordTouchHelper extends ItemTouchHelper.SimpleCallback {
        private PasswordListAdapter passwordListAdapter;
        Drawable background;
        public PasswordTouchHelper(PasswordListAdapter passwordListAdapter){
            super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT);
            this.passwordListAdapter = passwordListAdapter;
            background = new ColorDrawable(Color.RED);
        }


        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            passwordListAdapter.remove(viewHolder.getAdapterPosition());

        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            Bitmap icon;
            Paint p = new Paint();
            if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){
                View itemView = viewHolder.itemView;
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                float width = height / 3;
                if(dX > 0){
//                    p.setColor(Color.parseColor("#388E3C"));
//                    RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
//                    c.drawRect(background,p);
//                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_edit_white);
//                    RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) itemView.getTop() + width,(float) itemView.getLeft()+ 2*width,(float)itemView.getBottom() - width);
//                    c.drawBitmap(icon,null,icon_dest,p);
                } else {
                    p.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background,p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                    c.drawBitmap(icon,null,icon_dest,p);
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }


}
