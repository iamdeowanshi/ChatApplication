package com.mtvindia.connect.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.model.ChatList;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.repository.ChatListRepository;
import com.mtvindia.connect.ui.activity.ChatCallBack;
import com.mtvindia.connect.util.DialogUtil;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sibi on 26/11/15.
 */
public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    @Inject ChatListRepository chatListRepository;
    @Inject
    DialogUtil dialogUtil;

    private List<ChatList> chatList;
    private Context context;
    private ChatMessage chatMessage;
    private ChatCallBack chatCallBack;
    private int count = 0;

   // protected SwipeItemRecyclerMangerImpl mItemManger = new SwipeItemRecyclerMangerImpl(this);

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
        holder.txtChat.setText(getText(chatMessage));

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

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                final android.app.AlertDialog alertDialog = (android.app.AlertDialog) dialogUtil.createAlertDialog((Activity) context, "Delete Chat", "Are you sure you want to delete chat", "Yes", "No");
                alertDialog.show();
                alertDialog.setCancelable(true);
                Button positiveButton = (Button) alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                Button negativeButton = (Button) alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        chatListRepository.remove(chatList.get(position).getId());
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, chatList.size());
                        alertDialog.dismiss();
                    }
                });

                negativeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog.dismiss();
                    }
                });
                return true;
            }
        });
    }

    private String getText(ChatMessage chatMessage) {
        if (chatMessage.getBody().length() > 15) {
            return chatMessage.getBody().substring(0,12) + "...";
        }

        return chatMessage.getBody();
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
        long diffInWeeks = period.getWeeks();
        long diffInMonths = period.getMonths();

        if (diffInMonths > 12) {
            return "long time back";
        } else if (diffInMonths < 12 && diffInMonths > 0) {
            return diffInMonths + " months ago";
        } else if (diffInWeeks > 0 && diffInWeeks < 5) {
            return diffInWeeks + " weeks ago";
        } else if (diffInDays > 0 && diffInDays < 6) {
            return diffInDays + " days ago";
        } else if (diffInHours > 0 && diffInHours < 24) {
            return diffInHours + " hours ago";
        } else if (diffInMin > 0 && diffInMin < 60) {
            return diffInMin + " mins ago";
        } else {
            return "now";
        }
    }

    public void setChatCallBack(ChatCallBack chatCallBack) {
        this.chatCallBack = chatCallBack;
    }

    @Override
    public int getItemCount() {
        return chatList.size();
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
        @Bind(R.id.view)
        View view;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
