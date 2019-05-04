package wayforlife.com.wfl;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wayforlife.com.wfl.Modal_class.Problem_report_modal_class;
import wayforlife.com.wfl.Modal_class.Store_media_modal_class;
import wayforlife.com.wfl.Modal_class.Store_spam_modal_class;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.content.Context.LOCATION_SERVICE;


public class Fragment_ReportProblem extends Fragment implements OnMapReadyCallback {
    private View rootView,sampleView;
    private GoogleMap mMap;
    private MapView mapView;
    private ImageButton cur_loc;
    private FloatingActionButton report,blood;
    private double latitude, longitude;
    private Button cam,gal,cancel,skip,upload,close_marker;
    private final static int PICK_IMAGE_REQUEST=70,CAMERA_REQUEST_CODE=1;
    private Bitmap bmp,bmp1;
    private byte[] bitmap_data;
    private ProgressDialog pd,pd_loading_marker;
    private LayoutInflater layoutInflater;
    private StorageReference storageReference;
    private DatabaseReference databaseReference,media,notification;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private String download_url,currentTime,caption_text,marker_caption,desc_text;
    private EditText caption,desc;
    private Uri uri1;
    private Problem_report_modal_class fetch;
    private Boolean flag;
    private ImageView problem_image,problem_report;
    private TextView problem_caption,problem_desc,problem_uid;

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_report_problem, container, false);

        storageReference=FirebaseStorage.getInstance().getReference();
        databaseReference=FirebaseDatabase.getInstance().getReference();
        media=databaseReference;
        notification=databaseReference;
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        layoutInflater=(LayoutInflater) getActivity().getSystemService(LAYOUT_INFLATER_SERVICE);
        pd=new ProgressDialog(getActivity());
        pd.setTitle("Please Wait..");
        pd.setMessage("Reporting your problem.....");
        pd_loading_marker=new ProgressDialog(getActivity());
        pd_loading_marker.setTitle("Please Wait..");
        pd_loading_marker.setMessage("Loading Map For You.....");
        cur_loc = rootView.findViewById(R.id.getLocation);
        report = rootView.findViewById(R.id.report_problem);

        cur_loc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "click", Toast.LENGTH_SHORT).show();
                getCurrentLocation();
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reportProblem();
            }
        });

      /*  blood=rootView.findViewById(R.id.fab_blood);
        blood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri=Uri.parse("http://www.friends2support.org/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });*/

        return rootView;
    }

    private void reportProblem()
    {
        View show_option=layoutInflater.inflate(R.layout.show_select_option,null);
        cam=show_option.findViewById(R.id.btn_select_camera);
        gal=show_option.findViewById(R.id.btn_select_gallery);
        cancel=show_option.findViewById(R.id.btn_cancel_select);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setView(show_option);
        final AlertDialog dialog=builder.create();
        dialog.setCancelable(false);
        dialog.show();
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Camera Selected.", Toast.LENGTH_SHORT).show();
                open_cammera();
                dialog.dismiss();
            }
        });
        gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Gallery Selected.", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mapView = rootView.findViewById(R.id.google_map);
        if (mapView != null) {
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        MapsInitializer.initialize(getContext());
        mMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        getCurrentLocation();

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(getActivity(), "Click on the marker", Toast.LENGTH_SHORT).show();
                marker_caption=marker.getTitle();
                open_descView(marker_caption);
                return false;

            }
        });
    }

    private void open_descView(final String marker_caption)
    {
        //Toast.makeText(getActivity(), ""+marker_caption, Toast.LENGTH_SHORT).show();
        databaseReference.child("Problem").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    for(DataSnapshot dataSnapshot11:dataSnapshot1.getChildren())
                    {
                        try
                        {
                            Problem_report_modal_class obj=dataSnapshot11.getValue(Problem_report_modal_class.class);
                            if(obj.getCaption().equals(marker_caption))
                            {
                                fetch=obj;
                                flag=true;
                                break;
                            }
                            else
                            {
                                flag=false;
                            }
                        }
                        catch(Exception e)
                        {
                            Toast.makeText(getActivity(), "Error.."+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(flag)
                    {
                        break;
                    }
                }
                if(flag)
                {
                   // Toast.makeText(getActivity(), ""+fetch.getCaption(), Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                    View open_problem=layoutInflater.inflate(R.layout.show_detail_marker,null);
                    close_marker=open_problem.findViewById(R.id.btn_close);
                    problem_caption=open_problem.findViewById(R.id.t_caption);
                    problem_image=open_problem.findViewById(R.id.i_problem_image);
                    problem_caption.setText(fetch.getCaption());
                    problem_desc=open_problem.findViewById(R.id.pr_desc);
                    problem_report=open_problem.findViewById(R.id.report_problem);
                    problem_report.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder report=new AlertDialog.Builder(getActivity());
                            report.setCancelable(false)
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                            Store_spam_modal_class normal_noti=new Store_spam_modal_class(fetch.getUid(),currentTime,"spam");
                                            notification.child("Notification").child("admin_user").child(date).child(user.getUid()).child(getCurrentTime()).setValue(normal_noti);
                                            dialog.dismiss();

                                        }
                                    })
                                    .setMessage("Do you want to report this problem?")
                                    .setTitle("Attention!!")
                                    .setIcon(R.drawable.icon_red_alert)
                                    .show();
                        }
                    });
                    problem_desc.setText(fetch.desc);
                    Picasso.with(getContext()).load(fetch.getUri()).into(problem_image);
                    builder.setView(open_problem);
                    final AlertDialog alertDialog=builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.show();
                    close_marker.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void loadMarker()
    {
        databaseReference.child("Problem").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                pd_loading_marker.show();
                //Toast.makeText(getActivity(), "at root", Toast.LENGTH_SHORT).show();
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    //Toast.makeText(getActivity(), "at problem", Toast.LENGTH_SHORT).show();
                    for(DataSnapshot dataSnapshot11:dataSnapshot1.getChildren())
                    {
                        //Toast.makeText(getActivity(), "at user-id", Toast.LENGTH_SHORT).show();
                        try
                        {
                            //Toast.makeText(getActivity(), "at value", Toast.LENGTH_SHORT).show();
                            Problem_report_modal_class  get=dataSnapshot11.getValue(Problem_report_modal_class.class);
                            //Toast.makeText(getActivity(), ""+get.getCaption()+" fetched", Toast.LENGTH_SHORT).show();
                            LatLng latLng=new LatLng(get.getLatitude(),get.getLongitude());
                            mMap.addMarker(new MarkerOptions().position(latLng).title(get.getCaption()));
                           // Toast.makeText(getActivity(), ""+get.getCaption()+" added", Toast.LENGTH_SHORT).show();
                        }
                        catch(Exception e)
                        {
                            Toast.makeText(getActivity(), "Error.."+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                pd_loading_marker.dismiss();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public void getCurrentLocation()
    {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, new LocationListener() {
                @Override
                public void onLocationChanged(final Location location) {
                    mMap.clear();
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();
                   // Toast.makeText(getActivity(), ""+latitude+","+longitude, Toast.LENGTH_SHORT).show();
                    LatLng latLng=new LatLng(latitude,longitude);
                        mMap.addMarker(new MarkerOptions().position(latLng).title("Current Position")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.2f));
                        loadMarker();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }
        else if(locationManager.isProviderEnabled(locationManager.GPS_PROVIDER))
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    mMap.clear();
                    latitude=location.getLatitude();
                    longitude=location.getLongitude();
                    LatLng latLng=new LatLng(latitude,longitude);
                    mMap.addMarker(new MarkerOptions().position(latLng).title("Current Position")).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,0.2f));
                    loadMarker();
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
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
                uploadUri(uri);
            }
            if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE) {
                Bundle bundle = data.getExtras();
                bmp = (Bitmap) bundle.get("data");
                Toast.makeText(getActivity(), "" + bmp, Toast.LENGTH_SHORT).show();
                uploadBitmap(bmp);
            }
        } catch (Exception e) {
            Toast.makeText(getActivity(), "" + e, Toast.LENGTH_LONG).show();
        }
    }

    private void uploadBitmap(Bitmap bmp)
    {
        sampleView=layoutInflater.inflate(R.layout.popup_view_caption,null);
        skip=sampleView.findViewById(R.id.btn_skip);
        upload=sampleView.findViewById(R.id.btn_upload);
        caption=sampleView.findViewById(R.id.edit_caption);
        desc=sampleView.findViewById(R.id.edit_desc);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setView(sampleView);
        final AlertDialog alertDialog=builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        bmp1=bmp;
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                pd.show();
                getCurrentLocation();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bmp1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                bitmap_data = baos.toByteArray();
                currentTime=getCurrentTime();
                StorageReference filepath = storageReference.child("problem_images").child(user.getUid()).child(currentTime).child("profile");
                filepath.putBytes(bitmap_data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        download_url = taskSnapshot.getDownloadUrl().toString().trim();
                        Problem_report_modal_class obj=new Problem_report_modal_class(user.getUid(),download_url,latitude,longitude,currentTime);
                        databaseReference.child("Problem").child(user.getUid()).child(currentTime).setValue(obj);
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Problem Reported", Toast.LENGTH_SHORT).show();

                        Store_media_modal_class url_obj=new Store_media_modal_class(download_url);
                        media.child("Media").child(user.getUid()).child(getCurrentTime()).setValue(url_obj);


                        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                        Store_spam_modal_class normal_noti=new Store_spam_modal_class(user.getUid(),currentTime,"new_problem");
                        notification.child("Notification").child("admin_user").child(date).child(user.getUid()).child(getCurrentTime()).setValue(normal_noti);
                        notification.child("Notification").child("normal_user").child(date).child(user.getUid()).child(getCurrentTime()).setValue(normal_noti);

                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("Successful");
                        builder.setPositiveButton("okay", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder.setMessage("Problem reported successfully.");
                        final AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.show();
                    }
                });
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caption_text=caption.getText().toString().trim();
                desc_text=desc.getText().toString().trim();
                if(!caption_text.isEmpty() && !desc_text.isEmpty()) {
                    alertDialog.dismiss();
                    pd.show();
                    getCurrentLocation();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp1.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    bitmap_data = baos.toByteArray();
                    currentTime = getCurrentTime();
                    StorageReference filepath = storageReference.child("problem_images").child(user.getUid()).child(currentTime).child("profile");
                    filepath.putBytes(bitmap_data).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            download_url = taskSnapshot.getDownloadUrl().toString().trim();
                            Problem_report_modal_class obj = new Problem_report_modal_class(user.getUid(),download_url,caption_text, latitude, longitude,desc_text,currentTime);
                            databaseReference.child("Problem").child(user.getUid()).child(currentTime).setValue(obj);
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Problem Reported", Toast.LENGTH_SHORT).show();

                            Store_media_modal_class url_obj=new Store_media_modal_class(download_url);
                            media.child("Media").child(user.getUid()).child(getCurrentTime()).setValue(url_obj);

                            String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                            Store_spam_modal_class normal_noti=new Store_spam_modal_class(user.getUid(),currentTime,"new_problem");
                            notification.child("Notification").child("admin_user").child(date).child(user.getUid()).child(getCurrentTime()).setValue(normal_noti);
                            notification.child("Notification").child("normal_user").child(date).child(user.getUid()).child(getCurrentTime()).setValue(normal_noti);

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Successful");
                            builder.setPositiveButton("okay", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.setMessage("Problem reported successfully.");
                            final AlertDialog alertDialog = builder.create();
                            alertDialog.setCancelable(false);
                            alertDialog.show();
                        }
                    });
                }
                else
                {
                    caption.setError("Compulsory Field");
                }
            }
        });
    }
    private void uploadUri(Uri uri)
    {
        sampleView=layoutInflater.inflate(R.layout.popup_view_caption,null);
        skip=sampleView.findViewById(R.id.btn_skip);
        upload=sampleView.findViewById(R.id.btn_upload);
        caption=sampleView.findViewById(R.id.edit_caption);
        desc=sampleView.findViewById(R.id.edit_desc);
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setView(sampleView);
        final AlertDialog alertDialog=builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        uri1=uri;
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                pd.show();
                getCurrentLocation();
                currentTime = getCurrentTime();
                if (uri1 == null)
                {
                    Toast.makeText(getActivity(), "Please select the image", Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                }
                else {

                    StorageReference filepath = storageReference.child("problem_images").child(user.getUid()).child(currentTime).child("profile");
                    filepath.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            try {
                                download_url = taskSnapshot.getDownloadUrl().toString().trim();
                                Problem_report_modal_class obj = new Problem_report_modal_class(user.getUid(),download_url, latitude, longitude,currentTime);
                                databaseReference.child("Problem").child(user.getUid()).child(currentTime).setValue(obj);
                                pd.dismiss();
                                Toast.makeText(getActivity(), "Problem Reported", Toast.LENGTH_SHORT).show();

                                Store_media_modal_class url_obj=new Store_media_modal_class(download_url);
                                media.child("Media").child(user.getUid()).child(getCurrentTime()).setValue(url_obj);

                                String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                Store_spam_modal_class normal_noti=new Store_spam_modal_class(user.getUid(),currentTime,"new_problem");
                                notification.child("Notification").child("admin_user").child(date).child(user.getUid()).child(getCurrentTime()).setValue(normal_noti);
                                notification.child("Notification").child("normal_user").child(date).child(user.getUid()).child(getCurrentTime()).setValue(normal_noti);

                                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                builder.setTitle("Successful");
                                builder.setPositiveButton("okay", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                builder.setMessage("Problem reported successfully.");
                                final AlertDialog alertDialog = builder.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();

                            } catch (Exception e) {
                                Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                Log.w("hi", "" + e);
                            }
                        }
                    });
                }
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                caption_text=caption.getText().toString().trim();
                desc_text=desc.getText().toString().trim();
                if(!caption_text.isEmpty()) {
                    alertDialog.dismiss();
                    pd.show();
                    getCurrentLocation();

                    currentTime=getCurrentTime();
                    if (uri1 == null)
                    {
                        Toast.makeText(getActivity(), "Please select the image", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                    else {

                        StorageReference filepath = storageReference.child("problem_images").child(user.getUid()).child(currentTime).child("profile");
                        filepath.putFile(uri1).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                try {
                                    download_url = taskSnapshot.getDownloadUrl().toString().trim();
                                    Problem_report_modal_class obj = new Problem_report_modal_class(user.getUid(),download_url, caption_text, latitude, longitude,desc_text,currentTime);
                                    databaseReference.child("Problem").child(user.getUid()).child(currentTime).setValue(obj);
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Problem Reported", Toast.LENGTH_SHORT).show();

                                    Store_media_modal_class url_obj=new Store_media_modal_class(download_url);
                                    media.child("Media").child(user.getUid()).child(getCurrentTime()).setValue(url_obj);

                                    String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                                    Store_spam_modal_class normal_noti=new Store_spam_modal_class(user.getUid(),currentTime,"new_problem");
                                    notification.child("Notification").child("admin_user").child(date).child(user.getUid()).child(getCurrentTime()).setValue(normal_noti);
                                    notification.child("Notification").child("normal_user").child(date).child(user.getUid()).child(getCurrentTime()).setValue(normal_noti);

                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Successful");
                                    builder.setPositiveButton("okay", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.setMessage("Problem reported successfully.");
                                    final AlertDialog alertDialog = builder.create();
                                    alertDialog.setCancelable(false);
                                    alertDialog.show();
                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "" + e.getMessage(), Toast.LENGTH_LONG).show();
                                    Log.w("hi", "" + e);
                                }
                            }
                        });
                    }
                }
                else
                {
                    caption.setError("Compulsory Field");
                }
            }
        });
    }

    public String getCurrentTime()
    {
        Date date = new Date();
        String strDateFormat = "hh:mm:ss a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        return dateFormat.format(date);
    }
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
