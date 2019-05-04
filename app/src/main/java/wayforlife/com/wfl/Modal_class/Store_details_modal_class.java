package wayforlife.com.wfl.Modal_class;

public class Store_details_modal_class
{
    public String user_name,user_id,user_phone_no,user_email_id,user_photo,panel,reg_status;

    public Store_details_modal_class() {
    }

    public Store_details_modal_class(String user_name, String user_id, String user_phone_no, String user_email_id, String user_photo, String panel, String reg_status) {
        this.user_name = user_name;
        this.user_id = user_id;
        this.user_phone_no = user_phone_no;
        this.user_email_id = user_email_id;
        this.user_photo = user_photo;
        this.panel = panel;
        this.reg_status = reg_status;
    }

    public String getPanel() {
        return panel;
    }

    public void setPanel(String panel) {
        this.panel = panel;
    }

    public String getReg_status() {
        return reg_status;
    }

    public void setReg_status(String reg_status) {
        this.reg_status = reg_status;
    }

    public String getUser_panel() {
        return panel;
    }

    public void setUser_panel(String user_panel) {
        this.panel = user_panel;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_phone_no() {
        return user_phone_no;
    }

    public void setUser_phone_no(String user_phone_no) {
        this.user_phone_no = user_phone_no;
    }

    public String getUser_email_id() {
        return user_email_id;
    }

    public void setUser_email_id(String user_email_id) {
        this.user_email_id = user_email_id;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public void setUser_photo(String user_photo) {
        this.user_photo = user_photo;
    }
}
