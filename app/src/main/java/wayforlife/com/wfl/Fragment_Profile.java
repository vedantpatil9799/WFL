package wayforlife.com.wfl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wayforlife.com.wfl.Modal_class.Store_details_modal_class;
import wayforlife.com.wfl.Modal_class.Store_media_modal_class;
import wayforlife.com.wfl.Profile_tab_fragments.FirstFragment;
import wayforlife.com.wfl.Profile_tab_fragments.ThirdFragment;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class Fragment_Profile extends Fragment {

    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference,sample,media;
    private Button b_edit,cam,gal,cancel,new_detail_save,new_detail_discard;
    private TextView txt_nm,txt_email,txt_phno,txt_uid;
    private CircleImageView c_pro_img;
    private ProgressDialog pd_wait;
    private TabLayout tabLayout;
    public ViewPager viewPager;
    private LayoutInflater layoutInflater;
    private String profile_uri;
    private FloatingActionButton edit,delete,close;
    private StorageReference storageReference;
    private final static int PICK_IMAGE_REQUEST=70,CAMERA_REQUEST_CODE=1;
    private Bitmap bmp;
    private byte[] bitmap_data;
    private ProgressDialog pd;
    private String download_url,u_name,u_email_id;
    private Boolean flag_nm,flag_email;
    private EditText e_nm,e_emid;

    public Fragment_Profile() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        final ProgressDialog loading=new ProgressDialog(getActivity());
        loading.setTitle("Please wait..");
        loading.setMessage("Loading your profile..");
        loading.setCancelable(false);
        loading.show();
        databaseReference.child("User-details").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Store_details_modal_class obj=dataSnapshot.getValue(Store_details_modal_class.class);

                txt_email.setText(obj.user_email_id);
                txt_nm.setText(obj.user_name);
                txt_phno.setText(obj.user_phone_no);
                String uid="@"+obj.user_id;
                txt_uid.setText(uid);
                if(obj.user_photo.equals("none"))
                {
                    Picasso.with(getContext()).load(R.drawable.default_profile).into(c_pro_img);
                }
                else {
                    Picasso.with(getContext()).load(obj.user_photo).into(c_pro_img);
                }
                loading.dismiss();
                profile_uri=obj.getUser_photo();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        rootView=inflater.inflate(R.layout.fragment_fragment_profile, container, false);


        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        media=databaseReference;
        sample=databaseReference.child("User-details").child(user.getUid());
        storageReference= FirebaseStorage.getInstance().getReference();
        layoutInflater=(LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);

      //  Toolbar toolbar=rootView.findViewById(R.id.tool_bar);
      //  ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.app_name);
        pd=new ProgressDialog(getActivity());
        pd.setMessage("Please wait.....");
        b_edit=rootView.findViewById(R.id.btn_edit);
        txt_email=rootView.findViewById(R.id.t_email);
        txt_nm=rootView.findViewById(R.id.t_name);
        txt_phno=rootView.findViewById(R.id.t_phno);
        txt_uid=rootView.findViewById(R.id.t_uid);
        c_pro_img=rootView.findViewById(R.id.profile_img);
        c_pro_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View show_img=layoutInflater.inflate(R.layout.show_profile_photo,null);
                ImageView img=show_img.findViewById(R.id.show_prof_img);
                edit=show_img.findViewById(R.id.pro_update);
                delete=show_img.findViewById(R.id.pro_delete);
                Picasso.with(getContext()).load(profile_uri).into(img);
                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setView(show_img);
                final AlertDialog pro_alertDialog=builder.create();
                pro_alertDialog.show();
                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View show_option=layoutInflater.inflate(R.layout.show_select_option,null);
                        cam=show_option.findViewById(R.id.btn_select_camera);
                        gal=show_option.findViewById(R.id.btn_select_gallery);
                        cancel=show_option.findViewById(R.id.btn_cancel_select);
                        android.support.v7.app.AlertDialog.Builder builder=new android.support.v7.app.AlertDialog.Builder(getActivity());
                        builder.setView(show_option);
                        final android.support.v7.app.AlertDialog dialog=builder.create();
                        dialog.setCancelable(false);
                        dialog.show();
                        cam.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(), "Camera Selected.", Toast.LENGTH_SHORT).show();
                                open_cammera();
                                dialog.dismiss();
                                pro_alertDialog.dismiss();
                            }
                        });
                        gal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(getActivity(), "Gallery Selected.", Toast.LENGTH_SHORT).show();
                                open_gallery();
                                dialog.dismiss();
                                pro_alertDialog.dismiss();
                            }
                        });
                        cancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseReference.child("User-details").child(user.getUid()).child("user_photo").setValue("none");
                        Picasso.with(getContext()).load(R.drawable.default_profile).into(c_pro_img);
                        AlertDialog.Builder done=new AlertDialog.Builder(getActivity());
                        done.setTitle("Successful");
                        done.setMessage("Profile-Image deleted successfully");
                        done.setCancelable(false);
                        done.setPositiveButton("okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                pro_alertDialog.dismiss();
                            }
                        });
                        AlertDialog done_dialog=done.create();
                        done_dialog.show();
                    }
                });
            }
        });

        b_edit=rootView.findViewById(R.id.btn_edit);
        b_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder edit_builder=new AlertDialog.Builder(getActivity());
                final View edit_view=layoutInflater.inflate(R.layout.edit_details,null);
                new_detail_discard=edit_view.findViewById(R.id.upload_delete);
                new_detail_save=edit_view.findViewById(R.id.upload_save);
                e_emid=edit_view.findViewById(R.id.update_email);
                e_nm=edit_view.findViewById(R.id.update_name);
                databaseReference.child("User-details").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Store_details_modal_class obj=dataSnapshot.getValue(Store_details_modal_class.class);
                        e_emid.setText(obj.user_email_id);
                        e_nm.setText(obj.user_name);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                edit_builder.setView(edit_view);
                final AlertDialog alertDialog=edit_builder.create();
                alertDialog.setCancelable(false);
                alertDialog.show();
                new_detail_discard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder close_popup=new AlertDialog.Builder(getActivity());
                       close_popup.setMessage("Do you want to discard the details?").setTitle("Attention!!")
                               .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       dialog.dismiss();
                                       Toast.makeText(getActivity(), "Details discarded", Toast.LENGTH_SHORT).show();
                                       alertDialog.dismiss();
                                   }
                               })
                               .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       dialog.dismiss();;
                                       new_detail_save.setError("Click here to save details");
                                   }
                               })
                               .setCancelable(false)
                               .setIcon(R.drawable.icon_alert)
                               .show();
                    }
                });
                new_detail_save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        u_name=e_nm.getText().toString().trim();
                        u_email_id=e_emid.getText().toString().trim();
                        databaseReference.child("User-details").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Store_details_modal_class obj=dataSnapshot.getValue(Store_details_modal_class.class);
                                if((obj.user_name.equals(u_name)) && (obj.user_email_id.equals(u_email_id)))
                                {
                                    final AlertDialog.Builder check=new AlertDialog.Builder(getActivity());
                                    check.setCancelable(false)
                                            .setTitle("Attention!!")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    alertDialog.dismiss();
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    e_emid.setError("Fill this to change");
                                                    e_nm.setError("Fill this to change");
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setMessage("Do you want to save same details?")
                                            .show();
                                }
                                else
                                {
                                    sample.child("user_name").setValue(e_nm.getText().toString().trim());
                                    sample.child("user_email_id").setValue(e_emid.getText().toString().trim());
                                    alertDialog.dismiss();
                                    AlertDialog.Builder success=new AlertDialog.Builder(getActivity());
                                    success.setTitle("Successful")
                                            .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .setMessage("Details updated successfully")
                                            .setCancelable(false)
                                            .show();

                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });
        pd_wait=new ProgressDialog(getActivity());
        pd_wait.setTitle("Please Wait..");
        pd_wait.setMessage("Loading your profile");
        pd_wait.setCancelable(false);

        viewPager = (ViewPager) rootView.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) rootView.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        return rootView;
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity().getSupportFragmentManager());
        adapter.addFragment(new FirstFragment(), "Your problems");
        adapter.addFragment(new ThirdFragment(), "Media");;
        viewPager.setAdapter(adapter);
    }
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }

    private void open_cammera()
    {
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
        {
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
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
                c_pro_img.setImageURI(uri);
                pd.show();
                if (uri == null)
                {
                    Toast.makeText(getActivity(), "Please select the image", Toast.LENGTH_SHORT).show();
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
                                databaseReference.child("User-details").child(user.getUid()).child("user_photo").setValue(download_url);
                                Store_media_modal_class obj=new Store_media_modal_class(download_url);
                                media.child("Media").child(user.getUid()).child(getCurrentTime()).setValue(obj);
                                pd.dismiss();
                                AlertDialog.Builder done=new AlertDialog.Builder(getActivity());
                                done.setTitle("Successful");
                                done.setMessage("Profile-Image updated successfully");
                                done.setCancelable(false);
                                done.setPositiveButton("okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                AlertDialog done_dialog=done.create();
                                done_dialog.show();

                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.w("hi", "" + e);
                            }
                        }
                    });
                }
            }
            if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
                Bundle bundle = data.getExtras();
                bmp = (Bitmap) bundle.get("data");
                Toast.makeText(getActivity(), "" + bmp, Toast.LENGTH_SHORT).show();
                pd.show();
                c_pro_img.setImageBitmap(bmp);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                bitmap_data = baos.toByteArray();
                StorageReference filepath = storageReference.child("profile_images").child(user.getUid()).child("profile");
                filepath.putBytes(bitmap_data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        download_url = taskSnapshot.getDownloadUrl().toString().trim();
                        databaseReference.child("User-details").child(user.getUid()).child("user_photo").setValue(download_url);
                        pd.dismiss();
                        Store_media_modal_class obj=new Store_media_modal_class(download_url);
                        media.child("Media").child(user.getUid()).child(getCurrentTime()).setValue(obj);
                        AlertDialog.Builder done=new AlertDialog.Builder(getActivity());
                        done.setTitle("Successful");
                        done.setMessage("Profile-Image updated successfully");
                        done.setCancelable(false);
                        done.setPositiveButton("okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        AlertDialog done_dialog=done.create();
                        done_dialog.show();
                    }
                });
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_LONG).show();
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
