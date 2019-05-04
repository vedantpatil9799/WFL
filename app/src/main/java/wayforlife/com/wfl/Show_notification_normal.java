package wayforlife.com.wfl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import wayforlife.com.wfl.Adapter_class.Show_media_adapter;
import wayforlife.com.wfl.Adapter_class.Show_notification_adapter;
import wayforlife.com.wfl.Modal_class.Store_details_modal_class;
import wayforlife.com.wfl.Modal_class.Store_media_modal_class;
import wayforlife.com.wfl.Modal_class.Store_spam_modal_class;

public class Show_notification_normal extends AppCompatActivity {

    private DatabaseReference databaseReference,sample;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView spam_Layout;
    List<Store_details_modal_class> item_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notification);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        sample=databaseReference;
        spam_Layout=findViewById(R.id.recv_notification);

        item_list=new ArrayList<Store_details_modal_class>();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        databaseReference.child("Notification").child("normal_user").child(date).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                    {
                        for(DataSnapshot dataSnapshot11:dataSnapshot1.getChildren())
                        {
                            final Store_spam_modal_class obj=dataSnapshot11.getValue(Store_spam_modal_class.class);
                           // Toast.makeText(Show_notification_normal.this, ""+obj.s_uid, Toast.LENGTH_SHORT).show();
                            sample.child("User-details").child(obj.s_uid).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot data)
                                {
                                    Store_details_modal_class fetch=data.getValue(Store_details_modal_class.class);
                                    //Toast.makeText(Show_notification_normal.this, ""+fetch.user_email_id, Toast.LENGTH_SHORT).show();
                                    if((obj.s_type).equals("new_problem"))
                                    {
                                        String stat=fetch.user_id.concat(" posted new problem.");
                                        fetch.setUser_id(stat);
                                        item_list.add(fetch);
                                    }
                                    else if((obj.s_type).equals("new_post"))
                                    {
                                        String stat=fetch.user_id.concat(" posted new post on a timeline.");
                                        fetch.setUser_id(stat);
                                        item_list.add(fetch);
                                    }
                                    else if((obj.s_type).equals("new_event"))
                                    {
                                        String stat=fetch.user_id.concat(" posted new event.");
                                        fetch.setUser_id(stat);
                                        item_list.add(fetch);
                                    }

                                    Show_notification_adapter adapter=new Show_notification_adapter(getApplicationContext(),item_list);
                                    spam_Layout.setLayoutManager(new GridLayoutManager(getApplicationContext(),1));
                                    spam_Layout.setAdapter(adapter);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
