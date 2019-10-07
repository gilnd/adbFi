package org.horaapps.adbfi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by gilbertndr on 27/02/2017.
 */

public class SettingsActivity extends ThemedActivity {
    private PreferenceUtil SP;
    private SwitchCompat swShowNotication, swShowTips;
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_settings);
        //SP
        SP = PreferenceUtil.getInstance(getApplicationContext());

        //TOOLBAR
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupViews();
        initUi();
    }

    /*** - / MENU / - ***/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { return true; }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case android.R.id.home: onBackPressed(); break;
        } return true;
    }

    public void initUi(){
        //TODO: app:cardBackgroundColor="@color/colorCardBackground"
        //setCardViewBackgroundColor(((CardView) findViewById(R.id.card_settings_general)), getCardBackgroundColor());

    }

    public void setupViews(){
        //ON VIEW CLICK
        /*** NOTIFICAITON ***/
        swShowNotication = (SwitchCompat) findViewById(R.id.sw_settings_notification);
        swShowNotication.setChecked(SP.getBoolean("pref_show_notification", true));
        swShowNotication.setClickable(false);
        findViewById(R.id.ll_settings_notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swShowNotication.setChecked(!swShowNotication.isChecked());
                SP.putBoolean("pref_show_notification", swShowNotication.isChecked());
                //setSwitchCompactColor(swShowNotication, getAccentColor());
            }
        });
        /*** SHOW TIPS***/
        swShowTips = (SwitchCompat) findViewById(R.id.sw_settings_show_tips);
        swShowTips.setChecked(SP.getBoolean("pref_show_tips", true));
        swShowTips.setClickable(false);
        findViewById(R.id.ll_settings_show_tips).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swShowTips.setChecked(!swShowTips.isChecked());
                SP.putBoolean("pref_show_tips", swShowTips.isChecked());
                //setSwitchCompactColor(swShowNotication, getAccentColor());
            }
        });
        /*** NET PORT ***/
        ((LinearLayout) findViewById(R.id.ll_settings_port)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                portDialog();
            }
        });
    }

    /*** - OTHER STUFF - ***/
    public void portDialog(){
        final AlertDialog.Builder portDialog = new AlertDialog.Builder(SettingsActivity.this, R.style.AlertDialog_Dark);
        final View portDialogLayout = getLayoutInflater().inflate(R.layout.dialog_port, null);
        final CardView Background = (CardView) portDialogLayout.findViewById(R.id.dialog_port_card);
        Background.setCardBackgroundColor(getCardBackgroundColor());

        final EditText portEditxt = (EditText) portDialogLayout.findViewById(R.id.dialog_port_edittxt);
        portEditxt.setHint(Utils.getNetPort());

        portEditxt.setHintTextColor(getPrimaryColor());
        portEditxt.setTextColor(getPrimaryColor());
        portEditxt.setHighlightColor(getPrimaryColor());
        portEditxt.getBackground().mutate().setColorFilter(getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);

        portEditxt.addTextChangedListener(new TextWatcher(){
            public void afterTextChanged(Editable s) {}
            public void beforeTextChanged(CharSequence s, int start, int count, int after){}
            public void onTextChanged(CharSequence s, int start, int before, int count){
                String strText = portEditxt.getText().toString();
                if(strText.length() <= 5 && strText.length() >= 4){
                    //IT'S OKAY
                    portEditxt.setHintTextColor(getPrimaryDarkColor());
                    portEditxt.setTextColor(getPrimaryColor());
                    portEditxt.setHighlightColor(getPrimaryColor());
                    portEditxt.getBackground().mutate().setColorFilter(getPrimaryColor(), PorterDuff.Mode.SRC_ATOP);
                } else {
                    //NOT OKAY
                    portEditxt.setHintTextColor(getAccentColor());
                    portEditxt.setTextColor(getAccentColor());
                    portEditxt.setHighlightColor(getAccentColor());
                    portEditxt.getBackground().mutate().setColorFilter(getAccentColor(), PorterDuff.Mode.SRC_ATOP);
                }
                /*
                if(!strEnteredVal.equals("")){
                    int num=Integer.parseInt(strEnteredVal);
                    if(num<60){
                        portEditxt.setText(""+num);
                    }else{
                        portEditxt.setText("");
                    }
                }*/
            }
        });

        TextView txtPort = (TextView) portDialogLayout.findViewById(R.id.dialog_port_text);
        txtPort.setText(txtPort.getText() + " " + Utils.getNetPort());

        portDialog.setView(portDialogLayout);
        portDialog.setPositiveButton(this.getString(R.string.ok).toUpperCase(), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //Toast.makeText(App.getMyInstance(), "Toast Int port value: " + Integer.parseInt(portEditxt.getText().toString()), Toast.LENGTH_SHORT).show();
                String value=portEditxt.getText().toString();
                if(value.length() <= 5 && value.length() >= 4 && Integer.parseInt(value)>=1024 && Integer.parseInt(value)<=49151) {
                    Utils.setNetPort(portEditxt.getText().toString());
                    Toast.makeText(App.getMyInstance(), "Port: " + value + ", is now the new port!", Toast.LENGTH_SHORT).show();
                } else Toast.makeText(App.getMyInstance(), "Port: " + value + ", can't be used!", Toast.LENGTH_SHORT).show();

            }
        });
        portDialog.setNegativeButton(getString(R.string.cancel).toUpperCase(), new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialog, int id){}
        });
        portDialog.show();
    }

}
