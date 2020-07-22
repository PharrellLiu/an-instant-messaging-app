package my.application;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CreateChatroomAdapter extends RecyclerView.Adapter<CreateChatroomAdapter.CreateChatroomViewHolder> {

    private List<Member> mDataset;

    public CreateChatroomAdapter(List<Member> myDataset){
        mDataset = myDataset;
    }

    @NonNull
    @Override
    public CreateChatroomAdapter.CreateChatroomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  LayoutInflater.from(parent.getContext())
                .inflate(R.layout.create_chatroom_recyclerview, parent, false);
        CreateChatroomViewHolder vh = new CreateChatroomViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final CreateChatroomAdapter.CreateChatroomViewHolder holder, int position) {
        final Member member = mDataset.get(position);
        holder.textView.setText(member.getName());
        holder.checkBox.setChecked(member.getIsEnter());
        holder.textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               member.setIsEnter();
               holder.checkBox.setChecked(member.getIsEnter());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public static class Member {
        private String name;
        private boolean isEnter;
        public Member(String name,Boolean isEnter){
            this.isEnter = isEnter;
            this.name = name;
        }

        public void setIsEnter() {isEnter = !isEnter;}
        public void setIsEnter2(boolean isEnter2){this.isEnter = isEnter2;}
        public String getName() {return name;}
        public boolean getIsEnter() {return isEnter;}
    }

    public static class CreateChatroomViewHolder extends RecyclerView.ViewHolder{
        public CheckBox checkBox;
        public TextView textView;
        public CreateChatroomViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox_choose_name);
            textView = itemView.findViewById(R.id.textview_choose_name);
        }
    }
}
