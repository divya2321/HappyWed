package com.example.happywed.DBCon;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class HappyWedDB {

    private static DatabaseReference dbReference;
    private static FirebaseAuth firebaseAuth;
    private static StorageReference storageReference;

    public static synchronized DatabaseReference getDBConnection(){
        if (dbReference==null){
            dbReference = FirebaseDatabase.getInstance().getReference();
        }
        return dbReference;
    }

    public static synchronized FirebaseAuth getFirebaseAuth(){
        if (firebaseAuth==null){
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }

    public static synchronized StorageReference getStorageReference(){
        if (storageReference==null){
            storageReference = FirebaseStorage.getInstance().getReference();
        }
        return storageReference;
    }



}

//class HappyWedUser {
//
//    private static SQLiteDatabase sqLiteUserDatabase;
//
//    public static synchronized void createUserConnection(Context context){
//        if (sqLiteUserDatabase==null){
//            sqLiteUserDatabase = context.openOrCreateDatabase("HappyWedUser", Context.MODE_PRIVATE, null);
//            sqLiteUserDatabase.execSQL("CREATE TABLE IF NOT EXISTS user(userId String PRIMARY KEY, userName VARCHAR(50), partnerName VARCHAR(50), weddingDate VARCHAR(10), estBudget VARCHAR(10), status VARCHAR(10))");
//        }
//    }
//
//    public static void iud(Context context, String query){
//        if (sqLiteUserDatabase ==null){
//            createUserConnection(context);
//        }
//        sqLiteUserDatabase.execSQL(query);
//    }
//
//
//    public static Cursor search(Context context, String searchQuery){
//        if (sqLiteUserDatabase==null){
//            createUserConnection(context);
//        }
//        Cursor cursor = sqLiteUserDatabase.rawQuery(searchQuery, null);
//        return  cursor;
//    }
//
//}
//
