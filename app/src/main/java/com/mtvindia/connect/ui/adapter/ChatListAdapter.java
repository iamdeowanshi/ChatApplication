package com.mtvindia.connect.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.implments.SwipeItemRecyclerMangerImpl;
import com.mtvindia.connect.R;
import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.ui.activity.ChatCallBack;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by Sibi on 26/11/15.
 */
public class ChatListAdapter extends RecyclerSwipeAdapter<ChatListAdapter.ViewHolder> {

    @Inject ChatListRepository chatListRepository;

    private List<ChatList> chatList;
    private Context context;
    private ChatMessage chatMessage;
    private ChatCallBack chatCallBack;
    private int count = 0;

    protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

    public ChatListAdapter(Context context, List<ChatList> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_recycler_view_item, parent, false);
        Injector.instance().inject(this);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatListAdapter.ViewHolder holder, final int position) {
        count++;
        Picasso.with(context).load(chatList.get(position).getImage()).fit().into(holder.imageDp);
        holder.txtName.setText(chatList.get(position).getName());
        chatMessage = lastMessage(chatList.get(position).getId());
        holder.txtChat.setText(chatMessage.getBody());

        holder.txtTime.setText(getTime(chatMessage.getCreatedTime()));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatCallBack.onItemSelected(chatList.get(position));

            }
        });

        if (count == getItemCount()) {
            holder.view.setVisibility(View.INVISIBLE);
        }

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.swipeLayout.addSwipeListener(new SimpleSwipeListener() {
            @Override
            public void onOpen(SwipeLayout layout) {
                holder.itemView.setClickable(false);
                YoYo.with(Techniques.Tada).duration(500).delay(100).playOn(layout.findViewById(R.id.trash));

            }

            @Override
            public void onClose(SwipeLayout layout) {
                super.onClose(layout);
                holder.itemView.setClickable(true);
            }
        });

        holder.trash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Timber.d("deleted");
            }
        });

        mItemManger.bindView(holder.itemView, position);
    }


    private ChatMessage lastMessage(int userId) {
       return chatListRepository.lastMessage(userId);

    }

    private String getTime(String time) {
        DateTime now = DateTime.now();
        DateTime last = DateTime.parse(time);

        Period period = new Period(last, now);

        long diffInMin = period.getMinutes();
        long diffInHours = period.getHours();
        long diffInDays = period.getDays();
        long diffInMonths = period.getMonths();

        if (diffInMonths > 12) {
            return "long time back";
        } else if (diffInMonths < 12 && diffInMonths > 0) {
            return diffInMonths + " months ago";
        } else if (diffInDays > 0 && diffInDays < 30) {
            return diffInDays + " days ago";
        } else if (diffInHours > 0 && diffInHours < 24) {
            return diffInHours + " hours ago";
        } else if (diffInMin > 0 && diffInMin < 60) {
            return diffInMin + " mins ago";
        } else {
            return "moments ago";
        }
    }

    public void setChatCallBack(ChatCallBack chatCallBack) {
        this.chatCallBack = chatCallBack;
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.img_dp)
        ImageView imageDp;
        @Bind(R.id.txt_name)
        TextView txtName;
        @Bind(R.id.txt_chat)
        TextView txtChat;
        @Bind(R.id.right_txt_time)
        TextView txtTime;
        @Bind(R.id.swipe)
        SwipeLayout swipeLayout;
        @Bind(R.id.view)
        View view;
        @Bind(R.id.trash)
        ImageButton trash;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
