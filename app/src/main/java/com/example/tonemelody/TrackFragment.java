package com.example.tonemelody;

import static android.os.Environment.getExternalStorageDirectory;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class TrackFragment extends Fragment {
    ListView listView;
    String[] songs;
    ArrayList<File> musics;
    int position=0;

    ImageView playPauseBtn,nextBtn,prevBtn,fastForwardBtn,fastRewindBtn, imageView, zoom;
    TextView txtEnd,txtStart;
    SeekBar playerSeekBar;

    DataBaseHelper DB;
    Cursor cursor;
    ArrayList<String> favourite;

    static MediaPlayer mediaPlayer;

    private TrackFragment(){ }

    public static TrackFragment newInstance() { return  new TrackFragment(); }
    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_track, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = Objects.requireNonNull(getView()).findViewById(R.id.musicListView);

        playPauseBtn =getView().findViewById(R.id.playPauseBtn);
        nextBtn =getView().findViewById(R.id.nextBtn);
        prevBtn =getView().findViewById(R.id.prevBtn);
        fastRewindBtn =getView().findViewById(R.id.fastRewindBtn);
        fastForwardBtn =getView().findViewById(R.id.fastForwardBtn);

        zoom = getView().findViewById(R.id.zoom);

        imageView= getView().findViewById(R.id.imageView);

        txtEnd=getView().findViewById(R.id.endTime);
        txtStart=getView().findViewById(R.id.startTime);

        playerSeekBar =getView().findViewById(R.id.playerSeekBar);

        DB = new DataBaseHelper(getContext());

        cursor = DB.getData();
        favourite = new ArrayList<>();

        if(cursor!=null){
            while (cursor.moveToNext()){
                favourite.add(cursor.getString(0));
            }
        cursor.close();
        }

        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        displaySong();

        Uri uri = Uri.parse(musics.get(position).toString());

        try {
            mediaPlayer = MediaPlayer.create(getContext(),uri);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }

        playerSeekBar.setMax(mediaPlayer.getDuration());

        playerSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) { }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }
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

        final Handler updateSeekBar= new Handler();
        updateSeekBar.postDelayed(new Runnable() {
            @Override
            public void run() {
                playerSeekBar.setMax(mediaPlayer.getDuration());
                playerSeekBar.setProgress(mediaPlayer.getCurrentPosition());
                updateSeekBar.postDelayed(this,500);
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

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        nextBtn.performClick();
                    }
                });
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer!=null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                position=(position<musics.size())?position+1:0;

                Uri u =Uri.parse(musics.get(position).toString());

                try{
                    mediaPlayer=MediaPlayer.create(getContext(),u);
                    mediaPlayer.prepare();
                    playerSeekBar.setMax(mediaPlayer.getDuration());
                }catch (Exception e){
                    e.printStackTrace();
                }
                mediaPlayer.seekTo(0);
                txtEnd.setText(createTime(mediaPlayer.getDuration()));

                mediaPlayer.start();

                playPauseBtn.setImageResource(R.drawable.ic_pause);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        nextBtn.performClick();
                    }
                });
            }});

        prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                }
                position = ((position - 1) < 0) ? (musics.size() - 1) : position - 1;
                Uri u = Uri.parse(musics.get(position).toString());

                try {
                    mediaPlayer = MediaPlayer.create(getContext(), u);
                    mediaPlayer.prepare();
                    playerSeekBar.setMax(mediaPlayer.getDuration());
                } catch (Exception e) {
                    e.printStackTrace();
                }

                mediaPlayer.seekTo(0);
                txtEnd.setText(createTime(mediaPlayer.getDuration()));

                mediaPlayer.start();

                playPauseBtn.setImageResource(R.drawable.ic_pause);

                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        nextBtn.performClick();
                    }
                });
            }});

        zoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();

                startActivity(new Intent(getContext(),PlayerActivity.class)
                        .putExtra("songs",musics)
                        .putExtra("pos",position));
            }
        });
    }
    public void  displaySong(){
        try {
            musics = findMusics(getExternalStorageDirectory());
            songs = new String[musics.size()];

            for (int i = 0; i < musics.size(); i++) {
                songs[i] = musics.get(i).getName().replace(".mp3","").replace(".wav","").replace(".mp4a","").toUpperCase();
            }
            CustomAdapter customAdapter = new CustomAdapter();
            listView.setAdapter(customAdapter);
        }catch (NullPointerException e){
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    @NonNull
    private ArrayList<File> findMusics(File file){
        ArrayList<File> musicFiles = new ArrayList<>();
        File[] files =file.listFiles();

        if(files!=null) {
            for (File currentFiles : files) {
                if (currentFiles.isDirectory() && !currentFiles.isHidden()) {
                    musicFiles.addAll(findMusics(currentFiles));
                }else {
                    if (currentFiles.getName().endsWith(".mp3") || currentFiles.getName().endsWith(".wav") || currentFiles.getName().endsWith(".mp4a")) {
                        musicFiles.add(currentFiles);
                    }
                }
                }
            }
        return musicFiles;
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
//   Inner Custom Adapter Class
    private class  CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return songs.length;
        }
        @Override
        public Object getItem(int i) {
            return null;
        }
        @Override
        public long getItemId(int i) {
            return 0;
        }
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            @SuppressLint({"ViewHolder", "InflateParams"}) View myView = getLayoutInflater().inflate(R.layout.list_items,null);
            TextView txtSong = myView.findViewById(R.id.txtSong);
            txtSong.setSelected(true);
            txtSong.setText(songs[i]);
            ImageView favoriteBtn = myView.findViewById(R.id.favouriteBtn);
            ImageView addPlayBtn =myView.findViewById(R.id.addBtn);

            addPlayBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(DB.insertPlaylist(songs[i],"Recently Added",musics.get(i).toString()))
                        Toast.makeText(getContext(), "Added to Playlist", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), "Already Added", Toast.LENGTH_SHORT).show();
                }
            });

            if(favourite.contains(songs[i])){
                favoriteBtn.setImageResource(R.drawable.ic_favorite_filled);
            }

            ImageView playBtn = myView.findViewById(R.id.playBtn);
            playBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = i-1;
                    nextBtn.performClick();
                }
            });

            favoriteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!DB.insertFavourite(songs[i],musics.get(i).toString())){
                        DB.deleteFavourite(songs[i]);
                        favoriteBtn.setImageResource(R.drawable.ic_favorite);
                        Toast.makeText(getContext(), "Removed From Favourite", Toast.LENGTH_SHORT).show();
                    }else {
                        favoriteBtn.setImageResource(R.drawable.ic_favorite_filled);
                        Toast.makeText(getContext(), "Added to Favourite", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            return myView;
        }
    }
}