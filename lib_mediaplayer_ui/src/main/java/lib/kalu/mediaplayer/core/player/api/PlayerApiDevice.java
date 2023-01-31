package lib.kalu.mediaplayer.core.player.api;

import android.content.Context;
import android.view.ViewGroup;

import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.util.SpeedUtil;

public interface PlayerApiDevice extends PlayerApiBase {

    void setScreenKeep(boolean enable);

    default String getTcpSpeed() {
        try {
            ViewGroup layout = getLayout();
            Context context = layout.getContext();
            String speed = SpeedUtil.getNetSpeed(context);
            MPLogUtil.log("PlayerApiDevice => getTcpSpeed => speed = " + speed);
            return speed;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiDevice => getTcpSpeed => " + e.getMessage());
            return "0kb/s";
        }
    }
}
