package lib.kalu.mediaplayer.cache;

import androidx.annotation.IntDef;
import androidx.annotation.Keep;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Keep
@Retention(RetentionPolicy.SOURCE)
public @interface CacheType {

    int DEFAULT = 0x0001;
    int NONE = 0x0002;

    @IntDef(value = {CacheType.DEFAULT, CacheType.NONE})
    @Retention(RetentionPolicy.SOURCE)
    @Keep
    @interface Value {
    }
}