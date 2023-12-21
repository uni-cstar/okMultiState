package unics.okmultistate;


import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 状态类型
 */
@Retention(RetentionPolicy.SOURCE)
@IntDef(value = {Status.UNKNOWN, Status.CONTENT, Status.EMPTY, Status.LOADING, Status.ERROR, Status.NO_NETWORK})
public @interface Status {
    int UNKNOWN = -1;
    int CONTENT = 0x100;
    int LOADING = 0x101;
    int EMPTY = 0x102;
    int ERROR = 0x103;
    int NO_NETWORK = 0x104;
}
