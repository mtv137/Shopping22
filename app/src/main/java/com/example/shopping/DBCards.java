package com.example.shopping;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBCards {
    private SQLiteDatabase dbCards;

    public DBCards(Context context) {                         //конструктор
        OpenHelper mOpenHelper = new OpenHelper(context);
        dbCards = mOpenHelper.getWritableDatabase();
    }

    public void delete(long number) {                            //метод удаления карточки из БД
        int clearCount = dbCards.delete("CARDS", "Number = ?", new String[] { String.valueOf(number) });
    }

    public String[] find(long number) {                          //метод поиска карточки в БД
        Cursor mCursor = dbCards.query("CARDS", null, "Number = ?", new String[]{String.valueOf(number)}, null, null, null);
        String[] card=new String[4];
        mCursor.moveToFirst();
        card[0] = Integer.toString(mCursor.getInt(0));
        card[1] = mCursor.getString(1);
        card[2] = mCursor.getString(2);
        card[3] = mCursor.getString(3);
        return card;
    }

    public String[] last() {                                     //метод определения последней записи в БД
        Cursor mCursor = dbCards.query("CARDS", null, null, null, null, null, null);
        String[] card=new String[4];
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                card[0] = Integer.toString(mCursor.getInt(0));
                card[1]= mCursor.getString(1);
                card[2] = mCursor.getString(2);
                card[3] = mCursor.getString(3);
            } while (mCursor.moveToNext());
        }
        return card;
    }
    public ArrayList<String> selectAll() {                       //метод для выборки всей таблицы БД
        Cursor mCursor = dbCards.query("CARDS", null, null, null, null, null, null);
        ArrayList<String> arr = new ArrayList<String>();
        ListItemDressDB card=new ListItemDressDB();
        mCursor.moveToFirst();
        if (!mCursor.isAfterLast()) {
            do {
                card.setNumber(Integer.toString(mCursor.getInt(0)));
                card.setTitleDress(mCursor.getString(1));
                card.setPriceDress(mCursor.getString(2));
                card.setHyperlinkDress(mCursor.getString(3));
                arr.add(DresstoString(card));
            } while (mCursor.moveToNext());
        }
        return arr;
    }

    public long insert(String name,String price,String hyperlink){             //метод добавления карточки в БД
        ContentValues contentValues=new ContentValues();
        contentValues.put("Name", name);
        contentValues.put("Price", price);
        contentValues.put("Hyperlink", hyperlink);
        Log.d("MyLog", "arr:*******************************"+contentValues.toString() );
        return dbCards.insert("CARDS",null,contentValues);
    }
    public long insert(){             //метод добавления карточки в БД
        ContentValues contentValues=new ContentValues();
        contentValues.put("Name", "Платье летнее");
        contentValues.put("Price", "1000 руб.");
        contentValues.put("Hyperlink", "http:ffffff");
        return dbCards.insert("CARDS",null,contentValues);

    }

    public class OpenHelper extends SQLiteOpenHelper {

        public OpenHelper(Context context) {
            super(context, "cards.db", null, 1);              // Определяем имя БД
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table CARDS ("                  // SQL-запрос на создание таблицы БД
                    + "Number integer primary key autoincrement,"
                    + "Name text,"
                    + "Price text,"
                    + "Hyperlink text" + ");");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS cards.db");        // SQL-запрос на удаление старой таблицы БД
            onCreate(db);
        }
    }

    private static String DresstoString(ListItemDressDB dress){
        String s="";
        s="% "+dress.getNumber()+"% "+dress.getTitleDress()+"% "+dress.getPriceDress()+"% "+dress.getHyperlinkDress();
        return s;
    }
}
