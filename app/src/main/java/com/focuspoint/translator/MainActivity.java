package com.focuspoint.translator;

import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import javax.inject.Inject;

import retrofit2.Retrofit;


public class MainActivity extends AppCompatActivity {

    @Inject
    Retrofit retrofit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        App.from(this).getComponent().inject(this);
    }
}
