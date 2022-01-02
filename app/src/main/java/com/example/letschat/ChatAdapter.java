package com.example.letschat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ChatAdapter extends RecyclerView.Adapter {
    ArrayList<MessagesModel> messagesModels;
    Context context;
    int SENDER_VIEW_TYPE = 1;
    int SENDER_VIEW_PICTYPE = 3;
    int RECEIVER_VIEW_TYPE = 2;
    int RECEIVER_VIEW_PICTYPE = 4;
    String senderRoom, receiverRoom;

    public ChatAdapter(ArrayList<MessagesModel> messagesModels, Context context, String senderRoom, String receiverRoom) {
        this.messagesModels = messagesModels;
        this.context = context;
        this.senderRoom = senderRoom;
        this.receiverRoom = receiverRoom;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == SENDER_VIEW_TYPE || viewType == SENDER_VIEW_PICTYPE) {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender, parent, false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver, parent, false);
            return new ReceiverViewHolder(view);
        }
    }


    @Override
    public int getItemViewType(int position) {
        if(messagesModels.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())) {
            if (messagesModels.get(position).getMessageType().equals("pic"))
                return SENDER_VIEW_PICTYPE;
            else
                return SENDER_VIEW_TYPE;
        }
        else {
            if (messagesModels.get(position).getMessageType().equals("pic"))
                return RECEIVER_VIEW_PICTYPE;
            else
                return RECEIVER_VIEW_TYPE;
        }


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
            if(pos<0)
                return false;
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
            return true;

        });


        if(holder.getClass()==SenderViewHolder.class){
            if(getItemViewType(position)==SENDER_VIEW_PICTYPE){
                ((SenderViewHolder)holder).imageViewChat.setVisibility(View.VISIBLE);
                ((SenderViewHolder)holder).senderMsg.setVisibility(View.GONE);
                Picasso.get().load(messagesModel.getImageUrl()).placeholder(R.drawable.imageplaceholder).into(((SenderViewHolder)holder).imageViewChat);
            }else{
                ((SenderViewHolder)holder).imageViewChat.setVisibility(View.GONE);
            }
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(messagesModel.getTimestamp());
            String time = DateFormat.format("dd-MM-yyyy  hh:mm:ss", calendar).toString();
            ((SenderViewHolder) holder).senderTime.setText(time);

            ((SenderViewHolder) holder).senderMsg.setText(messagesModel.getMessage());

            if(messagesModel.getFeelings()>=0) {
                try {
                    ((SenderViewHolder) holder).Senderfeelings.setImageResource(reactions[messagesModel.getFeelings()]);
                    ((SenderViewHolder) holder).Senderfeelings.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
            else{
                ((SenderViewHolder)holder).Senderfeelings.setVisibility(View.GONE);
            }
            try {
                ((SenderViewHolder) holder).senderMsg.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                            popup.onTouch(view, motionEvent);
                        return false;

                    }
                });
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }else {
            if(getItemViewType(position)==RECEIVER_VIEW_PICTYPE){
                ((ReceiverViewHolder)holder).imageViewChat.setVisibility(View.VISIBLE);
                ((ReceiverViewHolder)holder).receiverMsg.setVisibility(View.GONE);
                Picasso.get().load(messagesModel.getImageUrl()).placeholder(R.drawable.imageplaceholder).into(((ReceiverViewHolder)holder).imageViewChat);
            }else{
                ((ReceiverViewHolder)holder).imageViewChat.setVisibility(View.GONE);
            }
            Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
            calendar.setTimeInMillis(messagesModel.getTimestamp());
            String time = DateFormat.format("dd-MM-yyyy  hh:mm:ss", calendar).toString();
            ((ReceiverViewHolder) holder).receiverTime.setText(time);


            ((ReceiverViewHolder) holder).receiverMsg.setText(messagesModel.getMessage());
            if (messagesModel.getFeelings() >= 0) {
                try {
                    ((ReceiverViewHolder) holder).Receiverfeelings.setImageResource(reactions[messagesModel.getFeelings()]);
                    ((ReceiverViewHolder) holder).Receiverfeelings.setVisibility(View.VISIBLE);
                }catch (Exception e){
                    e.printStackTrace();
                }

            } else {
                ((ReceiverViewHolder) holder).Receiverfeelings.setVisibility(View.GONE);
            }
            try {
                ((ReceiverViewHolder) holder).receiverMsg.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        if(motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN )
                            popup.onTouch(view, motionEvent);
                        return false;
                    }


                });
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    @Override
    public int getItemCount() {
        return messagesModels.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder{
        TextView receiverMsg,receiverTime;
        ImageView Receiverfeelings,imageViewChat;


        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg=itemView.findViewById(R.id.receiverTextChatDetails);
            receiverTime=itemView.findViewById(R.id.receiverTimeChatDetail);
            Receiverfeelings=itemView.findViewById(R.id.feelingsReceiverChatDetails);
            imageViewChat=itemView.findViewById(R.id.receiverImage);


        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView senderMsg,senderTime;
        ImageView Senderfeelings,imageViewChat;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg=itemView.findViewById(R.id.senderText);
            senderTime=itemView.findViewById(R.id.senderTime);
            Senderfeelings=itemView.findViewById(R.id.feelingsSenderChatDetails);
            imageViewChat=itemView.findViewById(R.id.senderImage);
        }
    }
}