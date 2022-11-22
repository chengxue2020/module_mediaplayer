package lib.kalu.mediaplayer.listener;

import androidx.annotation.Keep;

@Keep
public interface OnFullChangeListener {

    void onFull();

    void onNormal();
}
