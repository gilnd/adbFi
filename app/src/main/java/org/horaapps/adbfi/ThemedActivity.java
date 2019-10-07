package org.horaapps.adbfi;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.widget.Button;
import android.widget.TextView;

import com.mikepenz.iconics.view.IconicsImageView;

/**
 * Created by gilbertndr on 08/02/2017.
 */
public class ThemedActivity extends AppCompatActivity {

    /*** - GET COLORS - ***/
    public int getPrimaryColor(){return getColor(R.color.colorPrimary);}
    public int getPrimaryDarkColor(){return  getColor(R.color.colorPrimaryDark);}
    public int getAccentColor(){return getColor(R.color.colorAccent);}
    public int getBackgroundColor(){return  getColor(R.color.colorBackground);}
    public int getCardBackgroundColor(){return getColor(R.color.colorCardBackground);}
    public int getItemBackgroundColor(){return getColor(R.color.colorItemBackground);}
    public int getTextColor(){return getColor(R.color.colorText);}
    public int getSubTextColor(){return getColor(R.color.colorSubText);}
    public int getIconColor(){return getColor(R.color.colorIcon);}
    public int getActiveColor(){return getColor(R.color.colorActive);}
    public int getDisabledColor(){return getColor(R.color.colorDisabled);}

    /*** - SET ITEM COLORS - ***/
    public void setTextViewColor(TextView txt, int color){txt.setTextColor(color);}
    public void setIconColor(IconicsImageView icon, int color){icon.setColor(color);}
    public void setButtonColor(Button btn, int textColor, int backgroundColor){
        btn.setTextColor(textColor);
        btn.setBackgroundColor(backgroundColor);
    }

    public void setSwitchCompactColor(SwitchCompat sw, int color){
        /** SWITCH HEAD **/
        sw.getThumbDrawable().setColorFilter(
                sw.isChecked() ? color : getColor(R.color.colorText),
                PorterDuff.Mode.MULTIPLY);
        /** SWITCH BODY **/
        sw.getTrackDrawable().setColorFilter(
                sw.isChecked() ? getTransparentColor(color,100) : getTransparentColor(R.color.colorSubText, 80),
                PorterDuff.Mode.MULTIPLY);
    }
    public void setCardViewBackgroundColor(CardView cardView, int color){
        cardView.setBackgroundColor(color);
    }

    /*** - UTILS - ***/
    public static int getTransparentColor(int color, int alpha){
        return  ColorUtils.setAlphaComponent(color, alpha);
    }
    public static String getHexColor(int color) {
        return String.format("#%06X", (0xFFFFFF & color));
    }
    public static int getDarkerColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.72f; // value component
        return Color.HSVToColor(hsv);
    }
}
