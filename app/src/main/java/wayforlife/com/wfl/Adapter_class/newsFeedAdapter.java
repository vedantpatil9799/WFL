package wayforlife.com.wfl.Adapter_class;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.ValueIterator;
import android.media.Image;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import wayforlife.com.wfl.Modal_class.single_newsfeed_item;
import wayforlife.com.wfl.R;
import wayforlife.com.wfl.activity_comment;


public class newsFeedAdapter extends RecyclerView.Adapter {

    private ArrayList<single_newsfeed_item> list;
    private StorageReference reference;
    private Uri video_uri,image_uri;
    private static Context mContext;
    private static DatabaseReference databaseReference;
    private ArrayList<String> keylist,userprofile;
    private Boolean isVoted=false;
    private FirebaseAuth authentication;
    private ArrayList<String> votelist;




    public newsFeedAdapter(ArrayList<single_newsfeed_item> list, Context mContext, ArrayList<String> keylist,ArrayList<String> votelist,ArrayList<String> userprofile) {
        this.list = list;
        this.keylist=keylist;
        this.votelist=votelist;
        this.userprofile=userprofile;
        reference = FirebaseStorage.getInstance().getReference();
        databaseReference= FirebaseDatabase.getInstance().getReference().child("newsfeed");
        this.mContext = mContext;
        authentication=FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if(i==single_newsfeed_item.TEXT_TYPE)
            {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.newsfeed_text_item,viewGroup,false);
            return new TextTypeViewHolder(view);
        }
        else if(i==single_newsfeed_item.IMAGE_TYPE)
        {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.newsfeed_imageitem,viewGroup,false);
           return new ImageTypeViewHolder(view);
        }
        else
        {
            View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.newsfeed_videoitem,viewGroup,false);
                return new VideoTypeViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {

        switch (viewHolder.getItemViewType())
        {
            case single_newsfeed_item.TEXT_TYPE:
                ((TextTypeViewHolder)viewHolder).text_username.setText(list.get(i).getName());
                ((TextTypeViewHolder)viewHolder).text_date.setText(list.get(i).getDate());
                ((TextTypeViewHolder)viewHolder).act_text.setText(list.get(i).getUrl_or_text());

                if(userprofile.get(i).equals("none"))
                    ((TextTypeViewHolder)viewHolder).profile.setImageResource(R.drawable.ic_person_black_24dp);
                else
                    Picasso.with(mContext).load(userprofile.get(i)).into(((TextTypeViewHolder)viewHolder).profile);
                ((TextTypeViewHolder)viewHolder).addComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,activity_comment.class);
                        intent.putExtra("key",keylist.get(i));
                        mContext.startActivity(intent);

                    }
                });

                ((TextTypeViewHolder)viewHolder).allcomments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext, activity_comment.class);
                        intent.putExtra("key",keylist.get(i));
                        mContext.startActivity(intent);
                    }
                });

                if(list.get(i).isPoll())
                {

                    if(votelist.get(i).equals("true")) {
                       showPollResult(((TextTypeViewHolder)viewHolder).layout,keylist.get(i),list.get(i).getOp1_count(),list.get(i).getOp2_count(),list.get(i).getPoll_option1(),list.get(i).getPoll_option2());
                    }
                    else
                    {
                        addPollRowText(((TextTypeViewHolder)viewHolder).layout,keylist.get(i),i);
                    }
                }

                ((TextTypeViewHolder)viewHolder).upvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String userid=authentication.getUid();
                        databaseReference.child(keylist.get(i)).child("VotedUser").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.child(userid).exists())
                                {
                                    Toast.makeText(mContext,"AlreadyVoted",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    databaseReference.child(keylist.get(i)).child("VotedUser").child(userid).setValue(true);
                                    databaseReference.child(keylist.get(i)).child("feed").child("upvoteCount").setValue(list.get(i).getUpvoteCount()+1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });

                ((TextTypeViewHolder)viewHolder).downvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String userid=authentication.getUid();
                        databaseReference.child(keylist.get(i)).child("VotedUser").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(userid))
                                {
                                    Toast.makeText(mContext,"AlreadyVoted",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    databaseReference.child(keylist.get(i)).child("VotedUser").child(userid).setValue(true);
                                    databaseReference.child(keylist.get(i)).child("feed").child("downvoteCount").setValue(list.get(i).getDownvoteCount()+1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });


                break;

            case single_newsfeed_item.IMAGE_TYPE:
               ((ImageTypeViewHolder)viewHolder).image_date.setText(list.get(i).getDate());
                ((ImageTypeViewHolder)viewHolder).image_username.setText(list.get(i).getName());
                if (list.get(i).getUserCaption().equals("NO_CAPTION"))
                    ((ImageTypeViewHolder)viewHolder).image_cap.setText("");
                else
                    ((ImageTypeViewHolder)viewHolder).image_cap.setText(list.get(i).getUserCaption());


                if(userprofile.get(i).equals("none"))
                    ((ImageTypeViewHolder)viewHolder).profile.setImageResource(R.drawable.ic_person_black_24dp);
                else
                    Picasso.with(mContext).load(userprofile.get(i)).into(((ImageTypeViewHolder)viewHolder).profile);

                Picasso.with(mContext).load(list.get(i).getUrl_or_text()).into(((ImageTypeViewHolder) viewHolder).imageView);

                ((ImageTypeViewHolder)viewHolder).addComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent=new Intent(mContext,activity_comment.class);
                        intent.putExtra("key",keylist.get(i));
                        mContext.startActivity(intent);

                    }
                });

                ((ImageTypeViewHolder)viewHolder).allcomments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,activity_comment.class);
                        intent.putExtra("key",keylist.get(i));
                        mContext.startActivity(intent);
                    }
                });

                ((ImageTypeViewHolder)viewHolder).upvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String userid=authentication.getUid();
                        databaseReference.child(keylist.get(i)).child("VotedUser").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(userid))
                                {
                                    Toast.makeText(mContext,"AlreadyVoted",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    Toast.makeText(mContext, "Voted Successfully", Toast.LENGTH_SHORT).show();
                                    databaseReference.child(keylist.get(i)).child("VotedUser").child(userid).setValue(true);
                                    databaseReference.child(keylist.get(i)).child("feed").child("upvoteCount").setValue(list.get(i).getUpvoteCount()+1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                ((ImageTypeViewHolder)viewHolder).downvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String userid=authentication.getUid();
                        databaseReference.child(keylist.get(i)).child("VotedUser").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(userid))
                                {
                                    Toast.makeText(mContext,"AlreadyVoted",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    databaseReference.child(keylist.get(i)).child("VotedUser").child(userid).setValue(true);
                                    databaseReference.child(keylist.get(i)).child("feed").child("downvoteCount").setValue(list.get(i).getDownvoteCount()+1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                if(list.get(i).isPoll())
                {

                    if(votelist.get(i).equals("true")) {
                        showPollResult(((ImageTypeViewHolder)viewHolder).layout,keylist.get(i),list.get(i).getOp1_count(),list.get(i).getOp2_count(),list.get(i).getPoll_option1(),list.get(i).getPoll_option2());
                    }
                    else
                    {
                        String text=list.get(i).getPollText();
                        String op1=list.get(i).getPoll_option1();
                        String op2=list.get(i).getPoll_option2();
                        Toast.makeText(mContext, text+op1+op2, Toast.LENGTH_SHORT).show();
                       addPollRow(((ImageTypeViewHolder)viewHolder).layout,keylist.get(i),i,text,op1,op2);
                    }
                }

                break;

            case single_newsfeed_item.VIDEO_TYPE:
                ((VideoTypeViewHolder)viewHolder).video_name.setText(list.get(i).getName());
                ((VideoTypeViewHolder)viewHolder).video_date.setText(list.get(i).getDate());
                if(!list.get(i).getUserCaption().equals("NOCAPTION"))
                {
                    ((VideoTypeViewHolder)viewHolder).caption.setText(list.get(i).getUserCaption());
                }
                Toast.makeText(mContext,"VIDEO URL FECTHED"+list.get(i).getUrl_or_text(), Toast.LENGTH_SHORT).show();



                if(userprofile.get(i).equals("none"))
                    ((VideoTypeViewHolder)viewHolder).profile.setImageResource(R.drawable.ic_person_black_24dp);
                else
                    Picasso.with(mContext).load(userprofile.get(i)).into(((VideoTypeViewHolder)viewHolder).profile);

                MediaController controller=new MediaController(mContext);
                controller.setAnchorView(((VideoTypeViewHolder)viewHolder).videoView);
                ((VideoTypeViewHolder)viewHolder).videoView.setMediaController(controller);
                ((VideoTypeViewHolder)viewHolder).videoView.setVideoURI(Uri.parse(list.get(i).getUrl_or_text()));
                ((VideoTypeViewHolder)viewHolder).videoView.requestFocus();


                ((VideoTypeViewHolder)viewHolder).allcomments.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,activity_comment.class);
                        intent.putExtra("key",keylist.get(i));
                        mContext.startActivity(intent);
                    }
                });

                ((VideoTypeViewHolder)viewHolder).addComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(mContext,activity_comment.class);
                        intent.putExtra("key",keylist.get(i));
                        mContext.startActivity(intent);
                    }
                });

                ((VideoTypeViewHolder)viewHolder).upvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String userid=authentication.getUid();
                        databaseReference.child(keylist.get(i)).child("VotedUser").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(userid))
                                {
                                    Toast.makeText(mContext,"AlreadyVoted",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    databaseReference.child(keylist.get(i)).child("VotedUser").child(userid).setValue(true);
                                    databaseReference.child(keylist.get(i)).child("feed").child("upvoteCount").setValue(list.get(i).getUpvoteCount()+1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                ((VideoTypeViewHolder)viewHolder).downvote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String userid=authentication.getUid();
                        databaseReference.child(keylist.get(i)).child("VotedUser").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(userid))
                                {
                                    Toast.makeText(mContext,"AlreadyVoted",Toast.LENGTH_SHORT).show();
                                }
                                else
                                {
                                    databaseReference.child(keylist.get(i)).child("VotedUser").child(userid).setValue(true);
                                    databaseReference.child(keylist.get(i)).child("feed").child("downvoteCount").setValue(list.get(i).getDownvoteCount()+1);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });


                if(list.get(i).isPoll())
                {

                    if(votelist.get(i).equals("true")) {

                     showPollResult(((VideoTypeViewHolder)viewHolder).layout,keylist.get(i),list.get(i).getOp1_count(),list.get(i).getOp2_count(),list.get(i).getPoll_option1(),list.get(i).getPoll_option2());

                    }
                    else
                    {
                        String text=list.get(i).getPollText();
                        String op1=list.get(i).getPoll_option1();
                        String op2=list.get(i).getPoll_option2();
                        Toast.makeText(mContext, text+op1+op2, Toast.LENGTH_SHORT).show();
                      addPollRow(((VideoTypeViewHolder)viewHolder).layout,keylist.get(i),i,text,op1,op2);

                    }
                }
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
         return list.get(position).getType();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void addPollRow(final TableLayout layout,final String key,final int i,String text,String option1,String option2)
    {

        layout.removeAllViews();
        TableRow pollrow=new TableRow(mContext);
        TableRow pollrow2=new TableRow(mContext);
        Button op1=new Button(mContext);
        op1.setText(option1);
        TableRow.LayoutParams lp1 =new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1f);
        TableRow.LayoutParams lp2=new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1f);
        op1.setLayoutParams(lp1);
        op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                list.get(i).setOp1_count(list.get(i).getOp1_count()+1);
                Toast.makeText(mContext, "op1 pressed", Toast.LENGTH_SHORT).show();
                databaseReference.child(key).child("feed").child("op1_count").setValue(list.get(i).getOp1_count());
                databaseReference.child(key).child("pollVotedUser").child(authentication.getUid()).setValue("true");
                showPollResult(layout,key,list.get(i).getOp1_count(),list.get(i).getOp2_count(),list.get(i).getPoll_option1(),list.get(i).getPoll_option2());


            }
        });
        Button op2=new Button(mContext);
        op2.setText(option2);
        op2.setLayoutParams(lp2);
        op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.get(i).setOp2_count(list.get(i).getOp2_count()+1);
                Toast.makeText(mContext, "Op2 Pressed", Toast.LENGTH_SHORT).show();
                databaseReference.child(key).child("feed").child("op2_count").setValue(list.get(i).getOp2_count());
                databaseReference.child(key).child("pollVotedUser").child(authentication.getUid()).setValue("true");
                showPollResult(layout,key,list.get(i).getOp1_count(),list.get(i).getOp2_count(),list.get(i).getPoll_option1(),list.get(i).getPoll_option2());

            }
        });
        TextView textView=new TextView(mContext);
        textView.setText(text);
        TableRow.LayoutParams par2=new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 100,1);
        textView.setLayoutParams(par2);
        pollrow2.addView(textView);
        layout.addView(pollrow2);
        pollrow.addView(op1);
        pollrow.addView(op2);
        layout.addView(pollrow);

    }


    public void addPollRowText(final TableLayout layout, final String key, final int i)
    {
        layout.removeAllViews();
        TableRow pollrow=new TableRow(mContext);
        Button op1=new Button(mContext);
        op1.setText(list.get(i).getPoll_option1());
        TableRow.LayoutParams lp1 =new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1f);
        TableRow.LayoutParams lp2=new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT,1f);
        op1.setLayoutParams(lp1);
        op1.setBackgroundResource(R.drawable.rounded_border_blue);
        op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                list.get(i).setOp1_count(list.get(i).getOp1_count()+1);
                Toast.makeText(mContext, "op1 pressed", Toast.LENGTH_SHORT).show();
                databaseReference.child(key).child("feed").child("op1_count").setValue(list.get(i).getOp1_count());
                databaseReference.child(key).child("pollVotedUser").child(authentication.getUid()).setValue(true);
                showPollResult(layout,key,list.get(i).getOp1_count(),list.get(i).getOp2_count(),list.get(i).getPoll_option1(),list.get(i).getPoll_option2());

            }
        });
        Button op2=new Button(mContext);
        op2.setText(list.get(i).getPoll_option2());
        op2.setLayoutParams(lp2);
        op2.setBackgroundResource(R.drawable.rounded_border_blue);
        op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.get(i).setOp2_count(list.get(i).getOp2_count()+1);
                Toast.makeText(mContext, "Op2 Pressed", Toast.LENGTH_SHORT).show();
                databaseReference.child(key).child("feed").child("op2_count").setValue(list.get(i).getOp2_count());
                databaseReference.child(key).child("pollVotedUser").child(authentication.getUid()).setValue(true);
                showPollResult(layout,key,list.get(i).getOp1_count(),list.get(i).getOp2_count(),list.get(i).getPoll_option1(),list.get(i).getPoll_option2());

            }
        });
        pollrow.addView(op1);
        pollrow.addView(op2);
        layout.addView(pollrow);
    }



    public void showPollResult(TableLayout layout,final String key, final int op1, final int op2, String option1, String option2)
    {
        TableRow resultrow=new TableRow(mContext);
        layout.removeAllViews();
        TextView pos=new TextView(mContext);
        TextView neg=new TextView(mContext);
        float total_count=op1+op2;
        float posPer= Math.abs(op1*100/total_count);
        float negPer= Math.abs(op2*100/total_count);
        TableRow.LayoutParams par1=new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 50,posPer);
        TableRow.LayoutParams par2=new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 50,negPer);



        pos.setBackgroundResource(R.color.colorAccent);
        neg.setBackgroundResource(android.R.color.holo_red_dark);

        pos.setLayoutParams(par1);
        neg.setLayoutParams(par2);

        resultrow.addView(pos);
        resultrow.addView(neg);
        layout.addView(resultrow);
    }










    public static class TextTypeViewHolder extends RecyclerView.ViewHolder
    {
        TextView text_username,text_date,act_text,allcomments,polltext,addComment;
        Button op1,op2;
        ImageView upvote,downvote,profile;
        TableLayout layout;
        public TextTypeViewHolder(View itemView)
        {
            super(itemView);
            profile=itemView.findViewById(R.id.text_item_userProfile);
            layout=itemView.findViewById(R.id.poll_layout);
            allcomments=itemView.findViewById(R.id.text_all_comments);
            addComment=itemView.findViewById(R.id.text_etx_comment);
            text_username=itemView.findViewById(R.id.text_item_user_name);
            text_date=itemView.findViewById(R.id.text_item_date);
            act_text=itemView.findViewById(R.id.text_item_text);
            upvote=itemView.findViewById(R.id.text_upvote);
            downvote=itemView.findViewById(R.id.text_downvote);

        }
    }


    public static class ImageTypeViewHolder extends RecyclerView.ViewHolder {
        TextView image_username, image_date,allcomments,polltext,addComment,image_cap;
        ImageView imageView,profile;
        Button op1,op2;
        ImageButton upvote, downvote;
        TableLayout layout;

        public ImageTypeViewHolder(View itemView) {
            super(itemView);
            profile=itemView.findViewById(R.id.image_item_userProfile);
            image_cap=itemView.findViewById(R.id.image_description);
            allcomments=itemView.findViewById(R.id.image_all_comments);
            addComment=itemView.findViewById(R.id.image_etx_comment);
           image_date = itemView.findViewById(R.id.image_item_date);
            imageView = itemView.findViewById(R.id.image_item_image);
            upvote = itemView.findViewById(R.id.image_upvote);
            downvote = itemView.findViewById(R.id.image_downvote);
            image_username=itemView.findViewById(R.id.img_username);
            layout=itemView.findViewById(R.id.image_tableLayout);


        }
    }

        public static class VideoTypeViewHolder extends RecyclerView.ViewHolder {
            TextView video_name, video_date,allcomments,addComment,caption;
            VideoView videoView;
            ImageButton upvote, downvote;
            ImageView profile;
            TableLayout layout;

            public VideoTypeViewHolder(View itemView) {
                super(itemView);
                profile=itemView.findViewById(R.id.video_item_userProfile);
                caption=itemView.findViewById(R.id.video_description);
                allcomments=itemView.findViewById(R.id.video_all_comments);
                addComment=itemView.findViewById(R.id.video_etx_comment);
                video_name = itemView.findViewById(R.id.video_item_user_name);
                video_date = itemView.findViewById(R.id.video_item_date);
                videoView = itemView.findViewById(R.id.video_item_video);
                upvote = itemView.findViewById(R.id.video_upvote);
                downvote = itemView.findViewById(R.id.video_downvote);
                layout=itemView.findViewById(R.id.video_tableLayout);

            }

        }
    }


