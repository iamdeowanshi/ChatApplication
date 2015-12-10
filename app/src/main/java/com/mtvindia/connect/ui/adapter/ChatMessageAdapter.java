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

        holder.txtMsg.setMaxWidth((int) (getWidth() * .6));
        holder.txtMsg.setText(chatMessages.get(position).getBody());
        holder.txtTime.setText(getTime(chatMessages.get(position).getCreatedTime()));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)holder.txtLayout.getLayoutParams();

        if (chatMessages.get(position).getFrom().equals("webuser" + user.getId())) {
            setStatus(holder, position);
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            params.setMargins(0, 20, 27, 0 );
            holder.txtLayout.setBackgroundResource(R.drawable.msg_sent_bg);

        } else {
            holder.txtLayout.setBackgroundResource(R.drawable.msg_received_bg);
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            params.setMargins(22, 20, 0, 0 );
        }
    }

    private void setStatus(ViewHolder holder, int position) {
        if (chatMessages.get(position).getStatus().equals(ChatActivity.MessageState.Sending.toString())) {
            holder.imgStatus.setImageResource(R.drawable.icon_check);
        } else if (chatMessages.get(position).getStatus().equals(ChatActivity.MessageState.Sent.toString())) {
            holder.imgStatus.setImageResource(R.drawable.icon_double_tick);
        } else if (chatMessages.get(position).getStatus().equals(ChatActivity.MessageState.Delivered.toString())) {
            holder.imgStatus.setImageResource(R.drawable.icon_double_tick);
        } else if (chatMessages.get(position).getStatus().equals(ChatActivity.MessageState.Read.toString())) {
            holder.imgStatus.setImageResource(R.drawable.icon_double_tick);
        }
    }

    private String getTime(String time) {
        DateTime dateTime = DateTime.parse(time);
        return dateTime.toLocalTime().getHourOfDay() + ":" + dateTime.toLocalTime().getMinuteOfHour();
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.txt_msg)
        UbuntuTextView txtMsg;
        @Bind(R.id.txt_time)
        UbuntuTextView txtTime;
        @Bind(R.id.img_status)
        ImageView imgStatus;
        @Bind(R.id.txt_layout)
        RelativeLayout txtLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            txtMsg.setMaxWidth((int) (getWidth() * .6));
        }
    }
}
