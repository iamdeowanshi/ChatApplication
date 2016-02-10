package com.mtvindia.connect.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.app.di.Injector;
import com.mtvindia.connect.data.model.ChatMessage;
import com.mtvindia.connect.data.model.User;
import com.mtvindia.connect.ui.activity.ChatActivity;
import com.mtvindia.connect.ui.custom.UbuntuTextView;
import com.mtvindia.connect.util.UserPreference;

import org.joda.time.DateTime;

import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import github.ankushsachdeva.emojicon.EmojiconTextView;

/**
 * @author Aaditya Deowanshi.
 *
 *         Adapter class to hold and display all messages for
 */

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    @Inject UserPreference userPreference;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_HEADER = 1;

    private User user;
    private Context context;
    private List<ChatMessage> chatMessages;

    public ChatMessageAdapter(Context context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_HEADER;

        DateTime currentDateTime = DateTime.parse(chatMessages.get(position - 1).getCreatedTime());
        DateTime messageDateTime = DateTime.parse(chatMessages.get(position).getCreatedTime());

        int value = messageDateTime.getDayOfYear() - currentDateTime.getDayOfYear();

        return (value != 0) ? TYPE_HEADER : TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Injector.instance().inject(this);
        user = userPreference.readUser();

        //Setting type of message view.
        switch (viewType) {
            case TYPE_HEADER:
                return new HeaderViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_header, parent, false));
            case TYPE_ITEM:
                return new ItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_layout, parent, false));
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_HEADER:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
                configureHeaderViewHolder(headerViewHolder, position);
                break;
            case TYPE_ITEM:
                ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                configureItemViewHolder(itemViewHolder, position);
                break;
        }
    }

    /**
     * Header view holder, to display the date and message.
     *
     * @param holder
     * @param position
     */
    private void configureHeaderViewHolder(HeaderViewHolder holder, int position) {
        DateTime currentDateTime = DateTime.now();
        DateTime messageDateTime = DateTime.parse(chatMessages.get(position).getCreatedTime());

        int differenceInDays = currentDateTime.getDayOfYear() - messageDateTime.getDayOfYear();

        if (differenceInDays == 0) {
            holder.txtHeader.setText("TODAY");

            return;
        }

        if (differenceInDays == 1) {
            holder.txtHeader.setText("YESTERDAY");

            return;
        }

        holder.txtHeader.setText(DateTime.parse(chatMessages.get(position).getCreatedTime()).toString("MMMM dd, YYYY"));

        /**
         * if messages are sent it will be displayed right side of the screen
         * else if messages are received it will be displayed left side of the screen.
         */
        if (chatMessages.get(position).getFrom().equals("webuser" + user.getId())) {
            holder.rightTxtMsg.setMaxWidth((int) (getWidth() * .6));
            holder.rightTxtLayout.setVisibility(View.VISIBLE);
            holder.leftTxtLayout.setVisibility(View.GONE);
            holder.rightTxtMsg.setText(chatMessages.get(position).getBody());
            holder.rightTxtTime.setText(getMessageTime(chatMessages.get(position).getCreatedTime()));
        } else {
            holder.leftTxtMsg.setMaxWidth((int) (getWidth() * .6));
            holder.rightTxtLayout.setVisibility(View.GONE);
            holder.leftTxtLayout.setVisibility(View.VISIBLE);
            holder.leftTxtMsg.setText(chatMessages.get(position).getBody());
            holder.leftTxtTime.setText(getMessageTime(chatMessages.get(position).getCreatedTime()));
        }
    }

    /**
     * View holder for normal messages.
     *
     * @param holder
     * @param position
     */
    private void configureItemViewHolder(ItemViewHolder holder, int position) {
        /**
         * if messages are sent it will be displayed right side of the screen
         * else if messages are received it will be displayed left side of the screen.
         */
        if (chatMessages.get(position).getFrom().equals("webuser" + user.getId())) {
            holder.rightTxtMsg.setMaxWidth((int) (getWidth() * .6));
            holder.rightTxtLayout.setVisibility(View.VISIBLE);
            holder.leftTxtLayout.setVisibility(View.GONE);
            holder.rightTxtMsg.setText(chatMessages.get(position).getBody());
            holder.rightTxtTime.setText(getMessageTime(chatMessages.get(position).getCreatedTime()));
        } else {
            holder.leftTxtMsg.setMaxWidth((int) (getWidth() * .6));
            holder.rightTxtLayout.setVisibility(View.GONE);
            holder.leftTxtLayout.setVisibility(View.VISIBLE);
            holder.leftTxtMsg.setText(chatMessages.get(position).getBody());
            holder.leftTxtTime.setText(getMessageTime(chatMessages.get(position).getCreatedTime()));
        }
    }

    /**
     * Returns the width of screen.
     *
     * @return
     */
    private int getWidth() {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    /**
     * To set the delivery status of the messages, if required.
     *
     * @param holder
     * @param position
     */
    private void setStatus(ItemViewHolder holder, int position) {
        if (chatMessages.get(position).getStatus().equals(ChatActivity.MessageState.Sending.toString())) {
            holder.rightTick.setImageResource(R.drawable.img_double_tick_grey);
        } else if (chatMessages.get(position).getStatus().equals(ChatActivity.MessageState.Sent.toString())) {
            holder.rightTick.setImageResource(R.drawable.img_double_tick_grey);
        } else if (chatMessages.get(position).getStatus().equals(ChatActivity.MessageState.Delivered.toString())) {
            holder.rightTick.setImageResource(R.drawable.img_double_tick_grey);
        } else if (chatMessages.get(position).getStatus().equals(ChatActivity.MessageState.Read.toString())) {
            holder.rightTick.setImageResource(R.drawable.img_double_tick_color);
        }
    }

    /**
     * Return time for message as string.
     *
     * @param time
     * @return
     */
    private String getMessageTime(String time) {
        DateTime dateTime = DateTime.parse(time);

        return dateTime.toString("hh:mm a");
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    /**
     * Holder class for normal message.
     */
    class ItemViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.left_txt_msg) EmojiconTextView leftTxtMsg;
        @Bind(R.id.left_txt_time) UbuntuTextView leftTxtTime;
        @Bind(R.id.left_txt_layout) RelativeLayout leftTxtLayout;
        @Bind(R.id.right_txt_msg) EmojiconTextView rightTxtMsg;
        @Bind(R.id.right_txt_time) UbuntuTextView rightTxtTime;
        @Bind(R.id.right_tick) ImageView rightTick;
        @Bind(R.id.right_txt_layout) RelativeLayout rightTxtLayout;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    /**
     * Holder class for, message with header.
     */
    class HeaderViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.txt_header) TextView txtHeader;
        @Bind(R.id.left_txt_msg) EmojiconTextView leftTxtMsg;
        @Bind(R.id.left_txt_time) UbuntuTextView leftTxtTime;
        @Bind(R.id.left_txt_layout) RelativeLayout leftTxtLayout;
        @Bind(R.id.right_txt_msg) EmojiconTextView rightTxtMsg;
        @Bind(R.id.right_txt_time) UbuntuTextView rightTxtTime;
        @Bind(R.id.right_tick) ImageView rightTick;
        @Bind(R.id.right_txt_layout) RelativeLayout rightTxtLayout;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}
