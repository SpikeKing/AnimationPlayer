package org.wangchenlong.animationplayer;

import android.content.Context;

/**
 * 工具类
 * <p>
 * Created by wangchenlong on 17/1/3.
 */
public class Utils {
    public static int dp2px(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
