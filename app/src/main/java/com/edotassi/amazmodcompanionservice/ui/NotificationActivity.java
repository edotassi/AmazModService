package com.edotassi.amazmodcompanionservice.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.edotassi.amazmodcompanionservice.R;
import com.edotassi.amazmodcompanionservice.R2;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NotificationActivity extends Activity {

    @BindView(R2.id.notification_title)
    TextView title;
    @BindView(R2.id.notification_text)
    TextView text;
    @BindView(R2.id.notification_icon)
    ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);


        ButterKnife.bind(this);

        Intent intent = getIntent();

        if (intent != null) {
            extractDataFromIntent(intent);
        }
    }

    private void extractDataFromIntent(Intent intent) {
        String titleExtra = intent.getStringExtra("title");
        String textExtra = intent.getStringExtra("text");

        title.setText(titleExtra);
        text.setText(textExtra);

        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bitmapExtra = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        icon.setImageBitmap(Bitmap.createScaledBitmap(bitmapExtra, 48, 48, false));
    }

}
