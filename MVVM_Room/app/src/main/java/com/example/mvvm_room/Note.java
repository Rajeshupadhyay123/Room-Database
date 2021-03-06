package com.example.mvvm_room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @Entity annotation hide many code which are written in the sqlite database
 * here we are passing the parameter tableName which will give you an table name
 * because by-default the room create table as "note" but by passing this parameter
 * it create "note_table" like sqlite
 */
@Entity(tableName = "note_table")
public class Note {

    /**
     * @PrimaryKey annotation help to create the id autogenerated by comipoler here we don't
     * need to give the implementation details. It is same as primary key autoincrement parameter in the sqlite
     */
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private String description;


    private int priority;

    public Note(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;
    }

    /**
     * Here we only need to create the setter method for Id
     * which is set to be auto genereated
     * @param id
     */
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }



    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }


}
