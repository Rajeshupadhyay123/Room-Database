package com.example.mvvm_room;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao  {

    @Insert
    void insert(Note note);

    @Update
    void update(Note note);

    @Delete
    void delete(Note note);

    /**
     * The Room object does not give you all your convantional annotation
     * so if you want to execute your own query then you can use Query() annotation
     * and pass the query like sqlite but here it check all the parameter at
     * compile time rather then runtime. It means that if you use any variable
     * that does not exist in your Dao it give you compile time error
     *
     * Now below the Query() annotation you have to provide the method which return
     * you the result of your query method
     */
    @Query("DELETE FROM note_table")
    void deleteAllNotes();

    /**
     * Here the use of LiveData means that if you have done any changes to
     * your database it immediately notify you
     * @return
     */
    @Query("SELECT *FROM note_table ORDER BY priority DESC")
    LiveData<List<Note>> getAllNotes();
}
