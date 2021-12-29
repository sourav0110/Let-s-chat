package com.example.letschat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.MODELS.ChatRoomMessageModel;
import com.example.letschat.MODELS.MessagesModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ChatRoomAdapter extends RecyclerView.Adapter{
    ArrayList<ChatRoomMessageModel> messagesModels;
    Context context;
    int SENDER_VIEW_TYPE =1;
    int RECEIVER_VIEW_TYPE=2;



    public ChatRoomAdapter(ArrayList<ChatRoomMessageModel> messagesModels, Context context) {
        this.messagesModels = messagesModels;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SENDER_VIEW_TYPE)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderChatRoomViewHolder(view);
        }
        else{
            View view= LayoutInflater.from(context).inflate(R.layout.sample_chatroom_receiver,parent,false);
            return new ReceiverChatRoomViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messagesModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid()))
            return SENDER_VIEW_TYPE;
        else
            return  RECEIVER_VIEW_TYPE;


    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatRoomMessageModel messagesModel=messagesModels.get(position);

        if(holder.getClass()==SenderChatRoomViewHolder.class){
            ((SenderChatRoomViewHolder)holder).senderMsg.setText(messagesModel.getMessage());
        }else {
            ((ReceiverChatRoomViewHolder)holder).receiverMsg.setText(messagesModel.getMessage());
            ((ReceiverChatRoomViewHolder)holder).senderName.setText(messagesModel.getUserName());

        }

    }

    @Override
    public int getItemCount() {
        return messagesModels.size();
    }

    public class ReceiverChatRoomViewHolder extends RecyclerView.ViewHolder{
        TextView receiverMsg,receiverTime,senderName;


        public ReceiverChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg=itemView.findViewById(R.id.receiverTextChatRoom);
            receiverTime=itemView.findViewById(R.id.receiverTimeChatRoom);
            senderName=itemView.findViewById(R.id.SenderNameChatRoom);

        }
    }
    public class SenderChatRoomViewHolder extends RecyclerView.ViewHolder{
        TextView senderMsg,senderTime;

        public SenderChatRoomViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg=itemView.findViewById(R.id.senderText);
            senderTime=itemView.findViewById(R.id.senderTime);
        }
    }
}

