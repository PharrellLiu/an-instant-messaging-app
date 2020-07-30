package my.application;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatMessageViewHolder>{
    /**
     * this is the adapter of ChatActivity
     */

    private List<ChatMessage> mDataset;


    public ChatMessageAdapter(List<ChatMessage> myDataset){
        mDataset = myDataset;
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chat_message_recyclerview, parent, false);
        ChatMessageViewHolder vh = new ChatMessageViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        ChatMessage chatMessage = mDataset.get(position);
        int type = chatMessage.getType();
        if (type == 1) { // send
            holder.receive_layout.setVisibility(View.GONE);
            holder.textview_receive_name.setVisibility(View.GONE);
            holder.textview_receive_time.setVisibility(View.GONE);
            holder.textview_receive_message.setVisibility(View.GONE);

            holder.send_layout.setVisibility(View.VISIBLE);
            holder.textview_send_name.setVisibility(View.VISIBLE);
            holder.textview_send_time.setVisibility(View.VISIBLE);
            holder.textview_send_message.setVisibility(View.VISIBLE);

            holder.textview_send_name.setText(chatMessage.getName());
            holder.textview_send_time.setText(chatMessage.getTime());
            holder.textview_send_message.setText(chatMessage.getMessage());

        } else { // receive
            holder.receive_layout.setVisibility(View.VISIBLE);
            holder.textview_receive_name.setVisibility(View.VISIBLE);
            holder.textview_receive_time.setVisibility(View.VISIBLE);
            holder.textview_receive_message.setVisibility(View.VISIBLE);

            holder.send_layout.setVisibility(View.GONE);
            holder.textview_send_name.setVisibility(View.GONE);
            holder.textview_send_time.setVisibility(View.GONE);
            holder.textview_send_message.setVisibility(View.GONE);

            holder.textview_receive_name.setText(chatMessage.getName());
            holder.textview_receive_time.setText(chatMessage.getTime());
            holder.textview_receive_message.setText(chatMessage.getMessage());
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ChatMessage {
        // TYPE_RECEIVED = 0
        // TYPE_SEND = 1
        private int type;
        private String name;
        private String time;
        private String message;

        public ChatMessage(String name, String time, String message, int type) {
            this.message = message;
            this.type = type;
            this.name = name;
            this.time = time;
        }

        public String getName(){return name;}
        public String getTime(){return time;}
        public String getMessage(){return message;}
        public int getType(){return type;}
    }

    public static class ChatMessageViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout send_layout;
        public LinearLayout receive_layout;
        public TextView textview_receive_name;
        public TextView textview_receive_time;
        public TextView textview_receive_message;
        public TextView textview_send_name;
        public TextView textview_send_time;
        public TextView textview_send_message;

        public ChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            send_layout = itemView.findViewById(R.id.send_layout);
            receive_layout = itemView.findViewById(R.id.receive_layout);
            textview_receive_name = itemView.findViewById(R.id.textview_receive_name);
            textview_receive_time = itemView.findViewById(R.id.textview_receive_time);
            textview_receive_message = itemView.findViewById(R.id.textview_receive_message);
            textview_send_name = itemView.findViewById(R.id.textview_send_name);
            textview_send_time = itemView.findViewById(R.id.textview_send_time);
            textview_send_message = itemView.findViewById(R.id.textview_send_message);
        }
    }

}