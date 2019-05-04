package wayforlife.com.wfl.Modal_class;

public class Problem_report_modal_class
{
    public String uri,caption,desc,uid,time;
    public double latitude,longitude;

    public Problem_report_modal_class(String uid,String uri, double latitude, double longitude,String time) {
        this.uid=uid;
        this.uri = uri;
        this.latitude = latitude;
        this.longitude = longitude;
        this.time=time;
    }

    public Problem_report_modal_class(String uid,String uri, String caption, double latitude, double longitude,String desc,String time) {
        this.uid=uid;
        this.uri = uri;
        this.caption = caption;
        this.latitude = latitude;
        this.longitude = longitude;
        this.desc=desc;
        this.time=time;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Problem_report_modal_class() {

    }
}
