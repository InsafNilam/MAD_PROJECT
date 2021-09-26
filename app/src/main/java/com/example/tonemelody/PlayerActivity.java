package com.example.tonemelody;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    ImageView playPauseBtn,nextBtn,prevBtn,fastForwardBtn,fastRewindBtn, imageView;
    TextView txtName,txtEnd,txtStart;
    SeekBar playerSeekBar;
    BarVisualizer barVisualizer;

    String sName;
    static MediaPlayer mediaPlayer;
    int position;
    ArrayList<File> mySongs;
    Thread updateSeekBar;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        if(barVisualizer!=null){
            barVisualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        playPauseBtn =findViewById(R.id.playPauseBtn);
        nextBtn =findViewById(R.id.nextBtn);
        prevBtn =findViewById(R.id.prevBtn);
        fastRewindBtn =findViewById(R.id.fastRewindBtn);
        fastForwardBtn =findViewById(R.id.fastForwardBtn);

        imageView= findViewById(R.id.imageView);

        txtName=findViewById(R.id.txtSong);
        txtEnd=findViewById(R.id.endTime);
        txtStart=findViewById(R.id.startTime);

        playerSeekBar =findViewById(R.id.playerSeekBar);
        barVisualizer=findViewById(R.id.blast);

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i =getIntent();
        Bundle bundle = i.getExtras();

        mySongs = (ArrayList) bundle.getParcelableArrayList("songs");
        position = bundle.getInt("pos");

        txtName.setSelected(true);
        Uri uri = Uri.parse(mySongs.get(position).toString());
        sName =mySongs.get(position).getName()
                .replace(".mp3","")
                .replace(".wav","")
                .replace(".mp4a","")
                .toUpperCase();
        txtName.setText(sName);

        try {
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
        mediaPlayer.start();

        updateSeekBar = new Thread(){
            @Override
            public void run(){
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;

                while(currentPosition<totalDuration){
                    try{
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        playerSeekBar.setProgress(currentPosition);
                    }catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }
            }
        };

        playerSeekBar.setMax(mediaPlayer.getDuration());
        updateSeekBar.start();

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        txtEnd.setText(createTime(mediaPlayer.getDuration()));

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txtStart.setText(createTime(mediaPlayer.getCurrentPosition()));
                handler.postDelayed(this,500);
            }
        },500);

        fastForwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+5000);
                }
            }
        });
        fastRewindBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-5000);
                }
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    playPauseBtn.setImageResource(R.drawable.ic_play);
                    mediaPlayer.pause();
                }else{
                    playPauseBtn.setImageResource(R.drawable.ic_pause);
                    mediaPlayer.start();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    playerSeekBar.setProgress(0);
                }
              position=(position<mySongs.size())?position+1:0;

              Uri u =Uri.parse(mySongs.get(position).toString());

              try{
                  mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                  mediaPlayer.prepare();
                  playerSeekBar.setMax(mediaPlayer.getDuration());
                  updateSeekBar.start();
              }catch (Exception e){
                  e.printStackTrace();
              }
              mediaPlayer.seekTo(0);
              txtEnd.setText(createTime(mediaPlayer.getDuration()));

              sName= mySongs.get(position).getName()
                      .replace(".mp3","")
                      .replace(".wav","")
                      .replace(".mp4a","")
                      .toUpperCase();;
              txtName.setText(sName);
              mediaPlayer.start();
              playPauseBtn.setImageResource(R.drawable.ic_pause);

              startAnimation(imageView,360f);

              mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                  @Override
                  public void onCompletion(MediaPlayer mediaPlayer) {
                      nextBtn.performClick();
                  }
              });

              int audioID= mediaPlayer.getAudioSessionId();
              if(audioID!=-1){
                  barVisualizer.setAudioSessionId(audioID);
              }
            }
        });
        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    playerSeekBar.setProgress(0);
                }
                position=((position-1)<0)? (mySongs.size()-1): position-1;
                Uri u =Uri.parse(mySongs.get(position).toString());

                try{
                    mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                    mediaPlayer.prepare();
                    playerSeekBar.setMax(mediaPlayer.getDuration());
                    updateSeekBar.start();
                }catch (Exception e){
                    e.printStackTrace();
                }

                mediaPlayer.seekTo(0);
                txtEnd.setText(createTime(mediaPlayer.getDuration()));

                sName= mySongs.get(position).getName()
                        .replace(".mp3","")
                        .replace(".wav","")
                        .replace(".mp4a","")
                        .toUpperCase();
                txtName.setText(sName);

                mediaPlayer.start();
                playPauseBtn.setImageResource(R.drawable.ic_pause);
                startAnimation(imageView,-360f);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        nextBtn.performClick();
                    }
                });

                int audioID= mediaPlayer.getAudioSessionId();
                if(audioID!=-1){
                    barVisualizer.setAudioSessionId(audioID);
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                nextBtn.performClick();
            }
        });

        int audioID= mediaPlayer.getAudioSessionId();
        if(audioID!=-1){
            barVisualizer.setAudioSessionId(audioID);
        }

    }
    public void startAnimation(View view,float rotation){
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView,"rotation",0f,rotation);
        animator.setDuration(1000);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    public String createTime(int duration){
        String time="";
        int min =duration/1000/60;
        int sec=duration/1000%60;

        time+=min+":";
        if(sec<10){
            time+="0";
        }
        time+=sec;
        return time;
    }
}