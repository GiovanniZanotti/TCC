package com.example.liber.view.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.liber.R;
import com.example.liber.view.fragment.LoginFragment;
import com.google.android.gms.location.FusedLocationProviderClient;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Abre o fragment de login no Fragment Container View
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.login_fragment, LoginFragment.class, null)
                .commit();
        }
    }
}
