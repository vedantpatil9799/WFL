package wayforlife.com.wfl;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import wayforlife.com.wfl.Modal_class.single_newsfeed_item;

public class Write_OwnText extends AppCompatActivity {
    Button post;
    CheckBox includePoll;
    EditText text,polloption1,polloption2;
    String UserText,poll_op1,poll_op2;
    Date date1;
    String Upload_date;
    private Calendar mCalender;
    String key;
    private FirebaseAuth auth;
    private DatabaseReference reference,databaseReference;
    private String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write__own_text);
        auth=FirebaseAuth.getInstance();
        post=findViewById(R.id.post_textfeed);
        includePoll=findViewById(R.id.include_poll);
        text=findViewById(R.id.user_text);
        polloption1=findViewById(R.id.poll_option_1);
        polloption2=findViewById(R.id.poll_option_2);
        date1= Calendar.getInstance().getTime();
        SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy");   //Getting date
        Upload_date=sdf.format(date1);
        reference= FirebaseDatabase.getInstance().getReference();
        databaseReference=reference.child("newsfeed");
        key=databaseReference.push().getKey();
        Intent intent=getIntent();

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                single_newsfeed_item item=new single_newsfeed_item();
            if(!TextUtils.isEmpty(text.getText().toString()))
            {

                UserText=text.getText().toString();

                item.setUpvoteCount(0);
                item.setDownvoteCount(0);
                item.setUserCaption("NOCAPTION");
                if(includePoll.isChecked())
                {
                    poll_op1=polloption1.getText().toString();
                    poll_op2=polloption2.getText().toString();
                    if(!TextUtils.isEmpty(poll_op1) && !TextUtils.isEmpty(poll_op2))
                    {
                        item.setPollText("NOTEXT");
                        item.setPoll_option1(poll_op1);
                        item.setPoll_option2(poll_op2);
                        item.setOp1_count(0);
                        item.setOp2_count(0);
                    }
                    else
                    {
                        Toast.makeText(Write_OwnText.this, "Poll Options should not be empty", Toast.LENGTH_SHORT).show();
                    }
                }


                item.setPoll(includePoll.isChecked());
                item.setDate(Upload_date);
                item.setName(auth.getUid());

                Toast.makeText(Write_OwnText.this, ""+auth.getUid(), Toast.LENGTH_SHORT).show();
                item.setType(single_newsfeed_item.TEXT_TYPE);
                item.setUrl_or_text(UserText);
               databaseReference.push().child("feed").setValue(item);
                Toast.makeText(Write_OwnText.this, "Uploaded Successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Write_OwnText.this,MainActivity.class));
            }
            else
            {
                Toast.makeText(Write_OwnText.this,"Text should not be empty", Toast.LENGTH_SHORT).show();

            }
            }
        });

    }
}
