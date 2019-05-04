package wayforlife.com.wfl.Adapter_class;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import wayforlife.com.wfl.Modal_class.Problem_report_modal_class;
import wayforlife.com.wfl.Modal_class.Store_details_modal_class;
import wayforlife.com.wfl.Modal_class.Store_spam_modal_class;
import wayforlife.com.wfl.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Show_admin_notification_adapter extends RecyclerView.Adapter<Show_admin_notification_adapter.Item> {

    Context context;
    List<Store_details_modal_class> mData;
    DatabaseReference databaseReference,del;
    String del_time;
    public Show_admin_notification_adapter(Context context, List<Store_details_modal_class> mData)
    {
        this.context=context;
        this.mData=mData;
    }
    @NonNull
    @Override
    public Show_admin_notification_adapter.Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.activity_show_admin_notification_adapter,parent,false);
        context=parent.getContext();
        return new Show_admin_notification_adapter.Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Show_admin_notification_adapter.Item holder, final int position) {
        holder.t_stat.setText(mData.get(position).getUser_id());
        Picasso.with(context).load(mData.get(position).getUser_photo()).into(holder.p_img);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class Item extends RecyclerView.ViewHolder {

        TextView t_stat;
        CircleImageView p_img;
        public Item(View itemView) {
            super(itemView);
            t_stat=itemView.findViewById(R.id.txt_ad_notification);
            p_img=itemView.findViewById(R.id.profile_ad_notification);
        }
    }
}
