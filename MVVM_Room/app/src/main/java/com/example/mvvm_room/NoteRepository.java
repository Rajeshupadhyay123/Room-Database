package com.example.mvvm_room;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class NoteRepository {

    private NoteDao noteDao;
    private LiveData<List<Note>> allNotes;

    /**
     * @param application is the subclass of context so insted of using the
     *                    context we are using the application
     */
    public NoteRepository(Application application){
        NoteDatabase database=NoteDatabase.getInstance(application);
        //Here we are getting the method which are abstract in the NoteDatabase
        //Actually it is a instance of the NoteDao interface where we are defining the
        //getAllNotes() method
        noteDao=database.noteDao();
        allNotes=noteDao.getAllNotes();
    }

    public void insert(Note note){
        new InsertNoteAsyncTask(noteDao).execute(note);
    }

    public void update(Note note){
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }
    public void delete(Note note){
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }
    public void deleteAllNotes(){
        new DeleteAllNoteAsyncTask(noteDao).execute();
    }
    public LiveData<List<Note>> getAllNotes(){
        return allNotes;
    }

    /**
     * Room database allow the database activity directly it perform as AsyncTask in the
     * background
     */
    private static class InsertNoteAsyncTask extends AsyncTask<Note,Void,Void>{
        private NoteDao noteDao;
        private InsertNoteAsyncTask(NoteDao noteDao){
            this.noteDao=noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.insert(notes[0]);
            return null;
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note,Void,Void>{
        private NoteDao noteDao;
        private UpdateNoteAsyncTask(NoteDao noteDao){
            this.noteDao=noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }
    private static class DeleteNoteAsyncTask extends AsyncTask<Note,Void,Void>{
        private NoteDao noteDao;
        private DeleteNoteAsyncTask(NoteDao noteDao){
            this.noteDao=noteDao;
        }

        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    private static class DeleteAllNoteAsyncTask extends AsyncTask<Void,Void,Void>{
        private NoteDao noteDao;
        private DeleteAllNoteAsyncTask(NoteDao noteDao){
            this.noteDao=noteDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noteDao.deleteAllNotes();
            return null;
        }
    }
}
