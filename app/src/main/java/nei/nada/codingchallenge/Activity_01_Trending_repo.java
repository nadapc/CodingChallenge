package nei.nada.codingchallenge;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.util.ArrayList;

import Adapters.Adapter_trendingRepos;
import BDD.BDD_ElementRepo;
import Classes.Element_Repo;
import Services.myTrendingRepo;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_IDLE;

public class Activity_01_Trending_repo extends AppCompatActivity {

    private Context mContext;
    private static final String TAG = "CodingChallenge";

    private ListView lv_repo;
    private ArrayList<Element_Repo> listRepos;
    private Adapter_trendingRepos adapterListTrend;
    private int pageNumber = 0;
    private boolean loadingMore = false;
    private View loadMoreView;

    private myTrendingRepo mytrendingRepo;

    private int currentScrollState;
    private int currentVisibleItemCount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_01_trending_repo);

        //Déclaration du Context
        mContext = Activity_01_Trending_repo.this;

        //Init composant
        lv_repo = (ListView) findViewById(R.id.lv_repo);
        mytrendingRepo = new myTrendingRepo(mContext);
        loadMoreView = ((LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_load_more_view, null, false);

        //Gestion de la listview
        listRepos = new ArrayList<Element_Repo>();
        adapterListTrend = new Adapter_trendingRepos(mContext,R.layout.layout_listview_trend_item, listRepos);
        lv_repo.setAdapter(adapterListTrend);

        lv_repo.setOnScrollListener(new AbsListView.OnScrollListener(){

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                currentScrollState = scrollState;

                if (currentVisibleItemCount > 0 && currentScrollState == SCROLL_STATE_IDLE) {
                    if(!loadingMore){
                        loadingMore=true;
                        mytrendingRepo.getReposList(pageNumber);
                        pageNumber++;
                        lv_repo.addFooterView(loadMoreView);

                    }

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                currentVisibleItemCount = visibleItemCount;
            }
        });

        //Recuperation de la date depuis la bdd
        getDataRepos();

        //Recuperation de la data depuis l api
        mytrendingRepo.getReposList(pageNumber);
        pageNumber++;
    }


    private void getDataRepos(){

        BDD_ElementRepo BDD_repos = new BDD_ElementRepo(mContext);
        BDD_repos.openRead();
        listRepos.clear();
        listRepos.addAll(BDD_repos.getAllRepo());
        BDD_repos.close();

        if (listRepos.size()==0)lv_repo.addFooterView(loadMoreView);
        else lv_repo.removeFooterView(loadMoreView);

        adapterListTrend.notifyDataSetChanged();

    }

    //------------------------------------
    //  Gestion des broadcasts
    //------------------------------------

    private BroadcastReceiver mMessageReceiverReposListUpdated = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String state = intent.getExtras().getString("status");
            if (state.equals("reposChanged OK")) {
                loadingMore=false;
                getDataRepos();
            }

            lv_repo.removeFooterView(loadMoreView);


        }
    };

    //------------------------------------
    //  Gestion de l'activity
    //------------------------------------

    @Override
    protected void onResume() {
        super.onResume();
        //Activation du BroadcastManager
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiverReposListUpdated, new IntentFilter("GetRepoList"));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mMessageReceiverReposListUpdated);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        deleteDatabase("RepoDatabase.db");
        return super.onKeyDown(keyCode, event);

    }
}
