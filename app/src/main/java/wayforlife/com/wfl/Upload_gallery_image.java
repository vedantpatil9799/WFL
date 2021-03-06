package wayforlife.com.wfl;

import android.app.AlertDialog;
import android.content.Intent;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import wayforlife.com.wfl.Modal_class.single_newsfeed_item;

public class Upload_gallery_image extends AppCompatActivity {

    private ImageView image;
    private EditText caption,pollText,polloption1,polloption2;
    private Button cancel,upload;
    private StorageReference reference,ref;
    private DatabaseReference mReference;
    private Uri imageUri;
    private String imageId,up_date,poll_op1,poll_op2,userpolltext;
    private Date date;
    private CheckBox includePoll;
    private ProgressBar bar;
    private View showProgress;
    private AlertDialog dialog;
    private FirebaseAuth auth;
    private String userkey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitializeDialog();
        setContentView(R.layout.activity_upload_gallery_image);
        image=findViewById(R.id.gallery_selected_image);
        caption=findViewById(R.id.galleryimage_caption);
        cancel=findViewById(R.id.galleryimage_btncancel);
        upload=findViewById(R.id.galleryimage_btnupload);

        caption=findViewById(R.id.galleryimage_caption);
        includePoll=findViewById(R.id.gallery_img_include_poll);
        pollText=findViewById(R.id.gallery_img_poll_text);
        polloption1=findViewById(R.id.gallery_img_poll_option_1);
        polloption2=findViewById(R.id.gallery_imag_poll_option_2);

        reference= FirebaseStorage.getInstance().getReference();
        mReference= FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        Intent intent=getIntent();
        imageUri= Uri.parse(intent.getStringExtra("uri"));
        imageId="image_"+new Date().getTime();
        image.setImageURI(imageUri);


        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");
        date= Calendar.getInstance().getTime();//getting date
        up_date=sdf.format(date);

        ref=reference.child("newsfeed/"+imageId+".jpeg");

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();
                if(bar.getProgress()==100)
                {
                    dialog.dismiss();
                    startActivity(new Intent(Upload_gallery_image.this,MainActivity.class));
                }

            }
        });


    }

    public void uploadFile()
    {
        final single_newsfeed_item item = new single_newsfeed_item();
        if(TextUtils.isEmpty(caption.getText().toString())) {
            Toast.makeText(Upload_gallery_image.this, "Caption is Empty", Toast.LENGTH_SHORT).show();
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
                if (!TextUtils.isEmpty(polloption1.getText().toString()) && !TextUtils.isEmpty(polloption2.getText().toString())) {
                    item.setPollText(pollText.getText().toString());
                    item.setPoll_option1(polloption1.getText().toString());
                    item.setPoll_option2(polloption2.getText().toString());
                    item.setOp1_count(0);
                    item.setOp2_count(0);
                    item.setPoll(includePoll.isChecked());
                    item.setDate(up_date);
                    item.setName(auth.getUid());
                    item.setType(single_newsfeed_item.IMAGE_TYPE);
                    dialog.show();
                    ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    item.setUrl_or_text(uri.toString());
                                    mReference.child("newsfeed").push().child("feed").setValue(item);
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
                                startActivity(new Intent(Upload_gallery_image.this,MainActivity.class));
                            }

                        }
                    });


                } else {
                    Toast.makeText(Upload_gallery_image.this, "Poll Options should not be empty", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Toast.makeText(Upload_gallery_image.this, "Poll text should not be empty", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            item.setPoll(includePoll.isChecked());
            item.setDate(up_date);
            item.setName(auth.getUid());
            item.setType(single_newsfeed_item.IMAGE_TYPE);
            dialog.show();
            ref.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            item.setUrl_or_text(uri.toString());
                            mReference.child("newsfeed").push().child("feed").setValue(item);

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
                        startActivity(new Intent(Upload_gallery_image.this,MainActivity.class));
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
