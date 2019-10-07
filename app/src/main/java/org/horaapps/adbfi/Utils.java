package org.horaapps.adbfi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by gilbertndr on 2/5/17.
 */

public class Utils {
    //public static PreferenceUtil SP;
   // public static String NET_PORT = SP.getString("pref_net_port", "5555");
    public static final String USB_PORT = "-1";

    public static void update(){
        //SP = PreferenceUtil.getInstance(App.getMyInstance());
        //NET_PORT = SP.getString("pref_net_port", "5555");
    }

    public static String getNetPort(){return PreferenceUtil.getInstance(App.getMyInstance()).getString("pref_net_port", "5555");}
    public static void setNetPort(String port){PreferenceUtil.getInstance(App.getMyInstance()).putString("pref_net_port", port);}

    /*** - ROOT - ***/
    public static boolean isPhoneRooted() {
        // get from build info
        String buildTags = android.os.Build.TAGS;
        if (buildTags != null && buildTags.contains("test-keys")) {
            return true;
        }
        // check if /system/app/Superuser.apk is present
        try {
            File file = new File("/system/app/Superuser.apk");
            if (file.exists()) {
                return true;
            }
        } catch (Exception e1) {
            // ignore
        }

        // try executing commands
        return canExecuteCommand("/system/xbin/which su")
                || canExecuteCommand("/system/bin/which su") || canExecuteCommand("which su");
    }

    // executes a command on the system
    private static boolean canExecuteCommand(String command) {
        boolean executedSuccesfully;
        try {
            Runtime.getRuntime().exec(command);
            executedSuccesfully = true;
        } catch (Exception e) {
            executedSuccesfully = false;
        }
        return executedSuccesfully;
    }
    /*** - END / ROOT - ***/

    /*** - HOTSPOT - ***/
    public static boolean isHotspotEnabled(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

    static String getHotspotPassword(Context context){
        try {
            WifiManager wifiMgr;
            wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            Method getConfigMethod = wifiMgr.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiMgr);
            return wifiConfig.preSharedKey;
        } catch (Exception e){return e.getMessage();}
    }

    static String getGateway(Context context){
        try {
            WifiManager wifiMgr;
            wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            Method getConfigMethod = wifiMgr.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiMgr);
            return String.valueOf(wifiMgr.getDhcpInfo().serverAddress);

            /*
            "HiddenSSID: " + wifiConfig.hiddenSSID + "\nStatus: " + wifiConfig.status + "\nwepKeys: " + wifiConfig.wepKeys
                    + "\nPreSharedKey: " + wifiConfig.preSharedKey;
            */
        } catch (Exception e){return e.getMessage();}
        //return null;//String.valueOf(wifiMgr.getDhcpInfo().gateway);
    }
    /*** - END / HOTSPOT - ***/

    static boolean isWifiConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    static String getWifiName(Context context){
        WifiManager wifiMgr;
        WifiInfo wifiInfo;
        String wifiName;
        if (isWifiConnected(context)) {
            wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            wifiInfo = wifiMgr.getConnectionInfo();
            wifiName=wifiInfo.getSSID();
        } else if(Utils.isHotspotEnabled(context)){
            try {
                wifiMgr = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
                Method getConfigMethod = wifiMgr.getClass().getMethod("getWifiApConfiguration");
                WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiMgr);
                wifiName = wifiConfig.SSID;
            } catch (Exception e){
                wifiName = e.getMessage();
            }
        }
        else {wifiName = context.getString(R.string.no_connection);}
        return (wifiName);
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4  true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    static String getIPAddress(boolean useIPv4) {
        //if(!isHotspotEnabled(App.getMyInstance())) {
            try {
                List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                for (NetworkInterface intf : interfaces) {
                    List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                    for (InetAddress addr : addrs) {
                        if (!addr.isLoopbackAddress()) {
                            String sAddr = addr.getHostAddress();
                            //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                            boolean isIPv4 = sAddr.indexOf(':') < 0;

                            if (useIPv4) {
                                if (isIPv4)
                                    return sAddr;
                            } else {
                                if (!isIPv4) {
                                    int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                    return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                                }
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            } // for now eat exceptions
        //} else { return getGateway(App.getMyInstance());}
        return null;
    }

    static void toggleAdb(boolean active) {
        try {
            Process su = Runtime.getRuntime().exec("su");

            DataOutputStream outputStream = new DataOutputStream(su.getOutputStream());
            String commands [] = new String[]{
                    String.format(Locale.ENGLISH, "setprop service.adb.tcp.port %s", active ? getNetPort()/*NET_PORT*/ : USB_PORT),
                    "stop adbd", "start adbd", "exit"
            };

            for (String c : commands) {
                outputStream.writeBytes(c+"\n");
                outputStream.flush();
            }

            su.waitFor();
        } catch (Exception e) { e.printStackTrace(); }
    }

    static boolean isAdbOverWifiEnabled() {
        try{
            Process su = Runtime.getRuntime().exec("getprop service.adb.tcp.port");
            BufferedReader in = new BufferedReader(new InputStreamReader(su.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
                if (line.equalsIgnoreCase(getNetPort()))//NET_PORT
                    return true;
            su.waitFor();
        } catch(Exception e){
            throw new RuntimeException(e);
        }
        return false;
    }

    static Spanned html(String s) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(s, Html.FROM_HTML_MODE_LEGACY);
        else return Html.fromHtml(s);
    }

    static String getHexColor(int color){
        return String.format("#%06X", (0xFFFFFF & color));
    }
}
