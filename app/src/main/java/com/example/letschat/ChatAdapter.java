package com.example.letschat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.MODELS.MessagesModel;
import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatAdapter extends RecyclerView.Adapter{
    ArrayList<MessagesModel> messagesModels;
    Context context;
    int SENDER_VIEW_TYPE =1;
    int RECEIVER_VIEW_TYPE=2;
    String senderRoom,receiverRoom;

    public ChatAdapter(ArrayList<MessagesModel> messagesModels, Context context,String senderRoom,String receiverRoom) {
        this.messagesModels = messagesModels;
        this.context = context;
        this.senderRoom=senderRoom;
        this.receiverRoom=receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SENDER_VIEW_TYPE)
        {
            View view= LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewHolder(view);
        }
        else{
            View view= LayoutInflater.from(context).inflate(R.layout.sample_receiver,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messagesModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid()))
            return SENDER_VIEW_TYPE;
        else
            return  RECEIVER_VIEW_TYPE;


    }
    int reactions[]= new int[]{
            R.drawable.ic_fb_like,
            R.drawable.ic_fb_love,
            R.drawable.ic_fb_laugh,
            R.drawable.ic_fb_wow,
            R.drawable.ic_fb_sad,
            R.drawable.ic_fb_angry
    };

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MessagesModel messagesModel=messagesModels.get(position);
        ReactionsConfig config = new ReactionsConfigBuilder(context)
                .withReactions(reactions)
                .build();


        ReactionPopup popup = new ReactionPopup(context, config, (pos) -> {
            if(holder.getClass()==SenderViewHolder.class){
                ((SenderViewHolder)holder).Senderfeelings.setImageResource(reactions[pos]);
                ((SenderViewHolder)holder).Senderfeelings.setVisibility(View.VISIBLE);

            }
            else{
                ((ReceiverViewHolder)holder).Receiverfeelings.setImageResource(reactions[pos]);
                ((ReceiverViewHolder)holder).Receiverfeelings.setVisibility(View.VISIBLE);
            }
            messagesModel.setFeelings(pos);
            FirebaseDatabase.getInstance().getReference()
                    .child("Chats")
                    .child(senderRoom)
                    .child(messagesModel.getMessageId()).setValue(messagesModel);
            FirebaseDatabase.getInstance().getReference()
                    .child("Chats")
                    .child(receiverRoom)
                    .child(messagesModel.getMessageId()).setValue(messagesModel);
            return true; // true is closing popup, false is requesting a new selection

        });
        if(holder.getClass()==SenderViewHolder.class){
            ((SenderViewHolder)holder).senderMsg.setText(messagesModel.getMessage());

            if(messagesModel.getFeelings()>=0) {
                //messagesModel.setFeelings(reactions[messagesModel.getFeelings()]);
                ((SenderViewHolder)holder).Senderfeelings.setImageResource(reactions[messagesModel.getFeelings()]);
                ((SenderViewHolder)holder).Senderfeelings.setVisibility(View.VISIBLE);
            }
            else{
                ((SenderViewHolder)holder).Senderfeelings.setVisibility(View.GONE);
            }
            ((SenderViewHolder)holder).senderMsg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });

        }else {
            ((ReceiverViewHolder)holder).receiverMsg.setText(messagesModel.getMessage());
            if(messagesModel.getFeelings()>=0) {
                ((ReceiverViewHolder)holder).Receiverfeelings.setImageResource(reactions[messagesModel.getFeelings()]);
                ((ReceiverViewHolder)holder).Receiverfeelings.setVisibility(View.VISIBLE);
            }
            else{
                ((ReceiverViewHolder)holder).Receiverfeelings.setVisibility(View.GONE);
            }
            ((ReceiverViewHolder)holder).receiverMsg.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    popup.onTouch(view,motionEvent);
                    return false;
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return messagesModels.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView receiverMsg,receiverTime;
        ImageView Receiverfeelings;


        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg=itemView.findViewById(R.id.receiverTextChatDetails);
            receiverTime=itemView.findViewById(R.id.receiverTimeChatDetail);
            Receiverfeelings=itemView.findViewById(R.id.feelingsReceiverChatDetails);

        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView senderMsg,senderTime;
        ImageView Senderfeelings;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg=itemView.findViewById(R.id.senderText);
            senderTime=itemView.findViewById(R.id.senderTime);
            Senderfeelings=itemView.findViewById(R.id.feelingsSenderChatDetails);
        }
    }
}
