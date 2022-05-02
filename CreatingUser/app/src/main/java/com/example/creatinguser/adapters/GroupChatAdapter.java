//package com.example.creatinguser.adapters;
//
//import android.view.LayoutInflater;
//import android.view.ViewGroup;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.creatinguser.Models.ChatMessage;
//import com.example.creatinguser.Models.GroupChatMessage;
//import com.example.creatinguser.databinding.ItemContainerReceivedMessageBinding;
//import com.example.creatinguser.databinding.ItemContainerSentMessageBinding;
//
//import java.util.List;
//
//public class GroupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
//
//    private final List<GroupChatMessage> chatMessages;
//    //    private final Bitmap receiverProfileImage;
////    private final List<String> groupIds;
//    private final String senderId;
//
//    public static final int VIEW_TYPE_SENT = 1;
//    public static final int VIEW_TYPE_RECEIVED = 2;
//
//    public GroupChatAdapter(List<GroupChatMessage> chatMessages, String senderId){//} Bitmap receiverProfileImage, String senderId) {
//        this.chatMessages = chatMessages;
////        this.receiverProfileImage = receiverProfileImage;
//        this.senderId = senderId;
//    }
//
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        if (viewType == VIEW_TYPE_SENT){
//            return new SentMessageViewHolder(
//                    ItemContainerSentMessageBinding.inflate(
//                            LayoutInflater.from(parent.getContext()),
//                            parent,
//                            false
//                    )
//            );
//        }else {
//            return new RecivedMessageViewholder(
//                    ItemContainerReceivedMessageBinding.inflate(
//                            LayoutInflater.from(parent.getContext()),
//                            parent,
//                            false
//                    )
//            );
//        }
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        if (getItemViewType(position) == VIEW_TYPE_SENT){
//            ((SentMessageViewHolder) holder).setData(chatMessages.get(position));
//        }else {
//            ((RecivedMessageViewholder) holder).setData(chatMessages.get(position));//, receiverProfileImage);
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return chatMessages.size();
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (chatMessages.get(position).senderId.equals(senderId)){
//            return VIEW_TYPE_SENT;
//        }else{
//            return VIEW_TYPE_RECEIVED;
//        }
//    }
//
//    static class SentMessageViewHolder extends RecyclerView.ViewHolder{
//
//        private final ItemContainerSentMessageBinding binding;
//
//        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding){
//            super(itemContainerSentMessageBinding.getRoot());
//            binding = itemContainerSentMessageBinding;
//        }
//
//        void setData(GroupChatMessage chatMessage){
//            binding.textMessage.setText(chatMessage.message);
//            binding.textDataTime.setText(chatMessage.dateTime);
//        }
//    }
//
//    static class RecivedMessageViewholder extends RecyclerView.ViewHolder{
//
//        private final ItemContainerReceivedMessageBinding binding;
//
//        RecivedMessageViewholder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding){
//            super(itemContainerReceivedMessageBinding.getRoot());
//            binding = itemContainerReceivedMessageBinding;
//        }
//
//        void setData(GroupChatMessage chatMessage){//}, Bitmap receiverProfileImage){
//            binding.textMessage.setText(chatMessage.message);
//            binding.textDateTime.setText(chatMessage.dateTime);
////            binding.imageProfile.setImageBitmap(receiverProfileImage);
//        }
//    }
//}
