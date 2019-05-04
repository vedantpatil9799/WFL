package wayforlife.com.wfl.Profile_tab_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import wayforlife.com.wfl.Modal_class.Problem_report_modal_class;
import wayforlife.com.wfl.R;
import wayforlife.com.wfl.Adapter_class.Show_problem_adapter;


public class FirstFragment extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView frameLayout;
    List<Problem_report_modal_class> item_list;
    public FirstFragment () {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        frameLayout=view.findViewById(R.id.rec_view_problem);
        databaseReference=FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        item_list=new ArrayList<Problem_report_modal_class>();
        databaseReference.child("Problem").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                    try {
                        Problem_report_modal_class obj = dataSnapshot1.getValue(Problem_report_modal_class.class);
                        Problem_report_modal_class set=new Problem_report_modal_class();
                        set.setCaption(obj.caption);
                        set.setLatitude(obj.latitude);
                        set.setLongitude(obj.longitude);
                        set.setUri(obj.uri);
                        item_list.add(set);
                        //Toast.makeText(getActivity(), ""+obj.caption, Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e)
                    {
                        Toast.makeText(getActivity(), "Error.."+e, Toast.LENGTH_SHORT).show();
                    }
                }
                Show_problem_adapter adapter=new Show_problem_adapter(getContext(),item_list);
                frameLayout.setLayoutManager(new GridLayoutManager(getActivity(),1));
                frameLayout.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return view;
    }
}