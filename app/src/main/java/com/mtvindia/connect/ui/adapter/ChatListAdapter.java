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
import com.mtvindia.connect.data.repository.ChatMessageRepository;
import com.mtvindia.connect.presenter.ChatListPresenter;
import com.mtvindia.connect.ui.callbacks.ChatCallBack;
import com.mtvindia.connect.util.DialogUtil;
import com.mtvindia.connect.util.UserPreference;
import com.squareup.picasso.Picasso;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import github.ankushsachdeva.emojicon.EmojiconTextView;

/**
 * @author Aaditya Deowanshi
 *
 *         Adapter class to hold and display chat list.
 */

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ViewHolder> {

    @Inject ChatListRepository chatListRepository;
    @Inject ChatMessageRepository chatMessageRepository;
    @Inject DialogUtil dialogUtil;
    @Inject UserPreference userPreference;
    @Inject ChatListPresenter chatListPresenter;

    private Context context;
    private List<ChatList> chatList;
    private ChatMessage chatMessage;
    private ChatCallBack chatCallBack;

    public ChatListAdapter(Context context, List<ChatList> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @Override
    public ChatListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_list_recycler_view_item, parent, false);
        Injector.instance().inject(this);

        //Reinitializing realm threads.
        chatMessageRepository.reInitialize();
        chatListRepository.reInitialize();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ChatListAdapter.ViewHolder holder, final int position) {
        Picasso.with(context).load(chatList.get(position).getImage()).fit().into(holder.imageDp);
        holder.txtName.setText(chatList.get(position).getName());

        chatMessage = lastMessage(chatList.get(position).getUserId());

        holder.txtChat.setText(getLastMessageText(chatMessage));
        holder.txtTime.setText(getTime(chatMessage.getCreatedTime()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chatCallBack.onItemSelected(chatList.get(position));
            }
        });

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
                        chatListPresenter.removeUser(chatList.get(position).getUserId(), userPreference.readUser().getAuthHeader());
                        chatMessageRepository.removeAllMessage(chatList.get(position).getUserId(), userPreference.readUser().getId());
                        chatListRepository.remove(chatList.get(position).getUserId(), userPreference.readUser().getId());

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

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    /**
     * Returns text of last message for a particular user.
     * @param chatMessage
     * @return
     */
    private String getLastMessageText(ChatMessage chatMessage) {
        if (chatMessage.getBody().length() > 15) {
            return chatMessage.getBody().substring(0, 12) + "...";
        }

        return chatMessage.getBody().replace("\n", " ");
    }

    /**
     * Returns last message object for a particular userId.
     * @param userId
     * @return
     */
    private ChatMessage lastMessage(int userId) {
        return chatListRepository.lastMessage(userId, userPreference.readUser().getId());
    }

    /**
     * Returns the time duration from last message.
     * @param time
     * @return
     */
    private String getTime(String time) {
        if (time.equals("")) return "";

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
        }

        if (diffInMonths < 12 && diffInMonths > 0) {
            return diffInMonths + " months ago";
        }

        if (diffInWeeks > 0 && diffInWeeks < 5) {
            return diffInWeeks + " weeks ago";
        }

        if (diffInDays > 0 && diffInDays < 6) {
            return diffInDays + " days ago";
        }

        if (diffInHours > 0 && diffInHours < 24) {
            return diffInHours + " hours ago";
        }

        if (diffInMin > 0 && diffInMin < 60) {
            return diffInMin + " mins ago";
        }

        return "now";
    }

    public void setChatCallBack(ChatCallBack chatCallBack) {
        this.chatCallBack = chatCallBack;
    }

    /**
     * View Holder class.
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.img_dp) ImageView imageDp;
        @Bind(R.id.txt_name) TextView txtName;
        @Bind(R.id.txt_chat) EmojiconTextView txtChat;
        @Bind(R.id.right_txt_time) TextView txtTime;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
