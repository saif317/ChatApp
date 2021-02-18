package com.example.chatapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.chatapp.Common.Common;
import com.example.chatapp.Model.UserModel;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.example.chatapp.R.id;
import static com.example.chatapp.R.layout;

// (AppCompatActivity) : Base class for activities that wish to use some of the newer platform features on older Android devices
public class RegisterActivity extends AppCompatActivity {

    // Replace with View Binding later
    // Set which views bind to which variables
    @BindView(id.edt_first_name)
    TextInputEditText edt_first_name;
    @BindView(id.edt_last_name)
    TextInputEditText edt_last_name;
    @BindView(id.edt_phone_number)
    TextInputEditText edt_phone_number;
    @BindView(id.edt_dob)
    TextInputEditText edt_dob;
    @BindView(id.edt_bio)
    TextInputEditText edt_bio;
    @BindView(id.btn_register)
    Button btn_register;

    FirebaseDatabase database; // Entry point of the Firebase Database
    DatabaseReference userRef; // Used to read and write data from the database

    MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker().build(); // Initialize date picker
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy"); // Used to select date format and convert text into dates
    Calendar calendar = Calendar.getInstance(); // Get system date and time
    boolean birthDateSelected = false; // if birth date has been set

    // fires when the system first creates the activity
    // perform basic application startup logic that should happen only once for the entire life of the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) { // (Bundle savedInstanceState) : Save's the state of the application
        super.onCreate(savedInstanceState); // Recreates the saved state of the application
        setContentView(layout.activity_register); // Links the activity to the layout

        initialize(); // Initialize variables
        setDefaultData(); // Set the what the user info defaults to
    }

    private void setDefaultData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // Get currently signed in user
        edt_phone_number.setText(user.getPhoneNumber()); // Set user's phone number to verified phone number
        edt_phone_number.setEnabled(false); // Disable editing of phone number

        edt_dob.setOnFocusChangeListener((v, hasFocus) -> { // When DOB is selected
            if (hasFocus)
                materialDatePicker.show(getSupportFragmentManager(), materialDatePicker.toString()); // Show date picker
        });

        btn_register.setOnClickListener(v -> { // When register button is clicked
            if (!birthDateSelected) { // If birth date is not selected
                Toast.makeText(this, "Please enter your birth date", Toast.LENGTH_LONG).show();
                return;
            }

            UserModel userModel = new UserModel(); // Initialize a new User

            // Set registering user info to the User model
            userModel.setFirstName(edt_first_name.getText().toString());
            userModel.setLastName(edt_last_name.getText().toString());
            userModel.setBio(edt_bio.getText().toString());
            userModel.setPhone(edt_phone_number.getText().toString());
            userModel.setBirthDate(calendar.getTimeInMillis());
            userModel.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

            userRef.child(userModel.getUid()) // At location of the registering user
                    .setValue(userModel) // Insert User Model into the database
                    .addOnFailureListener(e -> { // If insertion fails
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show(); // Show error message
                    }).addOnSuccessListener(aVoid -> { // If insertion Succeeds
                Toast.makeText(this, "Registration Succeeded", Toast.LENGTH_SHORT).show();
                Common.currentUser=userModel; // Set user info
                startActivity(new Intent(this,HomeActivity.class)); // Start home activity
                finish(); // Destroy "this" activity
            });
        });
    }

    private void initialize() {
        ButterKnife.bind(this); // Bind views to variables
        database = FirebaseDatabase.getInstance(); // Initialize Firebase Database
        userRef = database.getReference(Common.USER_REFERENCES); // Access Users in the database
        // Listener will wait for the user to select date
        materialDatePicker.addOnPositiveButtonClickListener(selection -> { // Called when the user confirms a valid selection
            calendar.setTimeInMillis(selection); // Set time and date to whatever the user selected
            edt_dob.setText(simpleDateFormat.format(selection)); // Format the selected date into the DOB text view
            birthDateSelected = true; // Set DOB as filled
        });
    }
}