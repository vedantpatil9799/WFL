package wayforlife.com.wfl.Modal_class;

public class Store_spam_modal_class {
    public String s_uid,s_time,s_type,s_ph_url;

    public Store_spam_modal_class() {
    }

    public Store_spam_modal_class(String s_uid, String s_time, String s_type) {
        this.s_uid = s_uid;
        this.s_time = s_time;
        this.s_type = s_type;
    }

    public String getS_type() {
        return s_type;
    }

    public void setS_type(String s_type) {
        this.s_type = s_type;
    }

    public String getS_uid() {
        return s_uid;
    }

    public void setS_uid(String s_uid) {
        this.s_uid = s_uid;
    }

    public String getS_time() {
        return s_time;
    }

    public void setS_time(String s_time) {
        this.s_time = s_time;
    }
}
