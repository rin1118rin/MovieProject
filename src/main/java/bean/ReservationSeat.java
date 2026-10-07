package bean;

import java.io.Serializable;

public class ReservationSeat implements Serializable {
	
	private int reservationSeatId;
	
	private int reservationId;
	
	private String seatNo;
	
	public int getReservationSeatId() {
		return reservationSeatId;
	}
	
	public int getReservationId() {
		return reservationId;
	}
	
	public String getSeatNo() {
		return seatNo;
	}
	
	public void setReservationSeatId(int reservationSeatId) {
		this.reservationSeatId = reservationSeatId;
	}
	
	public void setReservationId(int reservationId) {
		this.reservationId = reservationId;
	}
	
	public void setSeatNo(String seatNo) {
		this.seatNo = seatNo;
	}
	
}