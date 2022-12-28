package lib.kalu.mediaplayer.core.view;

interface PlayerApiSuface {

    /**
     * 清屏
     */
    void clearSuface();

    /**
     * 刷屏
     */
    void updateSuface();

    /**
     * 截图
     *
     * @return
     */
    String screenshot();
}
