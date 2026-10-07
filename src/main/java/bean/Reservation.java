package bean;

import java.io.Serializable;
import java.sql.Date;

public class Reservation implements Serializable {
    private int reservationId;
    private int scheduleId;
    private String ticketType;
    private int totalPrice;
    private Date reservedAt;
    private String status;

    public int getReservationId(){
        return reservationId;
    }
    public void setReservationId(int reservationId){
        this.reservationId = reservationId;
    }
    public int getSheduleId(){
        return scheduleId;
    }
    public void setSheduleId(int sheduleId){
        this.scheduleId = sheduleId;
    }
    public String getTicketType(){
        return ticketType;
    }
    public void setTicketType(String ticketType){
        this.ticketType = ticketType;
    }
    public int getTotalPrice(){
        return totalPrice;
    }
    public void setTotalPrice(int totalPrice){
        this.totalPrice = totalPrice;
    }
    public Date getReservedAt(){
        return reservedAt;
    }
    public void serReservedAt(Date reservedAt){
        this.reservedAt = reservedAt;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status){
        this.status = status;
    }
}
