package com.example.woof;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

public class dBaseOperations extends SQLiteOpenHelper {

    public dBaseOperations(@Nullable Context context) {
        super(context, "Woof", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE tblUser(id INTEGER PRIMARY KEY AUTOINCREMENT, uName VARCHAR(50), uPhone INTEGER, uEmail VARCHAR(50), dob DATE, uRole VARCHAR(20), pass VARCHAR(64))";
        db.execSQL(sql);
        sql = "CREATE TABLE tblInventry(id INTEGER PRIMARY KEY AUTOINCREMENT, iCat VARCHAR(50), iName VARCHAR(50), iImage BLOB, iPrice DOUBLE, iDescription VARCHAR(255), iQuantity INTEGER)";
        db.execSQL(sql);
        sql = "CREATE TABLE tblNews(id INTEGER PRIMARY KEY AUTOINCREMENT, nDescription VARCHAR(255), nImage BLOB)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "DROP TABLE IF EXISTS tblUser";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS tblInventry";
        db.execSQL(sql);
        sql = "DROP TABLE IF EXISTS tblNews";
        db.execSQL(sql);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public void createUser(User user) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String hashedPassword = hashPassword(user.getPass());

        cv.put("uName", user.getuName());
        cv.put("uPhone", user.getuPhone());
        cv.put("uEmail", user.getuEmail());
        cv.put("dob", sdf.format(user.getDob()));
        cv.put("uRole", "cus");
        cv.put("pass", hashedPassword);
        database.insert("tblUser", null, cv);

        database.close();
    }

    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT 1 FROM tblUser WHERE uEmail = ?", new String[]{email});
        boolean exists = (cursor.getCount() > 0);
        cursor.close();
        db.close();
        return exists;
    }

    public User findUserByEmail(String email) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM tblUser WHERE uEmail = ?", new String[]{email});
        User user = null;
        if (cursor.moveToFirst()) {
            user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndex("id")));
            user.setuName(cursor.getString(cursor.getColumnIndex("uName")));
            user.setuEmail(cursor.getString(cursor.getColumnIndex("uEmail")));
            user.setuPhone(cursor.getInt(cursor.getColumnIndex("uPhone")));
            user.setPass(cursor.getString(cursor.getColumnIndex("pass")));
            String dobString = cursor.getString(cursor.getColumnIndex("dob"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date dob = sdf.parse(dobString);
                user.setDob(dob);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            user.setuRole(cursor.getString(cursor.getColumnIndex("uRole")));
        }
        cursor.close();
        database.close();
        return user;
    }

    public User checkLogin(String uEmail, String pass) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(pass);
        Cursor cursor = db.rawQuery("SELECT * FROM tblUser WHERE uEmail = ? AND pass = ?", new String[]{uEmail, hashedPassword});

        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            String uName = cursor.getString(cursor.getColumnIndexOrThrow("uName"));
            int uPhone = cursor.getInt(cursor.getColumnIndexOrThrow("uPhone"));
            String uRole = cursor.getString(cursor.getColumnIndexOrThrow("uRole"));
            Date dob = null;
            String dobString = cursor.getString(cursor.getColumnIndexOrThrow("dob"));

            if (dobString != null) {
                try {
                    dob = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dobString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            cursor.close();
            db.close();
            return new User(id, uName, uPhone, uEmail, dob, uRole, hashedPassword);
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    public int updateUserProfile(User user, String currentEmail) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        cv.put("uName", user.getuName());
        cv.put("uPhone", user.getuPhone());
        cv.put("dob", sdf.format(user.getDob()));

        int rowsUpdated = database.update("tblUser", cv, "uEmail = ?", new String[]{currentEmail});
        database.close();
        return rowsUpdated;
    }



    public int updateUser(User user) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        cv.put("uName", user.getuName());
        cv.put("uPhone", user.getuPhone());
        cv.put("uEmail", user.getuEmail());
        cv.put("dob", sdf.format(user.getDob()));

        int rowsUpdated = database.update("tblUser", cv, "id = ?", new String[]{String.valueOf(user.getId())});
        database.close();
        return rowsUpdated;
    }

    public int updateUserPassword(User user) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        String hashedPassword = hashPassword(user.getPass());

        cv.put("pass", hashedPassword);

        int rowsUpdated = database.update("tblUser", cv, "id = ?", new String[]{String.valueOf(user.getId())});
        database.close();
        return rowsUpdated;
    }

    public int deleteUser(String userName) {
        SQLiteDatabase database = getWritableDatabase();
        int rowsDeleted = database.delete("tblUser", "uName = ?", new String[]{userName});
        database.close();
        return rowsDeleted;
    }

    public ArrayList<User> getUsers() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM tblUser", null);
        ArrayList<User> arrayList = new ArrayList<>();

        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex("id")));
                user.setuName(cursor.getString(cursor.getColumnIndex("uName")));
                user.setuPhone(cursor.getInt(cursor.getColumnIndex("uPhone")));
                user.setuEmail(cursor.getString(cursor.getColumnIndex("uEmail")));
                String dobString = cursor.getString(cursor.getColumnIndex("dob"));
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    Date dob = sdf.parse(dobString);
                    user.setDob(dob);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                user.setuRole(cursor.getString(cursor.getColumnIndex("uRole")));

                arrayList.add(user);
            }
        } else {
            arrayList = null;
        }

        cursor.close();
        database.close();
        return arrayList;
    }

    // Inventory and News feed operations will be handled below this point

    public long insertInventory(String iCat, String iName, byte[] iImage, double iPrice, String iDescription, int iQuantity) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("iCat", iCat);
        cv.put("iName", iName);
        cv.put("iImage", iImage);
        cv.put("iPrice", iPrice);
        cv.put("iDescription", iDescription);
        cv.put("iQuantity", iQuantity);
        long id = database.insert("tblInventry", null, cv);
        database.close();
        return id;
    }

    public Inventory getInventory(int id) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM tblInventry WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            Inventory inventory = new Inventory(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("iCat")),
                    cursor.getString(cursor.getColumnIndex("iName")),
                    cursor.getBlob(cursor.getColumnIndex("iImage")),
                    cursor.getDouble(cursor.getColumnIndex("iPrice")),
                    cursor.getString(cursor.getColumnIndex("iDescription")),
                    cursor.getInt(cursor.getColumnIndex("iQuantity"))
            );
            cursor.close();
            database.close();
            return inventory;
        } else {
            cursor.close();
            database.close();
            return null;
        }
    }

    public int updateInventory(int id, String iCat, String iName, byte[] iImage, double iPrice, String iDescription, int iQuantity) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("iCat", iCat);
        cv.put("iName", iName);
        cv.put("iImage", iImage);
        cv.put("iPrice", iPrice);
        cv.put("iDescription", iDescription);
        cv.put("iQuantity", iQuantity);
        int rowsAffected = database.update("tblInventry", cv, "id = ?", new String[]{String.valueOf(id)});
        database.close();
        return rowsAffected;
    }

    public int deleteInventory(int id) {
        SQLiteDatabase database = getWritableDatabase();
        int rowsAffected = database.delete("tblInventry", "id = ?", new String[]{String.valueOf(id)});
        database.close();
        return rowsAffected;
    }

    public ArrayList<Inventory> getAllInventories() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM tblInventry", null);
        ArrayList<Inventory> inventoryList = new ArrayList<>();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Inventory inventory = new Inventory(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("iCat")),
                        cursor.getString(cursor.getColumnIndex("iName")),
                        cursor.getBlob(cursor.getColumnIndex("iImage")),
                        cursor.getDouble(cursor.getColumnIndex("iPrice")),
                        cursor.getString(cursor.getColumnIndex("iDescription")),
                        cursor.getInt(cursor.getColumnIndex("iQuantity"))
                );
                inventoryList.add(inventory);
            }
        }
        cursor.close();
        database.close();
        return inventoryList;
    }

    public long insertNews(String nDescription, byte[] nImage) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nDescription", nDescription);
        cv.put("nImage", nImage);
        long id = database.insert("tblNews", null, cv);
        database.close();
        return id;
    }

    public News getNews(int id) {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM tblNews WHERE id = ?", new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            News news = new News(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("nDescription")),
                    cursor.getBlob(cursor.getColumnIndex("nImage"))
            );
            cursor.close();
            database.close();
            return news;
        } else {
            cursor.close();
            database.close();
            return null;
        }
    }

    public int updateNews(int id, String nDescription, byte[] nImage) {
        SQLiteDatabase database = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("nDescription", nDescription);
        cv.put("nImage", nImage);
        int rowsAffected = database.update("tblNews", cv, "id = ?", new String[]{String.valueOf(id)});
        database.close();
        return rowsAffected;
    }

    public int deleteNews(int id) {
        SQLiteDatabase database = getWritableDatabase();
        int rowsAffected = database.delete("tblNews", "id = ?", new String[]{String.valueOf(id)});
        database.close();
        return rowsAffected;
    }

    public ArrayList<News> getAllNews() {
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery("SELECT * FROM tblNews", null);
        ArrayList<News> newsList = new ArrayList<>();
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                News news = new News(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("nDescription")),
                        cursor.getBlob(cursor.getColumnIndex("nImage"))
                );
                newsList.add(news);
            }
        }
        cursor.close();
        database.close();
        return newsList;
    }
}
