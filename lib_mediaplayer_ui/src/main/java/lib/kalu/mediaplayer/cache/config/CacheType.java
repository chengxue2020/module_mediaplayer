package lib.kalu.mediaplayer.cache.config;

import androidx.annotation.IntDef;
import androidx.annotation.Keep;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Keep
@Retention(RetentionPolicy.SOURCE)
public @interface CacheType {

    int RAM = 0;
    int ROM = 1;
    int ALL = 2;

    @IntDef(value = {CacheType.ALL, CacheType.RAM, CacheType.ROM})
    @Retention(RetentionPolicy.SOURCE)
    @Keep
    @interface Value {
    }
}