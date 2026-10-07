package bean;

import java.io.Serializable;
import java.sql.Date;

public class Inquiry implements Serializable {
    private int inquiryId;
    private String name;
    private String email;
    private String subject;
    private String body;
    private Date sentAt;
    private String status;
    
    public int getInquiryId(){
        return inquiryId;
    }
    public void setInquiryId(int inquiryId){
        this.inquiryId = inquiryId;
    }
    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getSubject(){
        return subject;
    }
    public void setSubject(String subject){
        this.subject = subject;
    }
    public String getBody(){
        return body;
    }
    public void setBody(String body){
        this.body = body;
    }
    public Date getSentAt(){
        return sentAt;
    }
    public void setSentAt(Date sentAt){
        this.sentAt = sentAt;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }
    
}
