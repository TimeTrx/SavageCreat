package ca.mortec.savageworldscharactercreator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.sql.SQLException;

/**
 * Created by Mike on 11/28/2016.
 * Provides easy connection and creation of UserCharacters database.
 */

 class DBHandler {

    //database name
    static final private String DATABASE_NAME = "characters";

    private SQLiteDatabase db;//for interacting with the database
    private DatabaseOpenHelper databaseOpenHelper;//creates the database

    //public constructor for DatabaseConnector
    DBHandler(Context context)
    {
        System.out.println("PROGRAM-TRACE: DBHamdler---DBHandler Constructor called");
        //System.out.println("DB created");
        //create a new DatabaseOpenHelper
        databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME, null, 1);
    }

    //updates an existing character in the database
    void updateCharacter(long num, String name, String placeholder2, String placeholder3, String placeholder4, String placeholder5, String placeholder6, String placeholder7) throws SQLException
    {
        System.out.println("PROGRAM-TRACE: DBHamdler---updateCharacter called, NAME: " + name);
        ContentValues editCharacter = new ContentValues();
        editCharacter.put("name", name);
        editCharacter.put("placeholder2", placeholder2);
        editCharacter.put("placeholder3", placeholder3);
        editCharacter.put("placeholder4", placeholder4);
        editCharacter.put("placeholder5", placeholder5);
        editCharacter.put("placeholder6", placeholder6);
        editCharacter.put("placeholder7", placeholder7);
        open();//open the database
        db.update("characters", editCharacter, "_id=" + num, null);
        close();//close the database

    }

    //open the database connection
    protected void open() throws SQLException
    {
        System.out.println("PROGRAM-TRACE: DBHamdler---open called, database is opened or created");
        //create or open a database for reading or writing
        db = databaseOpenHelper.getWritableDatabase();
    }

    //close the database connection
    void close() throws SQLException
    {
        if(db != null)
        {
            System.out.println("PROGRAM-TRACE: DBHamdler---close called, database exists, closing database");
            db.close();//close the database connection
        }
    }

    //inserts a new character in the database
    long insertCharacter(String name, String placeholder2, String placeholder3, String placeholder4, String placeholder5, String placeholder6, String placeholder7) throws SQLException
    {
        System.out.println("PROGRAM-TRACE: DBHamdler---insertCharacter called, NAME: " +name);
        ContentValues newCharacter = new ContentValues();
        newCharacter.put("name", name);
        newCharacter.put("placeholder2", placeholder2);
        newCharacter.put("placeholder3", placeholder3);
        newCharacter.put("placeholder4", placeholder4);
        newCharacter.put("placeholder5", placeholder5);
        newCharacter.put("placeholder6", placeholder6);
        newCharacter.put("placeholder7", placeholder7);

        open();//open the database
        long rowID = db.insert("characters", null, newCharacter);
        close();//close the database
        return rowID;
    }

    //return a Cursor with all character names in the database
    Cursor getAllCharacters()
    {
        System.out.println("PROGRAM-TRACE: DBHamdler---getAllCharacters called");
        return db.query("characters", new String[] {"_id", "name"}, null, null, null, null, "name");
    }

    //return a cursor containing specified character's information
    Cursor getOneCharacter(long num)
    {
        System.out.println("PROGRAM-TRACE: DBHamdler---getOneCharacter " + num + " called");
        return db.query("characters", null, "_id=" +num, null, null, null, null);
    }

    //delete the character given  by num
    void deleteCharacter(long num) throws SQLException
    {
        System.out.println("PROGRAM-TRACE: DBHamdler---deleteCharacter" + num +" called");
        open();
        db.delete("characters", "_id=" +num, null);
        close();
    }

    private class DatabaseOpenHelper extends SQLiteOpenHelper
    {

        //constructor
        DatabaseOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
        {
            super(context, name, factory, version);
        }
        //craetes the character table when the database is created
        @Override
        public void onCreate(SQLiteDatabase db)
        {
            System.out.println("PROGRAM-TRACE: DatabaseOpenHelper---onCreate called");
            //query to create a new table in the database called characters
            String createQuery = "CREATE TABLE characters" + "(_id integer primary key autoincrement,"+"name TEXT, placeholder2 TEXT, placeholder3 TEXT, " + " placeholder4 TEXT, placeholder5 TEXT, placeholder6 TEXT, placeholder7 TEXT);";
            db.execSQL(createQuery);//execute query to create the database
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            //TODO IMPLEMENT THIS *******
            System.out.println("PROGRAM-TRACE: DatabaseOpenHelper---onUpgrade called");
        }

    }
}
