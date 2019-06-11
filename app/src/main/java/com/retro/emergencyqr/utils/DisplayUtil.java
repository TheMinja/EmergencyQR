package com.retro.emergencyqr.utils;

import android.content.Context;
import android.util.TypedValue;

/**
 * Created by tommy on 10/June/2019.
 */

public class DisplayUtil {

    /**
     * Private constructor.
     */
    private DisplayUtil() {
    }

    public static float dpToPx(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }


    public static float pxToDp(Context context, float px) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, px, context.getResources().getDisplayMetrics());
    }

    public static float spToPx(Context context, float sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }
}
