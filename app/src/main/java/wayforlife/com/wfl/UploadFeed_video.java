package wayforlife.com.wfl;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import wayforlife.com.wfl.Modal_class.single_newsfeed_item;

public class UploadFeed_video extends AppCompatActivity {

    private EditText caption,pollText,polloption1,polloption2;
    private VideoView videoView;
    private Button btn_upload;
    private MediaController mMedia;
    private StorageReference mReference,reference;
    private String videoid,Upload_date;
    private Date date;
    private String up_date,userpolltext,poll_op1,poll_op2;
    private DatabaseReference database;
    private single_newsfeed_item feed;
    private Uri videoUri;
    private CheckBox includePoll;
    private ProgressBar bar;
    private View showProgress;
    private AlertDialog dialog;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_feed_video);
        InitializeDialog();
        date= Calendar.getInstance().getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        Upload_date =sdf.format(date);
        auth=FirebaseAuth.getInstance();

        videoView=findViewById(R.id.uploadFeed_video_videoview);
        btn_upload=findViewById(R.id.uploadFeed_video_up_button);

        includePoll=findViewById(R.id.uploadFeed_video_include_poll);
        pollText=findViewById(R.id.uploadFeed_video_poll_text);
        polloption1=findViewById(R.id.uploadFeed_video_poll_option_1);
        polloption2=findViewById(R.id.uploadFeed_video_poll_option_2);
        caption=findViewById(R.id.uploadFeed_video_caption);

        mReference= FirebaseStorage.getInstance().getReference();   //Getting Storage Reference
        videoid="video_"+date.getTime();                           //Creating Video Id
        reference=mReference.child("newsfeed/"+videoid+".mp4");     //Creating path reference to path
        database= FirebaseDatabase.getInstance().getReference();     //Getting database Reference



  //Getting date

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        mMedia=new MediaController(UploadFeed_video.this);
                        videoView.setMediaController(mMedia);
                        mMedia.setAnchorView(videoView);
                    }
                });
            }
        });

        Intent intent=getIntent();                      //Getting uri from intent
        videoUri=intent.getData();
        videoView.setVideoURI(videoUri);
        videoView.start();


        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            uploadData();

            }

        });



    }

    void uploadData() {
        final single_newsfeed_item item = new single_newsfeed_item();

        if(TextUtils.isEmpty(caption.getText().toString()))
        {
            Toast.makeText(UploadFeed_video.this, "Caption is Empty", Toast.LENGTH_SHORT).show();
            item.setUserCaption(caption.getText().toString());
        }
        else
        {
            item.setUserCaption(caption.getText().toString());
        }
        item.setUpvoteCount(0);
        item.setDownvoteCount(0);
        if(includePoll.isChecked())
        {

            if (!TextUtils.isEmpty(pollText.getText().toString()))
            {
                if (!TextUtils.isEmpty(polloption1.getText().toString()) && !TextUtils.isEmpty(polloption2.getText().toString()))
                {
                    item.setPollText(pollText.getText().toString());
                    item.setPoll_option1(polloption1.getText().toString());
                    item.setPoll_option2(polloption2.getText().toString());
                    item.setOp1_count(0);
                    item.setOp2_count(0);
                    item.setPoll(includePoll.isChecked());
                    item.setDate(Upload_date);
                    item.setName(auth.getUid());
                    item.setType(single_newsfeed_item.VIDEO_TYPE);
                    dialog.show();
                    reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                            reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setUrl_or_text(uri.toString());
                                    database.child("newsfeed").push().child("feed").setValue(item);
                                }

                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            bar.setProgress((int)progress);
                            if(bar.getProgress()==100)
                            {
                                dialog.dismiss();
                            }
                            startActivity(new Intent(UploadFeed_video.this,MainActivity.class));
                        }
                    });;
                } else {
                    Toast.makeText(UploadFeed_video.this, "Poll Options should not be empty", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                pollText.setError("Should Not be Empty");
            }
        }
        else
        {
            item.setPoll(includePoll.isChecked());
            item.setDate(Upload_date);
            item.setName(auth.getUid());
            item.setType(single_newsfeed_item.VIDEO_TYPE);
            dialog.show();
            reference.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                    reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setUrl_or_text(uri.toString());
                            database.child("newsfeed").push().child("feed").setValue(item);
                        }

                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    bar.setProgress((int)progress);
                    if(bar.getProgress()==100)
                    {
                        dialog.dismiss();
                    }
                    startActivity(new Intent(UploadFeed_video.this,MainActivity.class));
                }
            });;
        }

    }

    private void InitializeDialog()
    {
        LayoutInflater inflater=this.getLayoutInflater();
        showProgress=inflater.inflate(R.layout.upload_progress,null);
        AlertDialog.Builder newEvent= new AlertDialog.Builder(this);
        newEvent.setTitle("Uploading Video");
        newEvent.setView(showProgress);
        newEvent.setCancelable(true);
        bar=showProgress.findViewById(R.id.progress);
        dialog=newEvent.create();
        dialog.setCanceledOnTouchOutside(false);

    }


}
