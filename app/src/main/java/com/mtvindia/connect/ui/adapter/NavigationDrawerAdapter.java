package com.mtvindia.connect.ui.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mtvindia.connect.R;
import com.mtvindia.connect.ui.activity.NavigationCallBack;
import com.mtvindia.connect.ui.activity.NavigationItem;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Sibi on 13/10/15.
 */
public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {

    private NavigationCallBack navigationCallbacks;
    private List<NavigationItem> navigationItems;

    private int selectedPosition;
    private int touchedPosition = -1;

    public NavigationDrawerAdapter(List<NavigationItem> items) {
        navigationItems = items;
    }

    @Override
    public NavigationDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycler_view_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(NavigationDrawerAdapter.ViewHolder holder, int position) {
        holder.textView.setText(navigationItems.get(position).getText());
        holder.imageView.setImageResource(navigationItems.get(position).getIcon());

        handleRowEvents(holder.itemView, position);
        if (selectedPosition == position || touchedPosition == position) {
            holder.itemView.setBackgroundResource(R.color.selected_color);
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    @Override
    public int getItemCount() {
        return navigationItems != null ? navigationItems.size() : 0;
    }

    public void setNavigationCallbacks(NavigationCallBack navigationCallbacks) {
        this.navigationCallbacks = navigationCallbacks;
    }

    private void handleRowEvents(View itemView, final int position) {
        itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        touchPosition(position);
                        return false;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        touchPosition(-1);
                        return false;
                    case MotionEvent.ACTION_MOVE:
                        return false;
                }

                return true;
            }
        });

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (navigationCallbacks != null)
                    navigationCallbacks.onItemSelected(navigationItems.get(position));
            }
        });
    }

    private void touchPosition(int position) {
        int lastPosition = touchedPosition;
        touchedPosition = position;

        if (lastPosition >= 0) {
            notifyItemChanged(lastPosition);
        }

        if (position >= 0) {
            notifyItemChanged(position);
        }
    }

    public void setSelectedItem(NavigationItem item) {
        int position = navigationItems.indexOf(item);
        int lastPosition = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(lastPosition);
        notifyItemChanged(position);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.txt_item_name)
        TextView textView;
        @Bind(R.id.img_item_icon)
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }

}

