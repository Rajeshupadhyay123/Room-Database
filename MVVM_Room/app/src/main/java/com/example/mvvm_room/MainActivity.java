package com.example.mvvm_room;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;
    public static final int ADD_NOTE_REQUEST=1;
    public static final int Edit_NOTE_REQUEST=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton buttonAddNote=findViewById(R.id.button_add_note);
        buttonAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, AddEditNoteActivity.class);
                /**
                 * Here are calling the startActivityForResult() method this open the AddNoteActivity() and expect the
                 * some result from that activity and set this data as in constant ADD_NOTE_REQUEST
                 * and the below code we use onActivityResult() where we fatch the data based on this request constant ADD_NOTE_REQUEST
                 */
                startActivityForResult(intent,ADD_NOTE_REQUEST);
            }
        });

        RecyclerView recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter adapter=new NoteAdapter();
        recyclerView.setAdapter(adapter);
        /**
         * Don't change this ViewModelProvider code it is very restricted code
         * new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(NoteViewModel.class);
         */
        noteViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(this.getApplication())).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //update Recyclerview
                adapter.setNotes(notes);
            }
        });

        /**
         * Here are going to implement the data delete function
         * Here we will also add the swip delete functionality
         */
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));

                Toast.makeText(MainActivity.this,"Note deleted",Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);

        /**
         * This code for update
         */
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent=new Intent(MainActivity.this, AddEditNoteActivity.class);//Here are changes AddNoteActivity to AddEditNoteActivity
                intent.putExtra(AddEditNoteActivity.EXTRA_ID,note.getId());
                intent.putExtra(AddEditNoteActivity.EXTRA_TITLE,note.getTitle());
                intent.putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION,note.getDescription());
                intent.putExtra(AddEditNoteActivity.EXTRA_PRIORITY,note.getPriority());
                startActivityForResult(intent,Edit_NOTE_REQUEST);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Here we used the if condition and check the request code ADD_NOTE_REQUEST and also check this
         * request code contains the RESULT_OK or not. if both condition get satisfied then
         * goes to fatch the data which are present in the ADD_NOTE_REQUEST
         */
        if(requestCode==ADD_NOTE_REQUEST && resultCode==RESULT_OK){
            assert data != null;//It avoid the data to be null
            String title=data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String desciption=data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority=data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY,1);

            /**
             * And Here we are performing the getter method on this values
             * and with the help of ViewModel object we called the insert() method and
             * pass the note object. where all the database operation are take place
             * by Room database
             */
            Note note=new Note(title,desciption,priority);
            noteViewModel.insert(note);

            Toast.makeText(this,"Note Saved",Toast.LENGTH_SHORT).show();
        }else if(requestCode==Edit_NOTE_REQUEST && resultCode==RESULT_OK){
            assert data != null;//It avoid the data to be null
            /**
             * Here we are using the getIntExtra() method it check the EXTRA_ID key that it
             * contain the data or not. if it contains the data then it return some int value
             * otherwise it return the -1 value which is a default value in this case
             */
            int id=data.getIntExtra(AddEditNoteActivity.EXTRA_ID,-1);
            if(id==-1){
                Toast.makeText(this,"Note cannot be updated",Toast.LENGTH_SHORT).show();
                return;
            }

            String title=data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String desciption=data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int priority=data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY,1);

            Note note=new Note(title,desciption,priority);
            /**
             * Here we are using the setId() method which will always generated by-default by Room database
             * but here we are manually use because here we can select any data on the recycler view and
             * that data id will be get here are with help of this id we will perform the database operation
             */
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(this,"Note updated",Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(this,"Note not Saved",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this,"All notes deleted",Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}