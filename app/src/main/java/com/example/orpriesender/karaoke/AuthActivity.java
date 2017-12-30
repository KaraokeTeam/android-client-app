package com.example.orpriesender.karaoke;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Or Priesender on 23-Dec-17.
 */


public class AuthActivity extends Activity {
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build());

// Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                intent.putExtra("user",user.getUid());
                startActivity(intent);
            } else {
                Context context = getApplicationContext();
                if (response != null) {
                    if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                        Toast t = Toast.makeText(context, "Network Error", Toast.LENGTH_LONG);
                        t.show();
                    } else if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        Toast t = Toast.makeText(context, "Unknown Error", Toast.LENGTH_LONG);
                        t.show();
                    }
                }


            }
        }
    }


}
