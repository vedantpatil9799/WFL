package wayforlife.com.wfl;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

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

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import wayforlife.com.wfl.Modal_class.single_newsfeed_item;

public class UploadFeed_image extends AppCompatActivity {

    private ImageView imageView;
    private StorageReference mReference,reference;
    private Date date;
    private Button upload_image;
    private single_newsfeed_item feed;
    private Bitmap photo;
    private DatabaseReference database;
    private String imageId,up_date,poll_op1,poll_op2,userpolltext;
    private CheckBox includePoll;
    private Bitmap image;
    private EditText caption,pollText,polloption1,polloption2;
    private byte[] data;
    private ProgressBar bar;
    private View showProgress;
    private AlertDialog dialog;
    private FirebaseAuth auth;



    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_feed);
        imageView = findViewById(R.id.img_imageView);
        InitializeDialog();
        caption=findViewById(R.id.img_caption);
        includePoll=findViewById(R.id.img_include_poll);
        pollText=findViewById(R.id.img_poll_text);
        polloption1=findViewById(R.id.img_poll_option_1);
        polloption2=findViewById(R.id.img_poll_option_2);

        auth=FirebaseAuth.getInstance();
        mReference = FirebaseStorage.getInstance().getReference();                ///getting storage reference

        upload_image=findViewById(R.id.upload_feed_upload);




        imageId="image_"+new Date().getTime();                                            //getting imageid

        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");            //getting date
        date= Calendar.getInstance().getTime();
        up_date=sdf.format(date);

        Intent intent=getIntent();                                                   //getting uri from previous intent
        image=(Bitmap)intent.getParcelableExtra("image");
        imageView.setImageBitmap(image);

        database= FirebaseDatabase.getInstance().getReference();                       //Getting database reference
        reference = mReference.child("newsfeed/"+imageId+".jepg");

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,baos);
        data=baos.toByteArray();



        upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadData();

            }
        });

    }

    void uploadData() {

        final single_newsfeed_item item=new single_newsfeed_item();
        if(TextUtils.isEmpty(caption.getText().toString())) {
            Toast.makeText(UploadFeed_image.this, "Caption is Empty", Toast.LENGTH_SHORT).show();
            item.setUserCaption("NO_CAPTION");
        }
        else
        {
            item.setUserCaption(caption.getText().toString());
        }
        item.setUpvoteCount(0);
        item.setDownvoteCount(0);
        if(includePoll.isChecked()) {

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
                    item.setDate(up_date);
                    item.setName(auth.getUid());
                    item.setType(single_newsfeed_item.IMAGE_TYPE);
                    Toast.makeText(UploadFeed_image.this, "Upload Successful:", Toast.LENGTH_SHORT).show();
                    dialog.show();
                    reference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                                startActivity(new Intent(UploadFeed_image.this,MainActivity.class));
                            }
                        }
                    });
                }
                else
                {
                    Toast.makeText(UploadFeed_image.this, "Poll Options should not be empty", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(this, "Poll text should not be empty", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            item.setPoll(includePoll.isChecked());
            item.setDate(up_date);
            item.setName(auth.getUid());
            item.setType(single_newsfeed_item.IMAGE_TYPE);
            Toast.makeText(UploadFeed_image.this, "Upload Successful:", Toast.LENGTH_SHORT).show();
            dialog.show();
            reference.putBytes(data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
                        startActivity(new Intent(UploadFeed_image.this,MainActivity.class));
                    }
                }
            });
        }












    }


    private void InitializeDialog()
    {
        LayoutInflater inflater=this.getLayoutInflater();
        showProgress=inflater.inflate(R.layout.upload_progress,null);
        AlertDialog.Builder newEvent= new AlertDialog.Builder(this);
        newEvent.setTitle("Uploading Image");
        newEvent.setView(showProgress);
        newEvent.setCancelable(true);
        bar=showProgress.findViewById(R.id.progress);
        dialog=newEvent.create();
        dialog.setCanceledOnTouchOutside(false);

    }


}
