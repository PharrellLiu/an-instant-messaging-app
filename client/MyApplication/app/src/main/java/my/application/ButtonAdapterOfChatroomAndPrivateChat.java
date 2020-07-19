package my.application;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ButtonAdapterOfChatroomAndPrivateChat extends RecyclerView.Adapter<ButtonAdapterOfChatroomAndPrivateChat.ButtonViewHolderOfChatroomAndPrivateChat>{

    private List<String> mDataset;
    private Context mContext;
    private int isChatroom; // 1 is chatroom, 0 is private chat

    public ButtonAdapterOfChatroomAndPrivateChat(List<String> myDataset,Context context,int num) {
        mDataset = myDataset;
        mContext = context;
        isChatroom = num;
    }
    
    @NonNull
    @Override
    public ButtonAdapterOfChatroomAndPrivateChat.ButtonViewHolderOfChatroomAndPrivateChat onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.button_recyclerview_chatroom_and_private_chat, parent, false);
        ButtonViewHolderOfChatroomAndPrivateChat vh = new ButtonViewHolderOfChatroomAndPrivateChat(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ButtonAdapterOfChatroomAndPrivateChat.ButtonViewHolderOfChatroomAndPrivateChat holder, final int position) {
        holder.button.setText(mDataset.get(position));
        holder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext,mDataset.get(position),Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ButtonViewHolderOfChatroomAndPrivateChat extends RecyclerView.ViewHolder {
        public Button button;
        public ButtonViewHolderOfChatroomAndPrivateChat(@NonNull View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.button_chatroom_and_private_chat);
        }
    }
}
