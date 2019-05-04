package wayforlife.com.wfl.Modal_class;

public class single_event {

    String eventDate;
    String eventTitle,eventDesc,eventAddress;


    public single_event()
    {}

    public single_event(String date, String title, String desc, String address)
    {
        this.eventDate=date;
        this.eventTitle=title;
        this.eventDesc=desc;
        this.eventAddress=address;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getEventAddress() {
        return eventAddress;
    }

    public void setEventAddress(String eventAddress) {
        this.eventAddress = eventAddress;
    }
}
