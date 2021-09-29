package lib.kalu.mediaplayer.cache;

import androidx.annotation.IntDef;
import androidx.annotation.Keep;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Keep
@Retention(RetentionPolicy.SOURCE)
public @interface CacheType {

    int RAM = 0x0001;
    int ROM = 0x0002;
    int ALL = 0x0003;

    @IntDef(value = {CacheType.ALL, CacheType.RAM, CacheType.ROM})
    @Retention(RetentionPolicy.SOURCE)
    @Keep
    @interface Value {
    }
}