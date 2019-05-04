package wayforlife.com.wfl.Adapter_class;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import wayforlife.com.wfl.Modal_class.Problem_report_modal_class;
import wayforlife.com.wfl.R;

public class Show_problem_adapter extends RecyclerView.Adapter<Show_problem_adapter.Item> {

    Context context;
    List<Problem_report_modal_class> mData;
    public Show_problem_adapter(Context context, List<Problem_report_modal_class> mData)
    {
        this.context=context;
        this.mData=mData;
    }
    @NonNull
    @Override
    public Show_problem_adapter.Item onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.activity_show_problem_adapter,viewGroup,false);
        context=viewGroup.getContext();
        return new Show_problem_adapter.Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Show_problem_adapter.Item viewHolder, final int i) {
        viewHolder.t_cap.setText(mData.get(i).getCaption());
        Picasso.with(context).load(mData.get(i).getUri()).into(viewHolder.p_img);
       // Toast.makeText(context, "in adapter", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class Item extends RecyclerView.ViewHolder
    {
        TextView t_cap,t_desc;
        ImageView p_img;
        public Item(@NonNull View itemView) {
            super(itemView);
            t_cap=itemView.findViewById(R.id.txt_caption);
            t_desc=itemView.findViewById(R.id.txt_desc);
            p_img=itemView.findViewById(R.id.prob_img);
           // Toast.makeText(context, "in item", Toast.LENGTH_SHORT).show();
        }
    }
}
