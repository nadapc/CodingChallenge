package Services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import BDD.BDD_ElementRepo;
import Classes.Element_Repo;
import nei.nada.codingchallenge.R;


public class myTrendingRepo extends Service {

    private final Context mContext;
    private String TAG = "CodingChallenge";
    private int iHttpStatus;

    private String sResultJson = "clear" ;
    private boolean noErreur = true;

    private List<Element_Repo> listRepos;
    private List<Element_Repo> listReposRSS;

    private BDD_ElementRepo elementBdd;
    public static final String DATE_FORMAT = "yyyy-MM-dd";


    public myTrendingRepo(Context context) {
        //Initialisation des objets
        this.mContext = context;
        initObjects();

    }

    private void initObjects() {
        elementBdd = new BDD_ElementRepo(mContext);
    }

    public void getReposList(final int pageNumber){

        noErreur = true;

        //Déclaration des listes
        listRepos = new ArrayList<Element_Repo>();
        listRepos.clear();
        listReposRSS = new ArrayList<Element_Repo>();
        listReposRSS.clear();

        Thread t = new Thread() {

            @Override
            public void run() {
                HttpURLConnection myURLConnection = null;
                try {

                    URL url = new URL(mContext.getResources().getString(R.string.environement_url) + "search/repositories?q=created:>"+getCurrentDate()+"&sort=stars&order=desc&page="+pageNumber);
                    myURLConnection = (HttpURLConnection) url.openConnection();
                    myURLConnection.setRequestMethod("GET");
                    Log.d(TAG, "myTrendingRepo - getReposList - url : " + url);

                    //Récupération de la réponse
                    int iHttpStatus = myURLConnection.getResponseCode();
                    Log.d(TAG, "myTrendingRepo - getReposList - iHttpStatus : " + iHttpStatus);

                    if (iHttpStatus == HttpURLConnection.HTTP_OK ) {

                        InputStreamReader inStream = new InputStreamReader((myURLConnection.getInputStream()));
                        BufferedReader buReader = new BufferedReader(inStream);
                        try {
                            StringBuilder sb = new StringBuilder();
                            String output;
                            while ((output = buReader.readLine()) != null) {
                                sb.append(output);
                            }
                            sResultJson = sb.toString();
                        } finally {
                            buReader.close();
                            inStream.close();
                        }

                    } else {

                        Log.e(TAG, "myTrendingRepo - getReposList - iHttpStatus : " + iHttpStatus);
                        noErreur = false;

                    }

                } catch (IOException e) {
                    Log.e(TAG, "myTrendingRepo - getReposList - IOException : " + e);
                    e.printStackTrace();
                    noErreur = false;
                } catch (NullPointerException e) {
                    Log.e(TAG, "myTrendingRepo - getReposList - NullPointerException : " + e);
                    e.printStackTrace();
                    noErreur = false;
                } catch (IllegalStateException e) {
                    Log.e(TAG, "myTrendingRepo - getReposList - IllegalStateException : " + e);
                    e.printStackTrace();
                    noErreur = false;
                } catch (OutOfMemoryError e) {
                    Log.e(TAG, "myTrendingRepo - getReposList - OutOfMemoryError : " + e);
                    e.printStackTrace();
                    noErreur = false;
                } finally {

                    if (myURLConnection != null) {
                        try {
                            myURLConnection.disconnect();
                        } catch (Exception ex) {
                            Log.e(TAG, "myTrendingRepo - getReposList - Exception disconnect : " + ex);
                        }
                    }

                }

                if (sResultJson.equals("")) noErreur = false;

                if (noErreur ) {

                    listReposRSS = ParseResultRepos (sResultJson);
                    Log.d(TAG,"getDataRepos listReposRSS "+listReposRSS.size());

                    //On vide la base
                    elementBdd.open();
                    elementBdd.insertListRepo(listReposRSS);
                    elementBdd.close();
                    sendBroadcast("reposChanged OK");


                } else {
                    Log.e(TAG,"myTrendingRepo - getReposList Error");
                    sendBroadcast("reposChanged KO");

                }
            }
        };
        t.start();
    }

    public List<Element_Repo> ParseResultRepos (String sResult) {

        List<Element_Repo> lResult = new ArrayList<Element_Repo>();
        lResult.clear();

        try {

            JSONObject reader = new JSONObject(sResultJson);
            JSONArray items = reader.getJSONArray("items");

            for (int i=0; i < items.length(); i++) {

                try {

                    JSONObject itemsObject = items.getJSONObject(i);
                    Element_Repo currentElement = new Element_Repo();

                    try { currentElement.setRepo_id(itemsObject.getString("id")); }
                    catch (JSONException e) { currentElement.setRepo_id("null"); }

                    try { currentElement.setRepo_name(itemsObject.getString("full_name")); }
                    catch (JSONException e) { currentElement.setRepo_name("null");}

                    try { currentElement.setRepo_desc(itemsObject.getString("description")); }
                    catch (JSONException e) { currentElement.setRepo_desc("null"); }

                    try { currentElement.setRepo_stars(itemsObject.getInt("stargazers_count")); }
                    catch (JSONException e) { currentElement.setRepo_stars(0);}

                    JSONObject itemsObjectOwner = itemsObject.getJSONObject("owner");

                    try { currentElement.setRepo_username(itemsObjectOwner.getString("login")); }
                    catch (JSONException e) { currentElement.setRepo_username("null");}

                    try { currentElement.setRepo_avatar(itemsObjectOwner.getString("avatar_url")); }
                    catch (JSONException e) { currentElement.setRepo_avatar("null");}

                    lResult.add(currentElement);

                } catch (JSONException e) {

                    Log.e(TAG, "myTrendingRepo - lJSONException (" + i + ") :" + e.toString());

                }
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return lResult;

    }

    private void sendBroadcast(String sStatus) {

        Intent intent = new Intent("GetRepoList");
        intent.putExtra("status", sStatus);
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public static String getCurrentDate() {

        Calendar cal=Calendar.getInstance();
        int currentDay=cal.get(Calendar.DAY_OF_MONTH);
        //Set the date to 2 days ago
        cal.set(Calendar.DAY_OF_MONTH, currentDay-30);

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(cal.getTime());
    }


}
