package acadamy.enlear.chatme;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.core.Amplify;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AuthUser currentUser = Amplify.Auth.getCurrentUser();

        Intent intent;
        if(currentUser == null){
            // Go to the login screen
            intent = new Intent(getApplicationContext(), LoginActivity.class);
        }else {
            // Go to the Chat screen
            intent = new Intent(getApplicationContext(), ChatActivity.class);
        }

        // Start activity
        startActivity(intent);
        finish();
    }
}
