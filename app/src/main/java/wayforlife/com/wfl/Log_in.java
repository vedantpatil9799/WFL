package wayforlife.com.wfl;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Log_in extends AppCompatActivity {

    private EditText phno,otp;
    private Button send_otp,verify;
    private String final_phno,pno,code,mVerificationId;
    private PopupWindow otp_authentication;
    private FirebaseAuth mAuth;
    private RelativeLayout relativeLayout;
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mAuth=FirebaseAuth.getInstance();
        phno=findViewById(R.id.edit_phno);
        send_otp=findViewById(R.id.btn_send_otp);
        relativeLayout=findViewById(R.id.relative_log_in);
        pd=new ProgressDialog(this);
        pd.setMessage("Logging you in.....");
        pd.setTitle("Please Wait.....");
        send_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pno=phno.getText().toString().trim();
                if(pno.isEmpty() || pno.length()>10 || pno.length()<10)
                {
                    Toast.makeText(Log_in.this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
                    phno.setError("Invalid Mobile Number");
                }
                else
                {
                    final_phno = "+91".concat(phno.getText().toString().trim());
                    Toast.makeText(Log_in.this, "Mobile Number.." + final_phno, Toast.LENGTH_SHORT).show();
                    verifyPhoneNo();
                    LayoutInflater layoutInflater=(LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                    View pop=layoutInflater.inflate(R.layout.check_otp,null);
                    otp=pop.findViewById(R.id.edit_otp);
                    verify=pop.findViewById(R.id.btn_done);

                    otp_authentication=new PopupWindow(pop, RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT,false);
                    otp_authentication.showAtLocation(relativeLayout, Gravity.CENTER,0,0);
                }
            }
        });
    }
    private void verifyPhoneNo()
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                final_phno,
                60,
                TimeUnit.SECONDS,
                Log_in.this,
                mCallbacks);
    }
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential)
        {
            code=phoneAuthCredential.getSmsCode();
            if(code.isEmpty())
            {
                Toast.makeText(Log_in.this, "Automatic code read fail", Toast.LENGTH_SHORT).show();
                code=otp.getText().toString().trim();
                if(code.isEmpty())
                {
                    Toast.makeText(Log_in.this, "Please enter the OTP", Toast.LENGTH_SHORT).show();
                    otp.setError("Please enter the OTP");
                }
            }
            else
            {
                otp.setText(code);
            }
            verify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    verifyVerificationcode(code);
                    Toast.makeText(Log_in.this, "Verification Successful", Toast.LENGTH_SHORT).show();
                    otp_authentication.dismiss();
                    pd.show();
                }
            });
        }

        @Override
        public void onVerificationFailed(FirebaseException e)
        {
            Toast.makeText(Log_in.this, "Verification Unsuccessful"+e, Toast.LENGTH_SHORT).show();
            Log.d("errorrrrrrrr",""+e);
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken)
        {
            super.onCodeSent(s, forceResendingToken);
            Toast.makeText(Log_in.this, "OTP Send", Toast.LENGTH_SHORT).show();
            mVerificationId = s;
        }
    };

    private void verifyVerificationcode(String code)
    {
        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(mVerificationId,code);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            boolean isNew = task.getResult().getAdditionalUserInfo().isNewUser();
                            Toast.makeText(Log_in.this, ""+isNew, Toast.LENGTH_SHORT).show();
                            if(isNew)
                            {
                                pd.dismiss();
                                Toast.makeText(Log_in.this, "New user", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(Log_in.this,Registration.class);
                                intent.putExtra("phno",final_phno);
                                startActivity(intent);
                                finish();
                            }
                            else if(!isNew){
                                pd.dismiss();
                                Toast.makeText(Log_in.this, "Log In Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(Log_in.this, MainActivity.class));
                            }
                        }
                        else
                        {
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                            {
                                Toast.makeText(Log_in.this, "Error during authentication", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
