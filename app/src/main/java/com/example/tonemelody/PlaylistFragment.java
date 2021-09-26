package com.example.tonemelody;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class PlaylistFragment extends Fragment {
    ListView listView;
    DataBaseHelper DB;
    Cursor cursor;
    ArrayList<String> name,url,desc;

    ImageView delete,update;
    TextView txtSong,descSong;

    public PlaylistFragment() {
        // Required empty public constructor
    }
    public static PlaylistFragment newInstance() {
        return new PlaylistFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_playlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = Objects.requireNonNull(getView()).findViewById(R.id.playListView);
        DB = new DataBaseHelper(getActivity());

        cursor = DB.getPlaylistData();
        name = new ArrayList<>();
        url = new ArrayList<>();
        desc = new ArrayList<>();


        if(cursor!=null){
            while (cursor.moveToNext()){
                name.add(cursor.getString(0));
                desc.add(cursor.getString(1));
                url.add(cursor.getString(2));
            }
            cursor.close();
        }

        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);
    }

    //   Inner Custom Adapter Class
    private class  CustomAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return name.size();
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
            @SuppressLint("ViewHolder") View myView = getLayoutInflater().inflate(R.layout.list_playlist,null);
            txtSong = myView.findViewById(R.id.txtSong);
            descSong =myView.findViewById(R.id.txtDesc);
            delete = myView.findViewById(R.id.removePlaylist);
            update= myView.findViewById(R.id.updatePlaylist);
            txtSong.setSelected(true);

            txtSong.setText(name.get(i));
            descSong.setText(desc.get(i));




            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are Your Sure to Remove?");
                    builder.setIcon(R.drawable.ic_delete);
                    builder.setCancelable(true);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int j) {
                            DB.deletePlaylist(name.get(i));
                            name.remove(i);
                            url.remove(i);
                            desc.remove(i);
                            notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("No", null);
                    AlertDialog alert =builder.create();
                    alert.show();

                }
            });







            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText editText = new EditText(getActivity());
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are You Sure to update?");
                    builder.setIcon(R.drawable.ic_delete);
                    builder.setCancelable(true);
                    builder.setView(editText);

                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int j) {
                            DB.updatePlaylist(name.get(i),editText.getText().toString() ,url.get(i));
                            notifyDataSetChanged();
                        }
                    });
                    builder.setNegativeButton("No",null);

                    AlertDialog alert = builder.create();
                    alert.show();

                }
            });
            return myView;
        }
    }
}