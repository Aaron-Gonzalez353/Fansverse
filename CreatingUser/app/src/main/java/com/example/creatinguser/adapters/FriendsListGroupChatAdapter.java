package com.example.creatinguser.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.creatinguser.Models.ChatMessage;
import com.example.creatinguser.Models.User;
import com.example.creatinguser.databinding.ItemContainerReceivedMessageBinding;
import com.example.creatinguser.databinding.ItemContainerSentMessageBinding;
import com.example.creatinguser.databinding.ItemContainerUserBinding;
import com.example.creatinguser.listeners.UserListener;

import java.util.List;

public class FriendsListGroupChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<User> users;
    private final UserListener userListener;

    public FriendsListGroupChatAdapter(List<User> users, UserListener userListener){
        this.users = users;
        this.userListener = userListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    class FriendlistViewHolder extends RecyclerView.ViewHolder {

        ItemContainerUserBinding binding;

        FriendlistViewHolder(ItemContainerUserBinding itemContainerUserBinding) {
            super(itemContainerUserBinding.getRoot());
            binding = itemContainerUserBinding;
        }

        void setUserData(User user) {
            binding.textName.setText(user.name);
            binding.textEmail.setText(user.email);
//            binding.imageProfile.setImageBitmap(getUserImage(user.image));
            binding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));
        }
    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, android.util.Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }
}
