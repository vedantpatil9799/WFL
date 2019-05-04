package wayforlife.com.wfl;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;


public class Fragment_donate extends Fragment {


    CircleImageView blood_donate,charity_donate;
    public Fragment_donate() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

       View rootView=inflater.inflate(R.layout.fragment_fragment_donate, container, false);
       /* Uri uri=Uri.parse("http://mlp.co/wayoflif ");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);*/

       blood_donate=rootView.findViewById(R.id.blood_donate_img);
       charity_donate=rootView.findViewById(R.id.money_donate_img);



       blood_donate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
                Uri uri=Uri.parse("http://www.friends2support.org/ ");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
           }
       });

       charity_donate.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Uri uri=Uri.parse("http://mlp.co/wayoflif ");
                  Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
           }
       });

        return rootView;
    }

}
