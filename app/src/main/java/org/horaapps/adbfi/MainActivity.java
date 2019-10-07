package org.horaapps.adbfi;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.Locale;

public class MainActivity extends ThemedActivity implements View.OnClickListener{

    private boolean activeAdb;
    private FloatingActionButton fabToggle;
    private BroadcastReceiver wifiReceiver;
    Toolbar toolbar;
    int tipsCount = 1;

    private PreferenceUtil SP;

    /*** VIEWS ***/
    TextView txtNetworkName, txtNetworkIP, txtADBStatus;
    IconicsImageView networkIcon;

    /*** NOTIFICATION ***/
    NotificationCompat.Builder notiAdbRunning;
    NotificationManager notificationManager;
    public static final String TOGGLE_ACTION = "org.horaapps.adbfi.TURNOFF_ADBFI";
    public static final String STATE  = "state";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SP = PreferenceUtil.getInstance(getApplicationContext());

        if (getIntent().getAction().equals(TOGGLE_ACTION)) {
            Toast.makeText(this, "toggle notification adb: " +getIntent().getBooleanExtra(STATE, false), Toast.LENGTH_SHORT).show();
            Utils.toggleAdb(false);
            activeAdb = false;
            /*
            if(Utils.isPhoneRooted()){
                Utils.toggleAdb(false);
            }
            notificationManager.cancel(0);
            */
        }

        Log.wtf("asd", Utils.isHotspotEnabled(this)+"");



        if (!Utils.isWifiConnected(this))
            Toast.makeText(getApplicationContext(), getString(R.string.turn_on_something), Toast.LENGTH_SHORT).show();

        fabToggle = (FloatingActionButton) findViewById(R.id.fabButton);
        fabToggle.setOnClickListener(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction(android.net.ConnectivityManager.CONNECTIVITY_ACTION);
        wifiReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                wifiStateChanged();
            }
        };
        registerReceiver(wifiReceiver, filter);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(getTextColor());

        initUI();

        /*** NOTIFICATION ***/

        notiAdbRunning = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_adb_notificantion)
                .setContentTitle(getString(R.string.notification_text))
                .setContentText(getString(R.string.listening_at)+": " + Utils.getIPAddress(true) + ":"+Utils.getNetPort())
                .addAction(0, getString(R.string.turn_off), getToggleIntent(false))
                .setPriority(Notification.PRIORITY_MAX)
                .setAutoCancel(false);

        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        /*** - / TIPS / - ***/
        findViewById(R.id.tips_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(tipsCount ==1){
                    tipsCount++;
                    ((TextView) findViewById(R.id.txtTips)).setText(String.format(Locale.ENGLISH, getString(R.string.tips)+getString(R.string.tips_windows)));
                } else {
                    tipsCount--;
                    ((TextView) findViewById(R.id.txtTips)).setText(String.format(Locale.ENGLISH, getString(R.string.tips)+getString(R.string.tips_tile)));
                }
                ((TextView) findViewById(R.id.txtTipsCount)).setText(tipsCount+"/2");
            }
        });

    }

    public PendingIntent getToggleIntent(boolean state) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction(TOGGLE_ACTION);
        intent.putExtra(STATE, state);
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
    }

    /*** - / MENU / - ***/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.settings: startActivity(new Intent(MainActivity.this, SettingsActivity.class)); break;
            case R.id.about: startActivity(new Intent(MainActivity.this, AboutActivity.class)); break;
        } return true;
    }

    /*** - / ACTIVITY / - ***/
    @Override
    protected void onDestroy() {
        if (wifiReceiver != null) {
            unregisterReceiver(wifiReceiver);
            wifiReceiver = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        activeAdb=Utils.isAdbOverWifiEnabled();
        if(activeAdb)
            slideCardAnim(true);
        wifiStateChanged();
        setupViews();
    }

    /*** - / ACTIONS / - ***/
    @Override
    public void onClick(View v) {
        if (!Utils.isWifiConnected(getApplicationContext()) && !Utils.isHotspotEnabled(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.turn_on_something), Toast.LENGTH_SHORT).show();
        }

        activeAdb = !activeAdb;
        if(Utils.isPhoneRooted()){
            Utils.toggleAdb(activeAdb);
        }
        setupViews();
        slideCardAnim(activeAdb);

    }

    public void wifiStateChanged(){ setupViews(); }

    /*** - THEMING - ***/
    public void initUI(){
        //TODO: app:cardBackgroundColor="@color/colorCardBackground"
        //setCardViewBackgroundColor(((CardView) findViewById(R.id.card_main_start)), getPrimaryColor());
        //setCardViewBackgroundColor(((CardView) findViewById(R.id.tips_card)), getCardBackgroundColor());
        //setCardViewBackgroundColor(((CardView) findViewById(R.id.main_detail_card)), getCardBackgroundColor());
        txtNetworkName = (TextView) findViewById(R.id.txt_network_name);
        txtADBStatus = (TextView) findViewById(R.id.txt_network_status);
        txtNetworkIP = (TextView) findViewById(R.id.txt_network_ip);
        networkIcon = (IconicsImageView) findViewById(R.id.icon_wifi_status);
        //TIPS
        ((TextView) findViewById(R.id.txtTips)).setText(getString(R.string.tips)+getString(R.string.tips_tile));
    }
    /*** - SET VIEWS CONTENT - ***/
    private void setupViews(){
        findViewById(R.id.tips_card).setVisibility(SP.getBoolean("pref_show_tips", true) ? View.VISIBLE : View.GONE);
        if(Utils.isWifiConnected(getApplicationContext()) || Utils.isHotspotEnabled(getApplicationContext())) {
            networkIcon.setIcon("gmd-network-wifi");
            txtNetworkName.setText(Utils.getWifiName(getApplicationContext()));
            txtADBStatus.setText(activeAdb ? R.string.active : R.string.disabled);
            txtNetworkIP.setText(Utils.getIPAddress(true));
            fabToggle.setBackgroundTintList(ColorStateList.valueOf(getColor(activeAdb ? R.color.colorDisabled : R.color.colorActive)));
            fabToggle.setImageDrawable(new IconicsDrawable(this).icon(activeAdb ? GoogleMaterial.Icon.gmd_not_interested : GoogleMaterial.Icon.gmd_adb).color(Color.WHITE));

            if (activeAdb){
                /*** NOTIFICATION ***/
                if (SP.getBoolean("pref_show_notification",true))
                    notificationManager.notify(0, notiAdbRunning.build());

                ((TextView) findViewById(R.id.txt_ip_port)).setText(
                        Utils.html(String.format("%s: <b><font color='%s'>%s:%s</font></b>",
                                getString(R.string.listening_at),
                                Utils.getHexColor(getDisabledColor()),
                                Utils.getIPAddress(true), Utils.getNetPort())));
                ((TextView) findViewById(R.id.txt_terminal_connection)).setText(Utils.html(String.format("%s <b><font color='%s'>adb connect %s:%s</font></b>",
                        getString(R.string.how_connect_with_terminal),
                        Utils.getHexColor(getDisabledColor()),
                        Utils.getIPAddress(true), Utils.getNetPort())));
            } else{
                notificationManager.cancel(0);
            }
        } else {
            networkIcon.setIcon("gmd-signal-wifi-off");
            txtNetworkName.setText(Utils.getWifiName(getApplicationContext()));
            txtNetworkIP.setText(getString(R.string.no_connection));
            txtADBStatus.setText(getString(R.string.no_connection));
            fabToggle.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.colorCardBackground)));
            fabToggle.setImageDrawable(new IconicsDrawable(this).icon(GoogleMaterial.Icon.gmd_signal_wifi_off).color(getIconColor()));
            if (activeAdb)
                slideCardAnim(false);
            activeAdb=false;
        }
    }

    /*** - CARD ANIMATOR - ***/
    public void slideCardAnim(final boolean show){
        if(!Utils.isPhoneRooted()){//!
            ((LinearLayout) findViewById(R.id.ll_how_to_connect_root)).setVisibility(View.GONE);
            ((TextView) findViewById(R.id.txt_no_root_how_to_connect)).setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.txt_no_root_how_to_connect))
                    .setText(getString(R.string.no_root_how_to_connect,
                            Html.fromHtml("<b><font color='"+Utils.getHexColor(getAccentColor())+"'>"+Utils.getNetPort()+"</font></b>", Html.FROM_HTML_MODE_COMPACT),
                            Html.fromHtml("<b><font color='"+Utils.getHexColor(getAccentColor())+"'>"+Utils.getIPAddress(true)+"</font></b>", Html.FROM_HTML_MODE_COMPACT)));
        }
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),
                show ? R.anim.slide_up : R.anim.slide_down);
        findViewById(R.id.main_detail_card).startAnimation(anim);
        anim.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationStart(Animation arg0) {
                findViewById(R.id.main_detail_card).setVisibility(View.VISIBLE);
            }
            @Override
            public void onAnimationRepeat(Animation arg0) {
            }
            @Override
            public void onAnimationEnd(Animation arg0) {
                findViewById(R.id.main_detail_card).setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}