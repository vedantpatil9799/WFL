package wayforlife.com.wfl;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import wayforlife.com.wfl.Modal_class.Store_details_modal_class;
import wayforlife.com.wfl.Modal_class.Store_media_modal_class;

public class Registration extends AppCompatActivity implements View.OnClickListener{

    private EditText e1,e2,e3,e4,pin;
    private Button b1,cam,gal,cancel,ad_btn;
    private ImageView profile_image;
    private String name,email,phone,user_id,download_url;
    private Boolean f1,f2,f3,valid=true;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference,sample,media,admin;
    private StorageReference storageReference;
    private FirebaseUser user;
    private final static int PICK_IMAGE_REQUEST=70,CAMERA_REQUEST_CODE=1;
    private Intent intent;
    private ProgressDialog pd,pd_id;
    private Bitmap bmp;
    private byte[] bitmap_data;
    private CircleImageView pro_img;
    private String[] users={"Admin","Normal user","cancel"};
    private PopupWindow pin_authentication;
    private RelativeLayout relativeLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        relativeLayout=findViewById(R.id.registration);
       phone=getIntent().getExtras().getString("phno");
     //   phone="8329717028";
        Toast.makeText(this, ""+phone, Toast.LENGTH_SHORT).show();
        Log.d("errorrrrrr....",phone);
        profile_image= findViewById(R.id.upload_profile_img);
        pro_img=(CircleImageView)findViewById(R.id.profile_img);
        e1=findViewById(R.id.edit_phone_no);            //phone number
        e2=findViewById(R.id.edit_email_id);        //email-id
        e3=findViewById(R.id.edit_user_id);         //user-id
        e4=findViewById(R.id.edit_name);            //name

        b1=findViewById(R.id.btn_register);

        b1.setOnClickListener(this);
        profile_image.setOnClickListener(this);
        try {
            e1.setText(phone);
        }
        catch (Exception e)
        {
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
            Log.d("errorrrrr...",""+e);
        }

        databaseReference= FirebaseDatabase.getInstance().getReference();
        admin=databaseReference;
        media=databaseReference;
        storageReference= FirebaseStorage.getInstance().getReference();
        pd=new ProgressDialog(this);
        pd.setMessage("Please wait.....");
        pd_id=new ProgressDialog(this);
        pd_id.setTitle("Please wait.....");
        pd_id.setMessage("Checking user-id.....");
    }

    @Override
    public void onClick(View v)
    {
        if(v==b1)
        {
            name=e4.getText().toString().trim();
            user_id=e3.getText().toString().trim();
            email=e2.getText().toString().trim();
            if(user_id.isEmpty())
            {
                e3.setError("Compulsory Field");
                f2=true;
            }
            else
            {
                f2 = false;
                pd_id.show();
                sample = databaseReference.child("user-id");
                sample.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            try {
                                String id = dataSnapshot1.getValue(String.class);
                                if (user_id.equals(id)) {
                                    valid=false;
                                    break;
                                }
                                else
                                {
                                    valid=true;
                                }
                            } catch (Exception e) {
                                Toast.makeText(Registration.this, "Error:- " + e, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                if(valid)
                {
                    Toast.makeText(Registration.this, "User-id not taken,Proceed ahead.", Toast.LENGTH_SHORT).show();
                    databaseReference.child("user-id").child(user.getUid()).setValue(user_id);
                    pd_id.dismiss();
                    checkOtherField(f2);
                }
                else
                {
                    Toast.makeText(Registration.this, "User-id already taken,Please enter another one.", Toast.LENGTH_SHORT).show();
                    e3.setError("User-id already taken.");
                    pd_id.dismiss();
                }
            }
        }
        if(v==profile_image)
        {
            LayoutInflater layoutInflater=(LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
            View show_option=layoutInflater.inflate(R.layout.show_select_option,null);
            cam=show_option.findViewById(R.id.btn_select_camera);
            gal=show_option.findViewById(R.id.btn_select_gallery);
            cancel=show_option.findViewById(R.id.btn_cancel_select);
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setView(show_option);
            final AlertDialog dialog=builder.create();
            dialog.setCancelable(false);
            dialog.show();
            cam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Registration.this, "Camera Selected.", Toast.LENGTH_SHORT).show();
                    open_cammera();
                    dialog.dismiss();
                }
            });
            gal.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(Registration.this, "Gallery Selected.", Toast.LENGTH_SHORT).show();
                    open_gallery();
                    dialog.dismiss();
                }
            });
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
    }
    public void checkOtherField(boolean f2)
    {
        if(name.isEmpty())
        {
            e4.setError("Compulsory Field");
            f1=true;
        }
        else
        {
            f1=false;
        }
        if(email.isEmpty())
        {
            e2.setError("Compulsory Field");
            f3=true;
        }
        else
        {
            f3=false;
        }
        uploadDetails(f1,f2,f3);
    }
    public void uploadDetails(boolean f1,boolean f2,boolean f3)
    {
        if(!f1 && !f2 && !f3)
        {
            AlertDialog.Builder ask=new AlertDialog.Builder(this);
            ask.setCancelable(false)
                    .setTitle("Register as.....")
                    .setItems(users, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String selected=users[which];
                            if(selected.equals("Admin"))
                            {
                                dialog.dismiss();
                                LayoutInflater layoutInflater=(LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                                View pop=layoutInflater.inflate(R.layout.check_admin,null);
                                pin=pop.findViewById(R.id.edit_pin);
                                ad_btn=pop.findViewById(R.id.admin_done);

                                AlertDialog.Builder check_admin=new AlertDialog.Builder(Registration.this);
                                check_admin.setView(pop)
                                        .setCancelable(false);
                                final AlertDialog check_ad=check_admin.create();
                                check_ad.show();
                             //   pin_authentication=new PopupWindow(pop, RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,false);
                             //   pin_authentication.showAtLocation(relativeLayout, Gravity.CENTER,0,0);

                                ad_btn.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if(pin.getText().toString().trim().isEmpty())
                                        {
                                            pin.setError("pls provide the pin");
                                        }
                                        else
                                        {
                                            final String ad_pin=pin.getText().toString().trim();
                                            admin.child("admin_pin").addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Double fetch_pin=dataSnapshot.getValue(Double.class);
                                                    int f_pin=fetch_pin.intValue();
                                              //      Toast.makeText(Registration.this, ""+fetch_pin, Toast.LENGTH_SHORT).show();
                                                    String wfl_pin=Integer.toString(f_pin);
                                                    if(ad_pin.equals(wfl_pin))
                                                    {
                                                        check_ad.dismiss();
                                                        Store_details_modal_class obj=new Store_details_modal_class(name,user_id,phone,email,download_url,"admin","yes");
                                                        databaseReference.child("User-details").child(user.getUid()).setValue(obj)
                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        Toast.makeText(Registration.this, "Registered Successfully.", Toast.LENGTH_SHORT).show();
                                                                        startActivity(new Intent(Registration.this,MainActivity.class));
                                                                        finish();
                                                                    }
                                                                }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(Registration.this, "Something went wrong,please try again later.", Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                                    }
                                                    else
                                                    {
                                                        Toast.makeText(Registration.this, "Invalid PIN", Toast.LENGTH_SHORT).show();
                                                        pin.setError("PIN not valid");
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                });
                            }
                            else if(selected.equals("Normal user"))
                            {
                                if(download_url!=null)
                                {
                                    Store_details_modal_class obj=new Store_details_modal_class(name,user_id,phone,email,download_url,"normal","yes");
                                    databaseReference.child("User-details").child(user.getUid()).setValue(obj)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(Registration.this, "Registered Successfully.", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Registration.this,MainActivity.class));
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Registration.this, "Something went wrong,please try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                                else{
                                    Store_details_modal_class obj=new Store_details_modal_class(name,user_id,phone,email,"none","normal","yes");
                                    databaseReference.child("User-details").child(user.getUid()).setValue(obj)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(Registration.this, "Registered Successfully.", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(Registration.this,MainActivity.class));
                                                    finish();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Registration.this, "Something went wrong,please try again later.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                            else if(selected.equals("Close"))
                            {
                                dialog.dismiss();
                            }
                        }
                    })
                    .show();
        }
    }
    private void open_cammera()
    {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        }
        else
        {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(intent, CAMERA_REQUEST_CODE);

        }
    }
    private void open_gallery()
    {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
                Uri uri = data.getData();
                pro_img.setImageURI(uri);
                pd.show();
                if (uri == null)
                {
                    Toast.makeText(this, "Please select the image", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                else {
                    user=mAuth.getCurrentUser();
                    StorageReference filepath = storageReference.child("profile_images").child(user.getUid()).child("profile");
                    filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            try {
                                download_url = taskSnapshot.getDownloadUrl().toString().trim();
                                pd.dismiss();
                                Toast.makeText(Registration.this, "IMAGE UPLOAD DONE", Toast.LENGTH_SHORT).show();
                                Store_media_modal_class obj=new Store_media_modal_class(download_url);
                                media.child("Media").child(user.getUid()).child(getCurrentTime()).setValue(obj);
                            } catch (Exception e) {
                                Toast.makeText(Registration.this, "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.w("hi", "" + e);
                            }
                        }
                    });
                }
            }
            if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
                Bundle bundle = data.getExtras();
                bmp = (Bitmap) bundle.get("data");
                Toast.makeText(Registration.this, "" + bmp, Toast.LENGTH_SHORT).show();
                pd.show();
                pro_img.setImageBitmap(bmp);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                bitmap_data = baos.toByteArray();
                StorageReference filepath = storageReference.child("profile_images").child(user.getUid()).child("profile");
                filepath.putBytes(bitmap_data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        download_url = taskSnapshot.getDownloadUrl().toString().trim();
                        pd.dismiss();
                        Toast.makeText(Registration.this, "IMAGE UPLOAD DONE", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(Registration.this, "" + e, Toast.LENGTH_LONG).show();
        }
    }
    public String getCurrentTime()
    {
        Date date = new Date();
        String strDateFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }
}
