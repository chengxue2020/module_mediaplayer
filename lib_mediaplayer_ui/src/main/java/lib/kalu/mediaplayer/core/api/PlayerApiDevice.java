package lib.kalu.mediaplayer.core.api;

import lib.kalu.mediaplayer.util.MPLogUtil;
import lib.kalu.mediaplayer.util.SpeedUtil;

public interface PlayerApiDevice extends PlayerApiBase {

    void setScreenKeep(boolean enable);

    default String getTcpSpeed() {
        try {
            String speed = SpeedUtil.getNetSpeed(getBaseContext());
            MPLogUtil.log("PlayerApiDevice => getTcpSpeed => speed = " + speed);
            return speed;
        } catch (Exception e) {
            MPLogUtil.log("PlayerApiDevice => getTcpSpeed => " + e.getMessage());
            return "0kb/s";
        }
    }
}
