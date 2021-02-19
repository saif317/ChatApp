package com.example.chatapp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.chatapp.Common.Common;
import com.example.chatapp.Model.UserModel;
import com.example.chatapp.R;
import com.example.chatapp.ViewHolder.UserViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PeopleFragment extends Fragment {

    @BindView(R.id.people_recycler)
    RecyclerView people_recycler;
    FirebaseRecyclerAdapter firebaseRecyclerAdapter; // This class is a generic way of backing a RecyclerView with a Firebase location
    private Unbinder unbinder; // An unbinder contract that will unbind views when called.

    private PeopleViewModel pViewModel;

    static PeopleFragment instance;

    public static PeopleFragment getInstance() {
        return instance == null ? new PeopleFragment() : instance;
    }

    @Override
    // Creates and returns the view hierarchy associated with the fragment
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View itemView = inflater.inflate(R.layout.people_fragment, container, false); // Load the People fragment
        initializeView(itemView);
        loadPeople();
        return itemView;
    }

    private void loadPeople() {
        //(Query) Used for reading data. Listeners are attached, and they will be triggered when the corresponding data changes
        Query query = FirebaseDatabase.getInstance().getReference().child(Common.USER_REFERENCES);
        FirebaseRecyclerOptions<UserModel> options = new FirebaseRecyclerOptions.Builder<UserModel>().setQuery(query, UserModel.class).build(); // Build the "people" options using the user model
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<UserModel, UserViewHolder>(options) { // Customize the look of the items

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { // View holder for the design of the items
               View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_people,parent,false);
               return new UserViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder holder, int position, @NonNull UserModel model) {
                if (!firebaseRecyclerAdapter.getRef(position).getKey().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){ // Get all users except current user
                    // amulyakhare/TextDrawable Library
                    // Item Color
                    ColorGenerator colorGenerator=ColorGenerator.MATERIAL;
                    int color=colorGenerator.getColor(FirebaseAuth.getInstance().getCurrentUser().getUid()); // generate color based on UID
                    TextDrawable.IBuilder iBuilder=TextDrawable.builder().beginConfig().withBorder(4).endConfig().round(); // Design the look of the item
                    TextDrawable textDrawable=iBuilder.build(model.getFirstName().substring(0,1),color); // Take the the first letter of the first name
                    holder.avatar_image.setImageDrawable(textDrawable); // Assign letter to the avatar image
                    StringBuilder stringBuilder=new StringBuilder(); // A mutable sequence of characters
                    stringBuilder.append(model.getFirstName()).append(" ").append(model.getLastName()); // First name + Last name
                    holder.name_text.setText(stringBuilder.toString());  // Set name
                    holder.bio_text.setText(model.getBio()); // Set Bio

                    holder.itemView.setOnClickListener(v -> {
                        // TODO: 2/19/2021  
                    });
                }else{
                    holder.itemView.setVisibility(View.GONE); // current user item is invisible
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0,0)); // show current user item at position(0,0) of people fragment
                }
            }
        };

        firebaseRecyclerAdapter.startListening(); // Monitor changes to the Firebase query
        people_recycler.setAdapter(firebaseRecyclerAdapter); // combine the items to the recycler view in the people fragment
    }

    private void initializeView(View itemView) {
        unbinder = ButterKnife.bind(this, itemView); // Bind people layout to people fragment
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext()); // Responsible for laying out each item in the view
        people_recycler.setLayoutManager(linearLayoutManager); // Gives the desired items to be shown to the recycler
        people_recycler.addItemDecoration(new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation())); // Useful for drawing dividers between items, highlights, visual grouping boundaries and more
    }

    @Override
    // Tells the fragment that its activity has completed its own Activity.onCreate()
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        pViewModel = new ViewModelProvider(this).get(PeopleViewModel.class); // Instantiate the view model
        // TODO: Use the ViewModel
    }

    @Override
    public void onStart() {
        super.onStart();
        if (firebaseRecyclerAdapter!=null) firebaseRecyclerAdapter.startListening();
    }

    @Override
    public void onStop() {
        if (firebaseRecyclerAdapter!=null) firebaseRecyclerAdapter.stopListening();
        super.onStop();
    }
}