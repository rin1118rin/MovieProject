package bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Reservation implements Serializable {
    private int reservationId;
    private int scheduleId;
    private String customerName;
    private String ticketType; // 互換性のため残す
    private int totalPrice;
    private Date reservedAt;
    private String status;

    // 管理者側JSPのループ表示用リスト
    private List<String> seats;
    private List<TicketInfo> tickets;

    // 券種と数量をまとめる内部クラス
    public static class TicketInfo {
        private String ticketType;
        private int quantity;

        public TicketInfo(String ticketType, int quantity) {
            this.ticketType = ticketType;
            this.quantity = quantity;
        }
        public String getTicketType() { return ticketType; }
        public int getQuantity() { return quantity; }
    }

    public int getReservationId(){ return reservationId; }
    public void setReservationId(int reservationId){ this.reservationId = reservationId; }

    public int getScheduleId(){ return scheduleId; }
    public void setScheduleId(int scheduleId){ this.scheduleId = scheduleId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getTicketType(){ return ticketType; }
    public void setTicketType(String ticketType){ this.ticketType = ticketType; }

    public int getTotalPrice(){ return totalPrice; }
    public void setTotalPrice(int totalPrice){ this.totalPrice = totalPrice; }

    public Date getReservedAt(){ return reservedAt; }
    public void setReservedAt(Date reservedAt){ this.reservedAt = reservedAt; }

    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status = status; }

    public List<String> getSeats() { return seats; }
    public void setSeats(List<String> seats) { this.seats = seats; }

    public List<TicketInfo> getTickets() { return tickets; }
    public void setTickets(List<TicketInfo> tickets) { this.tickets = tickets; }
}