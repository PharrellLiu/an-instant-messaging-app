package my.application;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.List;

public class MomentAdapter extends RecyclerView.Adapter<MomentAdapter.MomentViewHolder> {

    private List<Moment> mDataset;
    private Context mcontext;

    public MomentAdapter(List<Moment> myDataset, Context mycontext){ mDataset = myDataset; mcontext = mycontext; }

    @NonNull
    @Override
    public MomentAdapter.MomentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.moment_recyclerview, parent, false);
        MomentViewHolder vh = new MomentViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MomentAdapter.MomentViewHolder holder, int position) {
        Moment moment = mDataset.get(position);
        int type = moment.getType();
        holder.moment_username.setVisibility(View.VISIBLE);
        holder.moment_content.setVisibility(View.VISIBLE);
        holder.moment_time.setVisibility(View.VISIBLE);

        holder.moment_username.setText(moment.getUserName());
        holder.moment_content.setText(moment.getContent());
        holder.moment_time.setText(moment.getTime());

        // 0 is text, 1 is image, 2 is video
        if (type==0){
            holder.moment_picture.setVisibility(View.GONE);
            holder.moment_exo_play_view.setVisibility(View.GONE);
        } else if (type == 1) {
            holder.moment_picture.setVisibility(View.VISIBLE);
            holder.moment_exo_play_view.setVisibility(View.GONE);
            Glide.with(mcontext).load(moment.getUrl()).into(holder.moment_picture);
        } else {
            holder.moment_picture.setVisibility(View.GONE);
            holder.moment_exo_play_view.setVisibility(View.VISIBLE);
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory factory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
            DefaultTrackSelector trackSelection = new DefaultTrackSelector(factory);
            SimpleExoPlayer exoPlayer = ExoPlayerFactory.newSimpleInstance(mcontext, trackSelection);
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(mcontext,
                    Util.getUserAgent(mcontext,"myapp"));
            Uri uri = Uri.parse(moment.getUrl());
            MediaSource mediaSources = new ExtractorMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
            holder.moment_exo_play_view.setPlayer(exoPlayer);
            exoPlayer.prepare(mediaSources);
            exoPlayer.setPlayWhenReady(true);
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class MomentViewHolder extends RecyclerView.ViewHolder {
        public TextView moment_username;
        public TextView moment_content;
        public ImageView moment_picture;
        public PlayerView moment_exo_play_view;
        public TextView moment_time;

        public MomentViewHolder(@NonNull View itemView) {
            super(itemView);
            moment_username = itemView.findViewById(R.id.moment_username);
            moment_content = itemView.findViewById(R.id.moment_content);
            moment_picture = itemView.findViewById(R.id.moment_picture);
            moment_exo_play_view = itemView.findViewById(R.id.moment_exo_play_view);
            moment_time = itemView.findViewById(R.id.moment_time);
        }
    }

    public static class Moment {
        private String url;
        private String content;
        private String userName;
        private String time;
        // 0 is text, 1 is image, 2 is video
        private int type;

        public Moment(String url, String content, String userName, String time, int type) {
            this.url = url;
            this.content = content;
            this.time = time;
            this.type = type;
            this.userName = userName;
        }

        public String getUrl() { return url; }
        public String getUserName() { return userName; }
        public String getTime() { return time; }
        public String getContent() { return content; }
        public int getType() { return type; }
    }

}
