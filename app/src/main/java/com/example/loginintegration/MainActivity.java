package com.example.loginintegration;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.linkedin.platform.APIHelper;
import com.linkedin.platform.LISessionManager;
import com.linkedin.platform.errors.LIApiError;
import com.linkedin.platform.errors.LIAuthError;
import com.linkedin.platform.listeners.ApiListener;
import com.linkedin.platform.listeners.ApiResponse;
import com.linkedin.platform.listeners.AuthListener;
import com.linkedin.platform.utils.Scope;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_STRING="com.example.loginintegration.EXTRA_STRING";
    public static final String EMAIL_IN="emailAddress";
    public static final String NAME="Name";
    LoginButton fblogin;
    ImageButton inlogin;
    FirebaseAuth auth;
    FirebaseUser user;
    CallbackManager callbackManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();
        //generateHashkey();
        if(user == null) {
            setContentView(R.layout.activity_main);
            FacebookSdk.sdkInitialize(MainActivity.this);
            callbackManager = CallbackManager.Factory.create();
            fblogin = (LoginButton) findViewById(R.id.fblogin);
            fblogin.setReadPermissions(Arrays.asList("email"));
            inlogin=(ImageButton)findViewById(R.id.inlogin);
            inlogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    handleLinkedinLogin(view);
                }
            });
        }
        else
        {
            Intent intent=new Intent(MainActivity.this,SecondActivity.class);
            intent.putExtra(EXTRA_STRING,user.getEmail());
            Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
    }

    public void fbLoginClick(View v)
    {
        fblogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AuthCredential credential= FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            user = auth.getCurrentUser();
                            Intent intent=new Intent(MainActivity.this,SecondActivity.class);
                            intent.putExtra(EXTRA_STRING,user.getEmail());
                            Toast.makeText(MainActivity.this,"Loggedin successfully",Toast.LENGTH_SHORT).show();
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

            @Override
            public void onCancel() {
                Toast.makeText(MainActivity.this,"User Cancelled ",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(MainActivity.this,"Some error Has occured ",Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void handleLinkedinLogin(View v)
    {
        LISessionManager.getInstance(getApplicationContext()).init(this, buildScope(), new AuthListener() {
            @Override
            public void onAuthSuccess() {
                // Authentication was successful.  You can now do
                // other calls with the SDK.
                Toast.makeText(MainActivity.this,"Authrization Success",Toast.LENGTH_SHORT).show();
                fetchDetailsLinkedin();
            }

            @Override
            public void onAuthError(LIAuthError error) {
                Log.e("Auth Error",error.toString());
                // Handle authentication errors
                Toast.makeText(MainActivity.this,"Authrization Failed",Toast.LENGTH_SHORT).show();
            }
        }, true);
    }

    private static Scope buildScope() {
        return Scope.build(Scope.R_BASICPROFILE, Scope.W_SHARE,Scope.R_EMAILADDRESS);
    }


    public void fetchDetailsLinkedin()
    {
        String url = "https://api.linkedin.com/v1/people/~:(id,first-name,last-name,headline,public-profile-url,picture-url,email-address,picture-urls::(original))";

        APIHelper apiHelper = APIHelper.getInstance(getApplicationContext());
        apiHelper.getRequest(MainActivity.this, url, new ApiListener() {
            @Override
            public void onApiSuccess(ApiResponse apiResponse) {
                // Success!
                Toast.makeText(MainActivity.this,"api response get Successfully",Toast.LENGTH_SHORT).show();
                JSONObject jsonObject=apiResponse.getResponseDataAsJson();
                try {
                    String fname = jsonObject.getString("firstName");
                    String lname=jsonObject.getString("lastName");
                    String emailAddress=jsonObject.getString("emailAddress");
                    Toast.makeText(MainActivity.this,"Loggedin Successfully",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this,LinkedinLogged.class);
                    intent.putExtra(NAME,fname+lname);
                    intent.putExtra(EMAIL_IN,emailAddress);
                    startActivity(intent);
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }
            }

            @Override
            public void onApiError(LIApiError liApiError) {
                // Error making GET request!
                Log.e("Api Error ",liApiError.getMessage());
                Toast.makeText(MainActivity.this," ",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        LISessionManager.getInstance(getApplicationContext()).onActivityResult(this, requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
