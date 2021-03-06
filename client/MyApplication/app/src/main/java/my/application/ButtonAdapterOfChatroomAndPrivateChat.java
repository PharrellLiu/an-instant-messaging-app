package my.application;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ButtonAdapterOfChatroomAndPrivateChat extends RecyclerView.Adapter<ButtonAdapterOfChatroomAndPrivateChat.ButtonViewHolderOfChatroomAndPrivateChat>{
    /**
     * this is adapter of both chatroom fragment and private chat fragment
     * however, the name of this adapter is weird, there are textviews not buttons
     * because I set them as button firstly, and change it
     **/

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
        final String nameOfChatroomOrFri = mDataset.get(position);
        holder.textView.setText(nameOfChatroomOrFri);
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext,ChatActivity.class);
                intent.putExtra("isNewChatroom",0);// if it is the init of a new chatroom, when we press the back button, it should not back to create new chatroom activity but home activity
                intent.putExtra("isChatroom",isChatroom);
                intent.putExtra("nameOfChatroomOrFri",nameOfChatroomOrFri);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class ButtonViewHolderOfChatroomAndPrivateChat extends RecyclerView.ViewHolder {
        public TextView textView;
        public ButtonViewHolderOfChatroomAndPrivateChat(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textview_chatroom_and_private_chat);
        }
    }
}
