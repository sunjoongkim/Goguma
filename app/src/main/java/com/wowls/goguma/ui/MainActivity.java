package com.wowls.goguma.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.wowls.goguma.R;
import com.wowls.goguma.ui.consumer.ConsumerActivity;

public class MainActivity extends Activity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        startConsumerActivity();
    }

    private void startConsumerActivity()
    {
        Intent intent = new Intent(this, ConsumerActivity.class);
        startActivity(intent);
    }
}
