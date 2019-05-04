package wayforlife.com.wfl.Adapter_class;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import wayforlife.com.wfl.Modal_class.Problem_report_modal_class;
import wayforlife.com.wfl.Modal_class.Store_details_modal_class;
import wayforlife.com.wfl.Modal_class.Store_spam_modal_class;
import wayforlife.com.wfl.R;

public class Show_notification_adapter extends RecyclerView.Adapter<Show_notification_adapter.Item> {

    Context context;
    List<Store_details_modal_class> mData;
    public Show_notification_adapter(Context context, List<Store_details_modal_class> mData)
    {
        this.context=context;
        this.mData=mData;
    }
    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.activity_show_notification_adapter,parent,false);
        context=parent.getContext();
        return new Show_notification_adapter.Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
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
            t_stat=itemView.findViewById(R.id.txt_notification);
            p_img=itemView.findViewById(R.id.profile_notification);
        }
    }
}
