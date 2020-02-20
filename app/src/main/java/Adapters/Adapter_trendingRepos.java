package Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import Classes.Element_Repo;
import nei.nada.codingchallenge.R;

public class Adapter_trendingRepos extends ArrayAdapter<Element_Repo> {

    private Context mContext;
    private int iResourceId;

    public Adapter_trendingRepos(Context ctx, int resourceId, List<Element_Repo> objects) {
        super( ctx, resourceId, objects );
        this.mContext = ctx;
        this.iResourceId = resourceId;

    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent ) {

        View listetView = convertView;

        if (listetView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            listetView = inflater.inflate(iResourceId, parent, false);
        }

        final Element_Repo repo = getItem( position );


        TextView tv_repoName=(TextView) listetView.findViewById(R.id.tv_repoName);
        TextView tv_repoDesc=(TextView) listetView.findViewById(R.id.tv_repoDesc);
        TextView tv_repo_userName=(TextView) listetView.findViewById(R.id.tv_repo_userName);
        TextView tv_repo_starsNumber=(TextView) listetView.findViewById(R.id.tv_repo_starsNumber);

        tv_repoName.setText(repo.getRepo_name());
        tv_repoDesc.setText(repo.getRepo_desc());
        tv_repo_userName.setText(repo.getRepo_username());
        tv_repo_starsNumber.setText(String.valueOf(repo.getRepo_stars()));

        return listetView;

    }

}
