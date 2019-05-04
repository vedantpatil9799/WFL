package wayforlife.com.wfl.Profile_tab_fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import wayforlife.com.wfl.Modal_class.Store_media_modal_class;
import wayforlife.com.wfl.R;
import wayforlife.com.wfl.Adapter_class.Show_media_adapter;


public class ThirdFragment extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private RecyclerView media_Layout;
    List<Store_media_modal_class> item_list;
    public ThirdFragment() {
        // Required empty public constructor
    }

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_third, container, false);

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference();
        media_Layout=rootView.findViewById(R.id.rec_view_media);

        item_list=new ArrayList<Store_media_modal_class>();
        databaseReference.child("Media").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren())
                {
                      Store_media_modal_class obj=dataSnapshot1.getValue(Store_media_modal_class.class);
                      item_list.add(obj);
                }
                Show_media_adapter adapter=new Show_media_adapter(getContext(),item_list);
                media_Layout.setLayoutManager(new GridLayoutManager(getActivity(),1));
                media_Layout.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return rootView;
    }
}
