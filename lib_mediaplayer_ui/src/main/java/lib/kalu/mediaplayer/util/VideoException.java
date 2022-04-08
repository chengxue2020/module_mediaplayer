package lib.kalu.mediaplayer.util;

public class VideoException extends RuntimeException {

    private int mCode = 0;

    public VideoException(int code, String msg) {
        super(msg);
        mCode = code;
    }

    @Deprecated
    public VideoException(String msg) {
        super(msg);
    }

    public VideoException(Throwable throwable) {
        super(throwable);
        if (throwable instanceof VideoException) {
            mCode = ((VideoException) throwable).getCode();
        }
    }

    public int getCode() {
        return mCode;
    }

    public String getMsg() {
        return getMessage();
    }

    //自定义RenderView不能设置为null
    public static final int CODE_NOT_RENDER_FACTORY = 19;
    //PlayerFactory不能设置为null
    public static final int CODE_NOT_PLAYER_FACTORY = 20;
    //VideoPlayer，需要在start播放前给设置控制器Controller
    public static final int CODE_NOT_SET_CONTROLLER = 21;

}
