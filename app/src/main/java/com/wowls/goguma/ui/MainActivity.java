package com.wowls.goguma.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.wowls.goguma.R;
import com.wowls.goguma.ui.consumer.ConsumerActivity;
import com.wowls.goguma.ui.producer.ProducerActivity;

public class MainActivity extends Activity
{
    private Button mBtnCon, mBtnProd;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mBtnCon = (Button) findViewById(R.id.btn_consumer);
        mBtnCon.setOnClickListener(mOnClickListener);
        mBtnProd = (Button) findViewById(R.id.btn_producer);
        mBtnProd.setOnClickListener(mOnClickListener);
    }

    private void startConsumerActivity()
    {
        Intent intent = new Intent(this, ConsumerActivity.class);
        startActivity(intent);
    }

    private void startProducerActivity()
    {
        Intent intent = new Intent(this, ProducerActivity.class);
        startActivity(intent);
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            switch (v.getId())
            {
                case R.id.btn_consumer:
                    startConsumerActivity();
                    break;

                case R.id.btn_producer:
                    startProducerActivity();
                    break;
            }
        }
    };
}
