package com.delaroystudios.efarmers;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.delaroystudios.efarmers.utils.PreferenceUtils;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.username) TextInputLayout username;
    @BindView(R.id.txt_username) TextInputEditText txt_username;
    @BindView(R.id.password) TextInputLayout password;
    @BindView(R.id.txt_password) TextInputEditText txt_password;
    @BindView(R.id.sign_in) MaterialButton sign_in;
    @BindView(R.id.sign_up) TextView sign_up;
    @BindView(R.id.progress) ProgressBar progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);
        String mUsername =  PreferenceUtils.getUsername(this);
        if (!mUsername.isEmpty()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        sign_in.setOnClickListener(this);
        sign_up.setOnClickListener(this);
    }

    public void verifyData() {
        username.setError(null);
        password.setError(null);

        if (txt_username.length() == 0) {

            username.setError(getString(R.string.error_username));

        } else if (txt_password.length() == 0) {

            password.setError(getString(R.string.error_password));

        } else {
            String username = txt_username.getText().toString().trim();
            String password = txt_password.getText().toString().trim();

            login(username, password);
        }
    }

    private void login (String username, String password) {
        progress.setVisibility(View.VISIBLE);
        if (username.equals("test@theagromall.com") && password.equals("password")) {
            Toast.makeText(this, "User successfully logged in", Toast.LENGTH_SHORT).show();
            PreferenceUtils.saveUsername(username, this);
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            progress.setVisibility(View.GONE);
            finish();
        } else if (!username.equals("test@theagromall.com")) {
            Toast.makeText(this, "Username is incorrect", Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
        } else {
            Toast.makeText(this, "Password is incorrect", Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in:
                verifyData();
                break;
            case R.id.sign_up:
                Toast.makeText(this, "Opened Sign up page", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
