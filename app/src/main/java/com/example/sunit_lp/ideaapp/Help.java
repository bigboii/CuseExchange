
package com.example.sunit_lp.ideaapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


/**
 * Created by Sunit-LP on 4/18/2016.
 */

public class Help extends YouTubeBaseActivity {
    private YouTubePlayerView youTubePlayerView;
    private TextView title;
    private YouTubePlayer.OnInitializedListener onInitializedListener;
    String mVideoId= "AhCGB13MTQs";
    private Button play_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        youTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtube_view);
     //   title = (TextView) findViewById(R.id.title);
        play_button=(Button)findViewById(R.id.play_button);
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youTubePlayerView.initialize("AIzaSyC6NTdMB3NX6GwD89INxpPDgZswJi9DQ3A",onInitializedListener);
            }
        });
        onInitializedListener = new YouTubePlayer.OnInitializedListener(){

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.setFullscreenControlFlags(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_ORIENTATION);
                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE);
                youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CONTROL_SYSTEM_UI);
                if (mVideoId != null) {
                    if (b) {
                        youTubePlayer.play();
                    } else {
                        youTubePlayer.loadVideo(mVideoId);
                    }
                }
            }
            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };




    }
}

