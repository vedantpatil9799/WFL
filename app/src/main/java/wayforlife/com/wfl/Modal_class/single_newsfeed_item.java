package wayforlife.com.wfl.Modal_class;

import java.io.Serializable;

public class single_newsfeed_item implements Serializable {

    public static final int TEXT_TYPE=0;
    public static final int IMAGE_TYPE=1;
    public static final int VIDEO_TYPE=2;

    private int type,DownvoteCount,UpvoteCount,op1_count,op2_count;
    private String date,poll_option1,poll_option2,UserCaption,pollText;
    private String name;
    private String url_or_text;
    private boolean isPoll;



    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getDownvoteCount() {
        return DownvoteCount;
    }

    public void setDownvoteCount(int downvoteCount) {
        DownvoteCount = downvoteCount;
    }

    public int getUpvoteCount() {
        return UpvoteCount;
    }

    public void setUpvoteCount(int upvoteCount) {
        UpvoteCount = upvoteCount;
    }

    public int getOp1_count() {
        return op1_count;
    }

    public void setOp1_count(int op1_count) {
        this.op1_count = op1_count;
    }

    public int getOp2_count() {
        return op2_count;
    }

    public void setOp2_count(int op2_count) {
        this.op2_count = op2_count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPoll_option1() {
        return poll_option1;
    }

    public void setPoll_option1(String poll_option1) {
        this.poll_option1 = poll_option1;
    }

    public String getPoll_option2() {
        return poll_option2;
    }

    public void setPoll_option2(String poll_option2) {
        this.poll_option2 = poll_option2;
    }

    public String getUserCaption() {
        return UserCaption;
    }

    public void setUserCaption(String userCaption) {
        UserCaption = userCaption;
    }

    public String getPollText() {
        return pollText;
    }

    public void setPollText(String pollText) {
        this.pollText = pollText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl_or_text() {
        return url_or_text;
    }

    public void setUrl_or_text(String url_or_text) {
        this.url_or_text = url_or_text;
    }

    public boolean isPoll() {
        return isPoll;
    }

    public void setPoll(boolean poll) {
        isPoll = poll;
    }

    public single_newsfeed_item()
    {}

}
