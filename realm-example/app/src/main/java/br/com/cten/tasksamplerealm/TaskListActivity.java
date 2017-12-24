package br.com.cten.tasksamplerealm;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;

import java.util.UUID;

import br.com.cten.tasksamplerealm.realm.Task;
import io.realm.Realm;
import io.realm.RealmResults;

public class TaskListActivity extends AppCompatActivity {

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        // RealmResults are "live" views, that are automatically kept up to date, even when changes happen
        // on a background thread. The RealmBaseAdapter will automatically keep track of changes and will
        // automatically refresh when a change is detected.
        RealmResults<Task> tasks = getTasks();
        final TaskAdapter adapter = new TaskAdapter(this, tasks);

        ListView listView = findViewById(R.id.task_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((adapterView, view, i, l) -> {
                final Task task = (Task) adapterView.getAdapter().getItem(i);
                final EditText taskEditText = new EditText(TaskListActivity.this);
                taskEditText.setText(task.getName());
                AlertDialog dialog = new AlertDialog.Builder(TaskListActivity.this)
                        .setTitle("Edit Task")
                        .setView(taskEditText)
                        .setPositiveButton("Save", (dialogInterface, j) -> {
                                changeTaskName(task.getId(), String.valueOf(taskEditText.getText()));
                        })
                        .setNegativeButton("Delete", (dialogInterface, k) -> {
                            deleteTask(task.getId());
                        })
                        .create();
                dialog.show();
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
                final EditText taskEditText = new EditText(TaskListActivity.this);
                AlertDialog dialog = new AlertDialog.Builder(TaskListActivity.this)
                        .setTitle("Add Task")
                        .setView(taskEditText)
                        .setPositiveButton("Add", (dialogInterface, i) -> {
                                createTask(String.valueOf(taskEditText.getText()));
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
        });
    }

    private void createTask(String taskName) {
        realm.executeTransactionAsync(r -> {
                r.createObject(Task.class, UUID.randomUUID().toString())
                        .setName(taskName);
        });
    }

    private RealmResults<Task> getTasks() {
        return realm.where(Task.class).findAll();
    }

    public void changeTaskDone(final String taskId) {
        realm.executeTransactionAsync(r -> {
                Task task = r.where(Task.class).equalTo("id", taskId).findFirst();
                task.setDone(!task.isDone());
        });
    }

    private void changeTaskName(final String taskId, final String name) {
        realm.executeTransactionAsync(r -> {
                Task task = r.where(Task.class).equalTo("id", taskId).findFirst();
                task.setName(name);
        });
    }

    private void deleteTask(final String taskId) {
        realm.executeTransactionAsync(r -> {
                r.where(Task.class).equalTo("id", taskId)
                        .findFirst()
                        .deleteFromRealm();
        });
    }

    private void deleteAllDone() {
        realm.executeTransactionAsync(r -> {
                realm.where(Task.class).equalTo("done", true)
                        .findAll()
                        .deleteAllFromRealm();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_delete) {
            deleteAllDone();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
