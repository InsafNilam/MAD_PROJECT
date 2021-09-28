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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

public class FavouriteFragment extends Fragment {
    ListView listView;
    DataBaseHelper DB;
    Cursor cursor;
    ImageView removeFavourite;
    TextView txtSong;
    ArrayList<String> name;

    private FavouriteFragment() {
        // Required empty public constructor
    }

    public static FavouriteFragment newInstance() {
        return new FavouriteFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favourite, container, false);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = Objects.requireNonNull(getView()).findViewById(R.id.favouriteListView);
        DB = new DataBaseHelper(getActivity());
        cursor = DB.getData();
        name= new ArrayList<>();

        if(cursor != null){
            while (cursor.moveToNext()){
                name.add(cursor.getString(0));
            }
            cursor.close();
        }

        CustomAdapter customAdapter = new CustomAdapter();
        listView.setAdapter(customAdapter);

//       listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//           @Override
//           public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//               cursor.moveToPosition(i);
//               DB.deleteFavourite(cursor.getString(0));
//               cursor = DB.getData();
//           }
//       });
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
            @SuppressLint("ViewHolder") View myView = getLayoutInflater().inflate(R.layout.list_favourite,null);
            txtSong = myView.findViewById(R.id.txtSong);
            removeFavourite = myView.findViewById(R.id.removefavourite);
            txtSong.setSelected(true);
            txtSong.setText(name.get(i));

            removeFavourite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Confirmation");
                    builder.setMessage("Are Your Sure to Remove From Favourites?");
                    builder.setIcon(R.drawable.ic_trash);
                    builder.setCancelable(true);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            DB.deleteFavourite(name.get(i));
                            name.remove(i);
                            notifyDataSetChanged();
                            Toast.makeText(getContext(), "Successfully Removed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNegativeButton("No", null);
                    AlertDialog alert =builder.create();
                    alert.show();
                }
            });
            return myView;
        }
    }

}