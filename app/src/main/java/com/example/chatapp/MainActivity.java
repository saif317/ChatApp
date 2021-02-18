package com.example.chatapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.chatapp.Common.Common;
import com.example.chatapp.Model.UserModel;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.Arrays;
import java.util.List;

// (AppCompatActivity) : Base class for activities that wish to use some of the newer platform features on older Android devices
public class MainActivity extends AppCompatActivity {
    // The private access modifier is accessible only within the same class
    private final static int LOGIN_REQUEST_CODE = 1; // Global constant
    private final static int PERMISSIONS_REQUEST_CODE = 2;
    private List<AuthUI.IdpConfig> providers; // (AuthUI.IdpConfig) : Holds Authentication methods
    private FirebaseAuth firebaseAuth; // Entry point of the Firebase Authentication SDK
    private FirebaseAuth.AuthStateListener listener; // Called when there is a change in the authentication state

    FirebaseDatabase database; // Entry point of the Firebase Database
    DatabaseReference userRef; // Used to read and write data from the database

    // fires when the system first creates the activity
    // perform basic application startup logic that should happen only once for the entire life of the activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {  // (Bundle savedInstanceState) : Save's the state of the application
        super.onCreate(savedInstanceState); // Recreates the saved state of the application
        setContentView(R.layout.activity_main); // Links the activity to the layout

        initialize(); // Initialize variables
    }

    // The onStart() call makes the activity visible to the user
    // Initializes the code that maintains the UI
    @Override
    protected void onStart() {
        super.onStart();
        // Show the sign in by phone interface
        firebaseAuth.addAuthStateListener(listener); // Register listener
    }


    // When your activity is no longer visible to the user
    // When a newly launched activity covers the entire screen
    // When the activity has finished running, and is about to be terminated
    @Override
    protected void onStop() {
        if (firebaseAuth != null && listener != null) // If Firebase Authentication SDK has been initialized and listener has been registered
            firebaseAuth.removeAuthStateListener(listener); // Unregister listener
        super.onStop();
    }

    private void initialize() {
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build()); // Authentication by phone number

        firebaseAuth = FirebaseAuth.getInstance(); // Initialize Firebase Authentication SDK

        database = FirebaseDatabase.getInstance(); // Initialize Firebase Database
        userRef = database.getReference(Common.USER_REFERENCES); // Access Users in the database

        requestPermissionsDexter(); // Request permissions at runtime

    }

    private void requestPermissionsDexter() {
        listener = myFirebaseAuth -> {
            Dexter.withContext(this) // Request permissions in "this" activity
                    .withPermissions(Arrays.asList( // List of permissions converted from an array to a list
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    )).withListener(new MultiplePermissionsListener() { // Listener to actions committed on permissions prompt
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) { // What to do with permissions
                    if (multiplePermissionsReport.areAllPermissionsGranted()) { // If all permissions are granted
                        // Check if the user is signed in or not
                        FirebaseUser user = myFirebaseAuth.getCurrentUser(); // Get currently signed in user
                        if (user != null) // if user is signed in
                            checkUserInDatabase(); // Check for the user in the database
                        else
                            showLoginLayout(); // send user to register phone number
                    } else
                        showAlertDialog();
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) { // Notify you when you are requesting a permission that needs an additional explanation for its usage
                    permissionToken.continuePermissionRequest();
                }
            }).check();
        };
    }

    private void requestPermissionsNoDexter() {
        // If any permissions are not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                + ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(
                this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                + ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale( // Ask for permissions again if user rejected them
                    this, Manifest.permission.CAMERA)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    this, Manifest.permission.ACCESS_FINE_LOCATION)) {

                showAlertDialog();
            } else { // Ask for permissions
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        },
                        PERMISSIONS_REQUEST_CODE
                );
            }
        } else {
            listener = myFirebaseAuth -> {
                FirebaseUser user = myFirebaseAuth.getCurrentUser(); // Get currently signed in user
                if (user != null) // if user is signed in
                    checkUserInDatabase(); // Check for the user in the database
                else
                    showLoginLayout(); // if user is not signed in send user to login page
            };
            // Show the sign in by phone interface
            firebaseAuth.addAuthStateListener(listener); // Register listener
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            // When request is cancelled, the results array are empty
            if ((grantResults.length > 0) && (grantResults[0] + grantResults[1] + grantResults[2] + grantResults[3] == PackageManager.PERMISSION_GRANTED)
            ) { // Permissions are granted
                listener = myFirebaseAuth -> {
                    FirebaseUser user = myFirebaseAuth.getCurrentUser(); // Get currently signed in user
                    if (user != null) // if user is signed in
                        checkUserInDatabase(); // Check for the user in the database
                    else
                        showLoginLayout(); // if user is not signed in send user to login page
                };
                // Show the sign in by phone interface
                firebaseAuth.addAuthStateListener(listener); // Register listener
            } else {
                showAlertDialog();
            }
            return;
        }
    }


    // Change later into custom FrameLayout
    private void showAlertDialog() {
        // Change later into custom FrameLayout
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this); // Create a alert dialog
        builder.setMessage("Camera, Read Write External and Access Location" +
                " Storage permissions are required to run the application.");
        builder.setTitle("Please grant permissions");
        builder.setPositiveButton("OK", (dialogInterface, i) -> { // On Accept
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS); // An intent towards the settings page of an app
            Uri uri = Uri.fromParts("package", getPackageName(), null); // Set URI as name of this package
            intent.setData(uri); // Set intent to direct user to settings page of this app
            startActivity(intent); // Open the settings page
        });
        builder.setNeutralButton("Cancel", null); // On cancel
        AlertDialog dialog = builder.show(); // Show alert dialog
    }


    private void showLoginLayout() {
        // Launch login intent while expecting a result from it
        // Sign in method is by phone
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder() // Creates sign in by phone interface
                .setIsSmartLockEnabled(false) // Disable smart lock
                .setTheme(R.style.LoginTheme)
                .setAvailableProviders(providers).build(), LOGIN_REQUEST_CODE); // Set available authentication methods available to the activity
    }

    private void checkUserInDatabase() {
        // Get signed in user's UID
        userRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() { // (addListenerForSingleValueEvent) : executes onDataChange method immediately and after executing that method once, it stops listening to the reference location it is attached to.
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { // This method will be called with a snapshot of the data at this location
                if (snapshot.exists()) { // If signed in user has a UID
                    UserModel userModel = snapshot.getValue(UserModel.class); // Initialize a new user
                    userModel.setUid(snapshot.getKey()); // Set user UID
                    goToHomeActivity(userModel); // Send user and his info to Home page
                } else
                    showRegisterLayout(); // Send user to register activity
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { // This method will be triggered in the event that this listener either failed at the server, or is removed as a result of the security and Firebase Database rules.

            }
        });
    }

    private void goToHomeActivity(UserModel userModel) {
        Common.currentUser = userModel; // Set user info
        startActivity(new Intent(this, HomeActivity.class)); // Start home activity
        finish(); // Destroy "this" activity
    }

    private void showRegisterLayout() {
        startActivity(new Intent(this, RegisterActivity.class)); // Starts Register Activity
        finish(); // Destroy "this" activity
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { // Result of the startActivityForResult method
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE) { // if the Login Activity has been started
            IdpResponse idpResponse = IdpResponse.fromResultIntent(data); // The data sent from the startActivityForResult
            if (resultCode == RESULT_OK) { // if login activity has been started successfully
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // The signed in user's information
            } else
                Toast.makeText(this, "[ERROR]" + idpResponse.getError(), Toast.LENGTH_SHORT).show();
        }
    }
}

