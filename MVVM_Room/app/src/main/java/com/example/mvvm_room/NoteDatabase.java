package com.example.mvvm_room;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.PrimaryKey;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;


/**
 * Database annotation provide all the details about database means which
 * entity your database use and which version like sqlite
 *
 * Here we are referring the Note.class where we define all the entity
 * used for our Room Database
 */
@Database(entities = {Note.class},version = 1)
public abstract class NoteDatabase extends RoomDatabase {

    /** Here we are creating the singleton instances which means that
     * we cannot create multiple instance for this same */
    private static NoteDatabase instance;

    /**
     *Here are setting this method as abstract because all the implementation
     * are taking place by Room
     */
    public abstract NoteDao noteDao();

    /**
     *Here we are defining the getInstance() method as synchronized because
     * we wants that only single thread access this method it hide bottel nake
     * condition
     */
    public static synchronized NoteDatabase getInstance(Context context){
        //Here cannot use direct instance of the RoomDatabase because it is
        //a interface so used Room.databaseBuilder()
        if(instance==null)
        {
            instance= Room.databaseBuilder(
                    context.getApplicationContext(),
                    NoteDatabase.class,
                    "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback) //Here we are adding the .addCallback for performing database changes
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback=new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateAsyncTask(instance).execute();
        }
    };

    /**
     * Here we want to perform the database insert activity so it's better to perform
     * in the background rather then main activity because it slow down the app
     * and also create memory leak. So for avoiding this memory leak we uses the static
     * class of our async class because it does not create even the object reference.
     */
    private static class PopulateAsyncTask extends AsyncTask<Void,Void,Void>{

        private NoteDao noteDao;
        private PopulateAsyncTask(NoteDatabase db){
            noteDao=db.noteDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.insert(new Note("Title 1","Description 1",1));
            noteDao.insert(new Note("Title 2","Description 2",2));
            noteDao.insert(new Note("Title 3","Description 3",3));
            return null;
        }
    }
}
