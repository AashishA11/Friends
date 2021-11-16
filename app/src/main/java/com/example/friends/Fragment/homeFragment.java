package com.example.friends.Fragment;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cooltechworks.views.shimmer.ShimmerRecyclerView;
import com.example.friends.Adaptor.PostAdapter;
import com.example.friends.Adaptor.StoryAdaptor;
import com.example.friends.Model.PostModel;
import com.example.friends.Model.StroyModel;
import com.example.friends.Model.UserStories;
import com.example.friends.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Date;


public class homeFragment extends Fragment {


    RecyclerView stroyRv;
    ShimmerRecyclerView dashboardRV;
    ArrayList<StroyModel>storyList;
    ArrayList<PostModel>postList;
    FirebaseAuth auth;
    FirebaseStorage storage;
    FirebaseDatabase database;
    ProgressDialog dialog;
    RoundedImageView addStoryImage;
    ActivityResultLauncher<String> galleyLauncher; 

    public homeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        storage=FirebaseStorage.getInstance();
        database=FirebaseDatabase.getInstance();
        dialog =new ProgressDialog(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        dashboardRV=view.findViewById(R.id.dashboardRv);
        dashboardRV.showShimmerAdapter();

        stroyRv=view.findViewById(R.id.storyRV);
        storyList=new ArrayList<>();
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setTitle("Story Uploading");
        dialog.setMessage("Please wail.....");
        dialog.setCancelable(false);

        StoryAdaptor adaptor=new StoryAdaptor(storyList,getContext());
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext(),LinearLayoutManager.HORIZONTAL,false);
        stroyRv.setLayoutManager(linearLayoutManager);
        stroyRv.setNestedScrollingEnabled(false);
        stroyRv.setAdapter(adaptor);

        database.getReference().child("stories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    storyList.clear();
                    for(DataSnapshot storySnapshot:snapshot.getChildren()){
                        StroyModel story=new StroyModel();
                        story.setStoryBy(storySnapshot.getKey());
                        story.setStoryAt(storySnapshot.child("postedBy").getValue(Long.class));

                        ArrayList<UserStories>stories=new ArrayList<>();
                        for (DataSnapshot snapshot1:storySnapshot.child("userStories").getChildren()) {
                            UserStories userStories = snapshot1.getValue(UserStories.class);
                            stories.add(userStories);
                        }
                        story.setStories(stories);
                        storyList.add(story);
                    }
                    adaptor.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

         postList=new ArrayList<>();


        PostAdapter postAdapter =new PostAdapter(postList,getContext());


        LinearLayoutManager layoutManager=new LinearLayoutManager(getContext());
        dashboardRV.setLayoutManager(layoutManager);
        dashboardRV.setNestedScrollingEnabled(false);

        database.getReference().child("posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    PostModel post=dataSnapshot.getValue(PostModel.class);

                    post.setPostId(dataSnapshot.getKey());
                    postList.add(post);
                }
                dashboardRV.setAdapter(postAdapter);
                dashboardRV.hideShimmerAdapter();
               postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
          addStoryImage=view.findViewById(R.id.stroyImg);
          addStoryImage.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
               galleyLauncher.launch("image/*");
              }
          });
          galleyLauncher=registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
              @Override
              public void onActivityResult(Uri result) {
                  addStoryImage.setImageURI(result);
                  dialog.show();
                  final StorageReference reference=storage.getReference()
                          .child("stories")
                          .child(FirebaseAuth.getInstance().getUid())
                          .child(new Date().getTime()+"");
                  reference.putFile(result).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                      @Override
                      public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                          reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                              @Override
                              public void onSuccess(Uri uri) {
                                  StroyModel story=new StroyModel();
                                  story.setStoryAt(new Date().getTime());
                                  database.getReference()
                                          .child("stories")
                                          .child(FirebaseAuth.getInstance().getUid())
                                          .child("postedBy")
                                          .setValue(story.getStoryAt()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                      @Override
                                      public void onSuccess(Void unused) {
                                          UserStories stories=new UserStories(uri.toString(),story.getStoryAt());

                                          database.getReference()
                                                  .child("stories")
                                                  .child(FirebaseAuth.getInstance().getUid())
                                                  .child("userStories")
                                                  .push()
                                                  .setValue(stories).addOnSuccessListener(new OnSuccessListener<Void>() {
                                              @Override
                                              public void onSuccess(Void unused) {
                                                  dialog.dismiss();
                                              }
                                          });
                                      }
                                  });
                              }
                          });
                      }
                  });

              }
          });
         return view;
    }
}