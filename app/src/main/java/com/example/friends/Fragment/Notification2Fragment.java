package com.example.friends.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.friends.Adaptor.NotificationAdaptor;
import com.example.friends.Model.NotificationModel;
import com.example.friends.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class Notification2Fragment extends Fragment {

     RecyclerView recyclerView;
     ArrayList<NotificationModel>list;
     FirebaseDatabase database;
     FirebaseAuth auth;

    public Notification2Fragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_notification2, container, false);

        recyclerView=view.findViewById(R.id.notification2RV);
        list=new ArrayList<>();

        NotificationAdaptor adaptor=new NotificationAdaptor(list,getContext());
        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adaptor);

       database.getReference()
               .child("notification")
               .child(FirebaseAuth.getInstance().getUid())
               .addValueEventListener(new ValueEventListener() {
                   @Override
                   public void onDataChange(@NonNull DataSnapshot snapshot) {
                       list.clear();
                       for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                           NotificationModel notification=dataSnapshot.getValue(NotificationModel.class);
                           notification.setNotificationID(dataSnapshot.getKey());
                           list.add(notification);
                       }
                       adaptor.notifyDataSetChanged();
                   }

                   @Override
                   public void onCancelled(@NonNull DatabaseError error) {

                   }
               });

        return view;
    }
}