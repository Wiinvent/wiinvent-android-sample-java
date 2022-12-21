package tv.wiinvent.android.wiinvent_android_sample_java.feature;

import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import tv.wiinvent.android.wiinvent_android_sample_java.R;
import tv.wiinvent.wiinventsdk.WiGameManager;
import tv.wiinvent.wiinventsdk.interfaces.games.GameEventListener;
import tv.wiinvent.wiinventsdk.models.games.GameData;

public class GameActivity extends AppCompatActivity {

  public static final String TAG = GameActivity.class.getCanonicalName();

  public static final String SAMPLE_ACCOUNT_ID = "30";
  public static final String SAMPLE_TOKEN = "MmMyanROTExYUGZOUkRmL3J5U1BVQT09fHxBRHx8aHR0cDovL3MxaW1nLm15Y2xpcC52bi9pbWFnZTEvMjAyMC8wMy8wMy8xNTgzMjA2MjkyNTY3L2M1Mjg1NzJjYzc0Zl8xODBfMTgwLmpwZ3x8MTYyMTU2MzY5MQ%3D%3D%26%26%26ZBHLuWGTS6lvsZpzppkRwf5GTm1NHPpg0xz%2FIzIcsLSLPli7K7RMLFB9hjJD03EzQ0P%2FMJxeaqOoWOjHXdOBobApWiexQ0TncwUwTUPeQIRc5sxwLlLzkOlmuiDT5pBSzIjazEyAz27qpYrikpUd9d%2BWTHnV1byoloPTV5ogXWJGKplnswPDRaIIWusIEppZInRmWPT8pWRFxikB6boQNTQWkCbJtFg%2FpxmYXEoHE4AexBonJH7Ma9%2Fqp4VXXrwcvE3h85QbEeHdbiLtpwn%2BOL%2FPyjICWn1PblZtS5Az2TAY9Z7OnSc%2BAKZzxnzomxfZenixdnmpkobWDals28imNw%3D%3D";

  private WiGameManager gameManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.requestWindowFeature(Window.FEATURE_NO_TITLE);
    Objects.requireNonNull(getSupportActionBar()).hide();
    setContentView(R.layout.activity_game);

    init(savedInstanceState);
  }

  private void init(Bundle savedInstanceState) {
    if (savedInstanceState == null) {
      initGames();
    }
  }

  private void initGames() {
    GameData gameData = new GameData.Builder()
        .accountId(SAMPLE_ACCOUNT_ID)
        .channelId("3")
        .streamId("2")
        .token(SAMPLE_TOKEN)
        .env(GameData.Environment.SANDBOX)
        .deviceType(GameData.DeviceType.PHONE)
        .build();

    gameManager = new WiGameManager(this, R.id.game_view, gameData);
    gameManager.addGameListener(new GameEventListener() {
      @Override
      public void onDismiss() {

      }

      @Override
      public void onTimeout() {

      }

      @Override
      public void onLoadError() {

      }
    });
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    if (gameManager != null) {
      gameManager.release();
      gameManager = null;
    }
  }
}
