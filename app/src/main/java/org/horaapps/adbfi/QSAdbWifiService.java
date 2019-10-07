package org.horaapps.adbfi;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

@SuppressLint("Override")
@TargetApi(Build.VERSION_CODES.N)
public class QSAdbWifiService extends TileService {

    @Override
    public void onStartListening() {
        updateTile(Utils.isAdbOverWifiEnabled());
    }

    @Override
    public void onClick(){
        boolean enabled = getQsTile().getState() == Tile.STATE_ACTIVE;
        if (!Utils.isWifiConnected(getApplicationContext()) && !Utils.isHotspotEnabled(getApplicationContext()))
            Toast.makeText(getApplicationContext(), getString(R.string.turn_on_something), Toast.LENGTH_SHORT).show();
        enabled = !enabled;
        Utils.toggleAdb(enabled);
        if (enabled)
            Toast.makeText(this, String.format("Listening at %s:5555", Utils.getIPAddress(true)), Toast.LENGTH_LONG).show();
        updateTile(enabled);
    }

    public void updateTile(boolean enabled) {
        Tile tile = super.getQsTile();
        tile.setState(enabled ? Tile.STATE_ACTIVE : Tile.STATE_INACTIVE);
        tile.updateTile();
    }
}
