package BDD;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class BDD_ElementRepo_BaseSQLite extends SQLiteOpenHelper {

    private static final String TABLE_REPO = "repo_table";
    private static final String KEY_ID = "id";
    private static final String KEY_REPO_ID = "repo_id";
    private static final String KEY_REPO_NAME = "repo_name";
    private static final String KEY_REPO_DESC= "repo_desc";
    private static final String KEY_REPO_STARS = "repo_stars";
    private static final String KEY_REPO_USERNAME = "repo_username";
    private static final String KEY_REPO_AVATAR = "repo_avatar";

    private static final String CREATE_TABLE_REPO = "CREATE TABLE " + TABLE_REPO + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_REPO_ID + " TEXT,"
            + KEY_REPO_NAME + " TEXT,"
            + KEY_REPO_DESC + " TEXT,"
            + KEY_REPO_STARS + " INTEGER,"
            + KEY_REPO_USERNAME + " TEXT,"
            + KEY_REPO_AVATAR + " TEXT" + ")";

    public BDD_ElementRepo_BaseSQLite(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_REPO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPO);
        onCreate(db);
    }

}