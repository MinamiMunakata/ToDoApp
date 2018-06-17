package com.personal_project.minami.todoapp;

import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SwipeMenuListView.OnMenuItemClickListener {

    private SwipeMenuListView listView;
    private ArrayList<String> mToDoItems;
    private ArrayAdapter adapter;
    private SQLiteDatabase database;
    private String TAG = "main activity";
    private final String FILE = "todo.db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogToAddTodo("What Do You Need to Do?", "CREATE");
            }
        });

        listView = findViewById(R.id.listView);
        listView.setMenuCreator(getSwipeMenuCreator());
        mToDoItems = new ArrayList<>();
        readDataBase();

        adapter = new ArrayAdapter(
                this,

                android.R.layout.simple_list_item_1,
                mToDoItems
        );
        listView.setAdapter(adapter);
        listView.setOnMenuItemClickListener(this);
        listView.smoothCloseMenu();
    }

    private void insertDataBase(String todo) {
        database = openOrCreateDatabase(FILE, MODE_PRIVATE, null);
        String sql = "INSERT INTO todo_data VALUES('" + todo + "')";
        database.execSQL(sql);
        database.close();
        Log.i(TAG, "insertDataBase: -----------> " + todo);
    }

    private void readDataBase() {
        database = openOrCreateDatabase(FILE, MODE_PRIVATE, null);
        String sql = "CREATE TABLE IF NOT EXISTS todo_data(todo TEXT);";
        database.execSQL(sql);
        Cursor query = database.rawQuery(
                "SELECT * FROM todo_data",
                null
        );
        if (query.moveToFirst()) {
            do {
                String todoItem = query.getString(query.getColumnIndex("todo"));
                mToDoItems.add(todoItem);
                Log.i(TAG, "readDataBase: ================= " + todoItem);
            } while (query.moveToNext());
        }
        query.close();
        database.close();
    }

    private void deleteData(String todo) {
        database = openOrCreateDatabase(FILE, MODE_PRIVATE, null);
        database.delete("todo_data", "todo = '" + todo + "'", null);
        database.close();
        Log.i(TAG, "deleteData: " + "Deleeeeeete???");
        Log.i(TAG, "deleteData: " + todo);
    }

    private void editData(String todo, String newToDo) {
        database = openOrCreateDatabase(FILE, MODE_PRIVATE, null);
        String sql = "UPDATE todo_data SET todo = '" + newToDo + "' WHERE todo = '" + todo + "';";
        database.execSQL(sql);
        database.close();
    }

    private SwipeMenuCreator getSwipeMenuCreator() {
        return new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "open" item
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                openItem.setBackground(new ColorDrawable(Color.rgb(0xC9, 0xC9,
                        0xCE)));
                // set item width
                openItem.setWidth(200);
                // set item title
                openItem.setTitle("Edit");
                // set item title fontsize
                openItem.setTitleSize(18);
                // set item title font color
                openItem.setTitleColor(Color.WHITE);
                // add to menu
                menu.addMenuItem(openItem);

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(200);
                // set a icon
                deleteItem.setIcon(android.R.drawable.ic_menu_delete);
                // add to menu
                menu.addMenuItem(deleteItem);


            }
        };
    }

    private void showDialogToAddTodo(String title, String button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        final EditText et_input = new EditText(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        layoutParams.setMargins(10, 0, 10, 0);
        et_input.setLayoutParams(layoutParams);
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newTodo = et_input.getText().toString();
                insertDataBase(newTodo);
                mToDoItems.add(newTodo);
                adapter.notifyDataSetChanged();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setView(et_input);
        dialog.show();
    }

    private void showDialogToAddTodo(String title, String button, final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        final EditText et_input = new EditText(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        layoutParams.setMargins(10, 0, 10, 0);
        et_input.setLayoutParams(layoutParams);
        et_input.setText(mToDoItems.get(position));
        builder.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String newTodo = et_input.getText().toString();
                editData(mToDoItems.get(position), newTodo);
                mToDoItems.set(position, newTodo);
                adapter.notifyDataSetChanged();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setView(et_input);
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
        switch (index) {
            case 0:
                // edit
                showDialogToAddTodo("Edit Your TODO", "EDIT", position);
                break;
            case 1:
                // delete
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Do You Want to Delete?");
                builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String todo = mToDoItems.get(position);
                        deleteData(todo);
                        mToDoItems.remove(position);
                        adapter.notifyDataSetChanged();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
        }
        // false : close the menu; true : not close the menu
        return false;
    }
}
