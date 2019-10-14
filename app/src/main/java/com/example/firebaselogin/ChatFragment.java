package com.example.firebaselogin;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {

    private View ChatsView;
    private RecyclerView myChatList;
    private DatabaseReference Chatsref = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth;
    private String currentUserID;



    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        ChatsView =  inflater.inflate(R.layout.chat_tab, container, false);

        myChatList = (RecyclerView) ChatsView.findViewById(R.id.chat_list);
        myChatList.setLayoutManager(new LinearLayoutManager(getContext()));

        final String currentUniqueId = mAuth.getCurrentUser().getUid();

        Chatsref.child("Users");

        return  ChatsView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}
