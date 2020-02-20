package BDD;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import Classes.Element_Repo;

public class BDD_ElementRepo {

    private Context mContext;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "RepoDatabase.db";

    private static final String TABLE_REPO = "repo_table";
    private static final String KEY_ID = "id";
    private static final String KEY_REPO_ID = "repo_id";
    private static final String KEY_REPO_NAME = "repo_name";
    private static final String KEY_REPO_DESC= "repo_desc";
    private static final String KEY_REPO_STARS = "repo_stars";
    private static final String KEY_REPO_USERNAME = "repo_username";
    private static final String KEY_REPO_AVATAR = "repo_avatar";

    private SQLiteDatabase bdd;
    private BDD_ElementRepo_BaseSQLite BaseSQLite;

    public BDD_ElementRepo(Context context){
        BaseSQLite = new BDD_ElementRepo_BaseSQLite(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext=context;
    }

    public void open(){
        if (bdd==null || !bdd.isOpen()) bdd = BaseSQLite.getWritableDatabase();
    }

    public void openRead(){
        bdd = BaseSQLite.getReadableDatabase();
    }

    public void close(){
        if (bdd!=null && bdd.isOpen()) bdd.close();
    }

    public int insertListRepo(List<Element_Repo> Elements){

        if (bdd==null || !bdd.isOpen()) bdd = BaseSQLite.getWritableDatabase();
        bdd.beginTransaction();
        ContentValues values = new ContentValues();
        int iValeur = 0;
        for (int i = 0; i < Elements.size(); i++) {

            values.put(KEY_REPO_ID, Elements.get(i).getRepo_id());
            values.put(KEY_REPO_NAME, Elements.get(i).getRepo_name());
            values.put(KEY_REPO_DESC, Elements.get(i).getRepo_desc());
            values.put(KEY_REPO_STARS, Elements.get(i).getRepo_stars());
            values.put(KEY_REPO_USERNAME, Elements.get(i).getRepo_username());
            values.put(KEY_REPO_AVATAR, Elements.get(i).getRepo_avatar());

            bdd.insert(TABLE_REPO, null, values);
        }

        //Insertion de l'element dans la BDD via le ContentValues
        bdd.setTransactionSuccessful();
        bdd.endTransaction();

        return iValeur;

    }

    public List<Element_Repo> getAllRepo() {

        List<Element_Repo> repoList = new ArrayList<Element_Repo>();
        String selectQuery = "SELECT  * FROM " + TABLE_REPO;

        Cursor cursor = bdd.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {

            do {

                Element_Repo repo = new Element_Repo();
                repo.setRepo_id(cursor.getString(1));
                repo.setRepo_name(cursor.getString(2));
                repo.setRepo_desc(cursor.getString(3));
                repo.setRepo_stars(cursor.getInt(4));
                repo.setRepo_username(cursor.getString(5));
                repo.setRepo_avatar(cursor.getString(6));

                repoList.add(repo);

            } while (cursor.moveToNext());
        }

        return repoList;
    }

    public int removeRepotable(){
        if (bdd==null || !bdd.isOpen()) bdd = BaseSQLite.getWritableDatabase();
        return bdd.delete(TABLE_REPO, null, null);
    }

}