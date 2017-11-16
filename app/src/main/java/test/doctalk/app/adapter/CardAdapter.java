package test.doctalk.app.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import test.doctalk.app.R;
import test.doctalk.app.model.Github;
import test.doctalk.app.model.GithubUser;
import test.doctalk.app.utils.RoundedCornersTransform;


public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    List<GithubUser> mItems;
    Context mContext;

    public CardAdapter(Context mContext) {
        super();
        this.mContext = mContext;
        mItems = new ArrayList<>();
    }

    public void addData(Github github) {
        mItems.addAll(github.getItems());
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        GithubUser user = mItems.get(i);
        viewHolder.login.setText(user.getLogin());
        viewHolder.score.setText("" + user.getScore());
        Picasso.with(mContext).load(user.getAvatar_url()).transform(new RoundedCornersTransform(10, 0)).into(viewHolder.profilePic);
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView login;
        public TextView score;
        public ImageView profilePic;

        public ViewHolder(View itemView) {
            super(itemView);
            login = (TextView) itemView.findViewById(R.id.login);
            score = (TextView) itemView.findViewById(R.id.score);
            profilePic = (ImageView) itemView.findViewById(R.id.profile_picture);
        }
    }
}