package com.mtvindia.connect.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

/**
 * Created by Sibi on 01/12/15.
 */
public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    @Inject
    UserPreference userPreference;

    private Context context;
    private List<ChatMessage> chatMessages;
    private User user;

    public ChatMessageAdapter(Context context, List<ChatMessage> chatMessages) {
        this.context = context;
        this.chatMessages = chatMessages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_layout, parent, false);
        Injector.instance().inject(this);

        user = userPreference.readUser();

        return new ViewHolder(view);
    }

    private int getWidth() {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return metrics.widthPixels;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

      /*  RelativeLayout.LayoutParams leftParams = (RelativeLayout.LayoutParams)holder.leftTxtLayout.getLayoutParams();
        RelativeLayout.LayoutParams rightParams = (RelativeLayout.LayoutParams)holder.rightTxtLayout.getLayoutParams();
*/
        if (chatMessages.get(position).getFrom().equals("webuser" + user.getId())) {
            holder.rightTxtMsg.setMaxWidth((int)(getWidth()* .6));
            holder.rightTxtLayout.setVisibility(View.VISIBLE);
            holder.leftTxtLayout.setVisibility(View.GONE);
            holder.rightTxtMsg.setText(chatMessages.get(position).getBody());
            //setStatus(holder, position);
            holder.rightTxtTime.setText(getTime(chatMessages.get(position).getCreatedTime()));
        } else {
            holder.leftTxtMsg.setMaxWidth((int)(getWidth()*.6));
            holder.rightTxtLayout.setVisibility(View.GONE);
            holder.leftTxtLayout.setVisibility(View.VISIBLE);
            holder.leftTxtMsg.setText(chatMessages.get(position).getBody());
            holder.leftTxtTime.setText(getTime(chatMessages.get(position).getCreatedTime()));
        }
    }

    private void setStatus(ViewHolder holder, int position) {
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

    private String getTime(String time) {
        DateTime dateTime = DateTime.parse(time);
        return dateTime.toString().split("T")[1].substring(0,5);
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.left_txt_msg)
        UbuntuTextView leftTxtMsg;
        @Bind(R.id.left_txt_time)
        UbuntuTextView leftTxtTime;
        @Bind(R.id.left_txt_layout)
        RelativeLayout leftTxtLayout;
        @Bind(R.id.right_txt_msg)
        UbuntuTextView rightTxtMsg;
        @Bind(R.id.right_txt_time)
        UbuntuTextView rightTxtTime;
        @Bind(R.id.right_tick)
        ImageView rightTick;
        @Bind(R.id.right_txt_layout)
        RelativeLayout rightTxtLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
