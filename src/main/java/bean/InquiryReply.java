package bean;

import java.io.Serializable;
import java.sql.Date;

public class InquiryReply implements Serializable {
    private int replyId;
    private int inquirId;
    private String replyBody;
    private String staffName;
    private Date repliedAt;

    public int getReplyId(){
        return replyId;
    }
    public void setReplyId(int replyId){
        this.replyId = replyId;
    }
    public int getInquiryId(){
        return inquirId;
    }
    public void setInquiryId(int inquiryId){
        this.inquirId = inquiryId;
    }
    public String getReplyBody(){
        return replyBody;
    }
    public void setReplyBody(String replyBody){
        this.replyBody = replyBody;
    }
    public String getStaffName(){
        return staffName;
    }
    public void setStaffName(String staffName){
        this.staffName = staffName;
    }
    public Date getRepliedAt(){
        return repliedAt;
    }
    public void setRepliedAt(Date repliedAt){
        this.repliedAt = repliedAt;
    }
}
