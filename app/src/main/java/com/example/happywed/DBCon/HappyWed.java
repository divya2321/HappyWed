package com.example.happywed.DBCon;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HappyWed {

    private static SQLiteDatabase sqLiteDatabase;

    public static synchronized void createConnection(Context context){
        if(sqLiteDatabase ==null) {
            sqLiteDatabase = context.openOrCreateDatabase("HappyWed", Context.MODE_PRIVATE, null);
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS user(id INTEGER PRIMARY KEY AUTOINCREMENT, userName VARCHAR(100), partnerName VARCHAR(500), weddingDate VARCHAR(15), estBudget VARCHAR(20))");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS checklist(itemID INTEGER PRIMARY KEY AUTOINCREMENT, itemName VARCHAR(500), completeDate VARCHAR(500), status VARCHAR(500))");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS guest(guestID INTEGER PRIMARY KEY AUTOINCREMENT, familyName VARCHAR(500), adultCount INTEGER, childCount INTEGER)");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS budget(budgetId INTEGER PRIMARY KEY AUTOINCREMENT, budgetInfo VARCHAR(500), budgetCost DOUBLE)");
            sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS custom" +
                    "(customId INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "customTitle VARCHAR(500), " +
                    "customContact VARCHAR(10)," +
                    "customNo VARCHAR(15), " +
                    "customStreet VARCHAR(60)," +
                    "customCity VARCHAR(60)," +
                    "customDescription VARCHAR(500)," +
                    "customBudget VARCHAR(10))");
        }
    }

    public static void iud(Context context, String query){
            createConnection(context);
        sqLiteDatabase.execSQL(query);
    }


    public static Cursor search(Context context, String searchQuery){
//        if (sqLiteDatabase==null ){
            createConnection(context);
//        }
        Cursor cursor = sqLiteDatabase.rawQuery(searchQuery, null);
        return  cursor;
    }


    public static void resetUserDatabase(Context context) {
        createConnection(context);
//        SQLiteDatabase sqLiteDB = context.openOrCreateDatabase("HappyWed", Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("DELETE FROM user");

    }

    public static void resetAllDatabase(Context context) {
        createConnection(context);
//        SQLiteDatabase sqLiteDB = context.openOrCreateDatabase("HappyWed", Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("DELETE FROM user");
        sqLiteDatabase.execSQL("DELETE FROM checklist");
        sqLiteDatabase.execSQL("DELETE FROM guest");
        sqLiteDatabase.execSQL("DELETE FROM budget");
        sqLiteDatabase.execSQL("DELETE FROM custom");
    }

    public static boolean isUserTableExists() {

        if (!sqLiteDatabase.isOpen()) {
            return false;
        }
        Cursor cursor = sqLiteDatabase.rawQuery("SELECT COUNT(*) FROM user",null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

}