package lib.kalu.mediaplayer.videoui.config;


import androidx.annotation.Keep;

import java.io.Serializable;
import java.util.Map;

/**
 * @description: 视频信息实体类
 * @date:  2021-05-12 14:49
 */
@Keep
public class VideoInfoBean implements Serializable {

    /**
     * 视频的标题
     */
    private String title;
    /**
     * 播放的视频地址
     */
    private String videoUrl;
    /**
     * 请求header
     */
    private Map<String, String> headers;
    /**
     * 视频封面
     */
    private String cover;
    /**
     * 视频时长
     */
    private long length;
    /**
     * 清晰度等级
     */
    private String grade;
    /**
     * 270P、480P、720P、1080P、4K ...
     */
    private String p;

    public VideoInfoBean(String title, String cover, String url) {
        this.title = title;
        this.videoUrl = url;
        this.cover = cover;
    }

    public VideoInfoBean(String title ,String grade, String p, String videoUrl) {
        this.title = title;
        this.grade = grade;
        this.p = p;
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getP() {
        return p;
    }

    public void setP(String p) {
        this.p = p;
    }
}
