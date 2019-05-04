package wayforlife.com.wfl;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import wayforlife.com.wfl.Adapter_class.commentAdapter;
import wayforlife.com.wfl.Modal_class.single_comment;
import wayforlife.com.wfl.Modal_class.single_newsfeed_item;

public class activity_comment extends AppCompatActivity {

    DatabaseReference reference;
    ArrayList<single_comment> list;
    single_newsfeed_item item;
    ImageButton makeComment;
    EditText textComment;
    commentAdapter adapter;
    RecyclerView commentRecyclerview;
    String newsfeed_key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        makeComment = findViewById(R.id.make_comment);
        textComment = findViewById(R.id.text_comment);
        reference = FirebaseDatabase.getInstance().getReference();
        commentRecyclerview = findViewById(R.id.comment_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        commentRecyclerview.setLayoutManager(layoutManager);
        list = new ArrayList<>();

        Intent intent=getIntent();

        newsfeed_key=intent.getStringExtra("key");
        getComments();





        makeComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(textComment.getText().toString())) {
                    single_comment com = new single_comment("Nitin", textComment.getText().toString());
                    reference.child("newsfeed").child(newsfeed_key).child("comment").push().setValue(com);
                    textComment.setText("");
                }
                else
                {
                    Toast.makeText(activity_comment.this, "Text is Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


 void getComments()
    {
        final boolean gotdata;

        reference.child("newsfeed").child(newsfeed_key).child("comment").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> it=dataSnapshot.getChildren();
                list.clear();
            for(DataSnapshot d :it)
            {

                single_comment com=d.getValue(single_comment.class);
                list.add(com);

            }
                Toast.makeText(activity_comment.this, Integer.toString(list.size()), Toast.LENGTH_SHORT).show();
            adapter=new commentAdapter(list,activity_comment.this);
            commentRecyclerview.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
