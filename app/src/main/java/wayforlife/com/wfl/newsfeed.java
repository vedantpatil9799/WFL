package wayforlife.com.wfl;


import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Collections;

import wayforlife.com.wfl.Adapter_class.newsFeedAdapter;
import wayforlife.com.wfl.Modal_class.single_newsfeed_item;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.widget.Toast.LENGTH_SHORT;


/**
 * A simple {@link Fragment} subclass.
 */
public class newsfeed extends Fragment {

    private FloatingActionButton fab_upload_feed,upload_photo,upload_gallery,upload_video,write_text;
    private Animation fabOpen,fabClose,rotateForward,rotateBackword;
    private boolean isOpen=false;
    private Intent intent;
    private final static int GALLERY=1,CAMERA=2,VIDEO=3;
    private DatabaseReference reference;
    private StorageReference storageReference;
    private ArrayList<single_newsfeed_item> list,templist;
    private ArrayList<String> list2;
    private newsFeedAdapter adapter;
    private RecyclerView newsfeedView;
    private ArrayList<String> keylist,tempkeylist,userprofile;
    private AlertDialog.Builder fetchp;
    private AlertDialog dialog;
    private FirebaseAuth auth;
    private String userkey;



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CAMERA && resultCode==RESULT_OK)
        {
            Bitmap photo=(Bitmap)data.getExtras().get("data");
            //Bitmap photo=(Bitmap)data.getExtras().get("data");

            intent=new Intent(getContext(),UploadFeed_image.class);
            intent.putExtra("image",photo);
            intent.putExtra("userkey",userkey);
            startActivity(intent);
        }
        else if(requestCode==VIDEO && resultCode==RESULT_OK)
        {
            Uri videoUri=data.getData();
            Toast.makeText(getContext(), "video Saved to "+videoUri, LENGTH_SHORT).show();
            intent=new Intent(getContext(),UploadFeed_video.class);
            intent.putExtra("userkey",userkey);
            intent.setData(videoUri);
            startActivity(intent);
        }
        else if(requestCode==GALLERY && resultCode==RESULT_OK)
        {
                Uri selectMedia=data.getData();
            ContentResolver cr=getContext().getContentResolver();
            String mime=cr.getType(selectMedia);

            if(mime.toLowerCase().contains("video"))
            {
                Toast.makeText(getContext(), "Video Selected", LENGTH_SHORT).show();

            }
            else if(mime.toLowerCase().contains("image"))
            {
               Uri imageuri=data.getData();
               intent=new Intent(getContext(),Upload_gallery_image.class);
               intent.putExtra("userkey",userkey);
               intent.putExtra("uri",imageuri.toString());
               startActivity(intent);

            }
        }
        else if(resultCode==RESULT_CANCELED)
        {

        }
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view= inflater.inflate(R.layout.fragment_newsfeed, container, false);
        keylist=new ArrayList<>();
        list=new ArrayList<single_newsfeed_item>();
        templist=new ArrayList<single_newsfeed_item>();
        tempkeylist=new ArrayList<>();
        userprofile=new ArrayList<>( );
        list2=new ArrayList<>();
        auth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();
        fab_upload_feed=view.findViewById(R.id.frag_newsfeed_fab);
        upload_photo=view.findViewById(R.id.frag_newsfeed_fab_addPhoto);
        upload_gallery=view.findViewById(R.id.frag_newsfeed_fab_SelectFromGallery);
        upload_video=view.findViewById(R.id.frag_newsfeed_fab_video);
        write_text=view.findViewById(R.id.frag_newsfeed_fab_writeYourOwn);

        fabOpen= AnimationUtils.loadAnimation(getContext(),R.anim.fab_open);
        fabClose= AnimationUtils.loadAnimation(getContext(),R.anim.fab_close);
        rotateForward= AnimationUtils.loadAnimation(getContext(),R.anim.rotate_forward);
        rotateBackword= AnimationUtils.loadAnimation(getContext(),R.anim.rotate_backward);



        newsfeedView=view.findViewById(R.id.recyclerview_newsfeed);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity());
        newsfeedView.setLayoutManager(layoutManager);

        getData();

        adapter=new newsFeedAdapter(list,getContext(),keylist,list2,userprofile);
        newsfeedView.setAdapter(adapter);

        fab_upload_feed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animateFab();

            }
        });

        upload_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(intent, CAMERA);
                }
                else
                {
                    Toast.makeText(getActivity(), "Permission for Camera is not Granted", Toast.LENGTH_SHORT).show();
                }
            }
        });

        upload_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateFab();
                intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                String[] mimeTypes={"image/*","video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES,mimeTypes);
                if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(Intent.createChooser(intent, "SELECT MEDIA"), GALLERY);
                }
                else {
                    Toast.makeText(getActivity(), "Permission not Granted for reading media", Toast.LENGTH_SHORT).show();
                }
                }
        });

        upload_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    startActivityForResult(intent, VIDEO);
                } else {
                    Toast.makeText(getActivity(), "Permission not granted for Camera", Toast.LENGTH_SHORT).show();
                }
            }
        });


        write_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent=new Intent(getContext(),Write_OwnText.class);
                intent.putExtra("userkey",userkey);
                startActivity(intent);
            }
        });
        return view;
    }





    protected void getData()
    {



        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                list.clear();
                list2.clear();
                keylist.clear();
                userprofile.clear();
                        DataSnapshot data= dataSnapshot.child("newsfeed");

                   Iterable<DataSnapshot> it=data.getChildren();
                    for(DataSnapshot d: it)
                    {
                        keylist.add(d.getKey());
                        single_newsfeed_item item=d.child("feed").getValue(single_newsfeed_item.class);
                        list.add(item);

                            if(d.child("pollVotedUser").child(auth.getUid()).exists())
                                list2.add("true");
                            else
                                list2.add("false");


                    }

                    DataSnapshot data1=dataSnapshot.child("User-details");
                    Iterable<DataSnapshot> it1=data1.getChildren();
                    for(single_newsfeed_item item : list)
                    {
                        String key=item.getName();
                        item.setName(dataSnapshot.child("User-details").child(key).child("user_id").getValue().toString());
                        userprofile.add(dataSnapshot.child("User-details").child(key).child("user_photo").getValue().toString());
                    }
                Collections.reverse(list);
                Collections.reverse(list2);
                Collections.reverse(userprofile);
                Collections.reverse(keylist);
                    adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    private void animateFab()
    {
        if(isOpen)
        {
            fab_upload_feed.startAnimation(rotateBackword);
            upload_photo.startAnimation(fabClose);
            upload_gallery.startAnimation(fabClose);
            upload_video.startAnimation(fabClose);
            write_text.startAnimation(fabClose);
            write_text.setClickable(false);
            upload_photo.setClickable(false);
            upload_gallery.setClickable(false);
            upload_video.setClickable(false);
            isOpen=false;
        }
        else
        {
            fab_upload_feed.startAnimation(rotateForward);
            upload_photo.startAnimation(fabOpen);
            upload_gallery.startAnimation(fabOpen);
            upload_video.startAnimation(fabOpen);
            write_text.startAnimation(fabOpen);
            write_text.setClickable(true);
            upload_photo.setClickable(true);
            upload_gallery.setClickable(true);
            upload_video.setClickable(true);
            isOpen=true;
        }
    }

}
