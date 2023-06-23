package com.example.zenaparty;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.MyViewHolder> {
    Context context;

    ArrayList<MyEvent> list;


    public EventListAdapter(Context context, ArrayList<MyEvent> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.event_item_layout,parent,false);
        return  new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventListAdapter.MyViewHolder holder, int position) {
        MyEvent event = list.get(position);
        holder.event_name.setText(event.getEvent_name());
        holder.time.setText(event.getTime());
        holder.location.setText(event.getLocation());
        holder.type.setText(event.getType());
        holder.price.setText(event.getPrice());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView event_name, time, location, type, price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            event_name = itemView.findViewById(R.id.eventName);
            time = itemView.findViewById(R.id.eventTime);
            location = itemView.findViewById(R.id.eventLocation);
            type = itemView.findViewById(R.id.eventType);
            price = itemView.findViewById(R.id.eventPrice);

        }
    }

    public void setEventList(ArrayList<MyEvent> eventList) {
        this.list = eventList;
    }

}
