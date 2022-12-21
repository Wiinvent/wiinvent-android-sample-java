package tv.wiinvent.android.wiinvent_android_sample_java;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import tv.wiinvent.android.wiinvent_android_sample_java.feature.InStreamActivity;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.GameActivity;
import tv.wiinvent.android.wiinvent_android_sample_java.feature.WelcomeAdActivity;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.instreamBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, InStreamActivity.class);
        startActivity(intent);
      }
    });

    findViewById(R.id.votingBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, GameActivity.class);
        startActivity(intent);
      }
    });

    findViewById(R.id.welcomeBtn).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent intent = new Intent(MainActivity.this, WelcomeAdActivity.class);
        startActivity(intent);
      }
    });
  }
}
