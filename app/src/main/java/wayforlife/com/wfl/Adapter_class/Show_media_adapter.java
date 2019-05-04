package wayforlife.com.wfl.Adapter_class;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import wayforlife.com.wfl.Modal_class.Store_media_modal_class;
import wayforlife.com.wfl.R;

public class Show_media_adapter extends RecyclerView.Adapter<Show_media_adapter.Item> {

    Context context;
    List<Store_media_modal_class> mData;

    public Show_media_adapter(Context context, List<Store_media_modal_class> mData) {
        this.context = context;
        this.mData = mData;
    }

    @NonNull
    @Override
    public Item onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.activity_show_media_adapter,parent,false);
        context=parent.getContext();
        return new Show_media_adapter.Item(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Item holder, int position) {
        Picasso.with(context).load(mData.get(position).getUrl()).into(holder.media);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class Item extends RecyclerView.ViewHolder {
        ImageView media;
        public Item(View itemView) {
            super(itemView);
            media=itemView.findViewById(R.id.image_media);
        }
    }
}
