package wayforlife.com.wfl;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

public class SplashScreen extends AppCompatActivity {

    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth mAuth;

    @Override
    public void onStart()
    {
        super.onStart();
        mAuth.addAuthStateListener(authStateListener);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        mAuth=FirebaseAuth.getInstance();
                authStateListener=new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if(firebaseAuth.getCurrentUser()!=null)
                        {
                            startActivity(new Intent(SplashScreen.this,MainActivity.class));
                            finish();
                        }
                        else {
                            startActivity(new Intent(SplashScreen.this, WelcomeActivity.class));
                            finish();
                        }
                    }
                };
    }
}
