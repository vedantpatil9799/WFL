package wayforlife.com.wfl.Adapter_class;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import wayforlife.com.wfl.Modal_class.single_comment;
import wayforlife.com.wfl.R;

public class commentAdapter extends RecyclerView.Adapter {

    ArrayList<single_comment> list;
    Context mContext;

    public commentAdapter(ArrayList<single_comment> list, Context mContext)
    {
        this.list=list;
        this.mContext=mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       View view= LayoutInflater.from(mContext).inflate(R.layout.single_comment_layout,viewGroup,false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        ((CommentViewHolder)viewHolder).comment.setText(list.get(i).comment);
        ((CommentViewHolder)viewHolder).userid.setText(list.get(i).userid);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }



    public static class CommentViewHolder extends RecyclerView.ViewHolder
    {
        TextView userid,comment;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);

            userid=itemView.findViewById(R.id.comment_userid);
            comment=itemView.findViewById(R.id.comment_text);
        }
    }
}
