package org.horaapps.adbfi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
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

public class AboutActivity extends ThemedActivity {
    Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_about);

        //TOOLBAR
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        // LINKS
        ((TextView) findViewById(R.id.about_author_gilbert_github_item)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {launchUrl("https://github.com/gilbertndr");}
        });
        ((TextView) findViewById(R.id.about_author_gilbert_mail_item)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {mail("gilbert.ndresaj@gmail.com");}
        });
        //DONALD
        ((TextView) findViewById(R.id.about_special_thanks_donald)).setMovementMethod(LinkMovementMethod.getInstance());
        ((TextView) findViewById(R.id.about_special_thanks_donald)).setLinkTextColor(getActiveColor());
    }

    public void mail(String mail) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"+mail));
        try { startActivity(intent);
        } catch (Exception e){ Toast.makeText(AboutActivity.this, getString(R.string.send_mail_error), Toast.LENGTH_SHORT).show(); }
    }
    public void launchUrl(String URL){
        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(URL));
            startActivity(i);
        } catch (Exception e) { Toast.makeText(getApplicationContext(), e.toString(),Toast.LENGTH_LONG).show(); }
    }

}
