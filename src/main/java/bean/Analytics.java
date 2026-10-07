package bean;

import java.io.Serializable;

public class Analytics implements Serializable{
	
	private int scheduleId;
	
	private String movieTitle;
	
	private double occupancyRate;
	
	private int totalSeats;
	
	private int reservedSeats;
	
	private double maleRatio;
	
	private double femaleRatio;
	
	public int getScheduleId() {
		return scheduleId;
	}
	
	public String getMovieTitle() {
		return movieTitle;
	}
	
	public double getOccupancyRate() {
		return occupancyRate;
	}
	
	public int getTotalSeats() {
		return totalSeats;
	}
	
	public int getReservedSeats() {
		return reservedSeats;
	}
	
	public double getMaleRatio() {
		return maleRatio;
	}
	
	public double getFameleRatio() {
		return femaleRatio;
	}
	
	public void setScheduleId(int scheduleId) {
		this.scheduleId = scheduleId;
	}
	
	public void setMovieTitle(String movieTitle) {
		this.movieTitle = movieTitle;
	}
	
	public void setOccupancyRate(double occupancyRate) {
		this.occupancyRate = occupancyRate;
	}
	
	public void setTotalSeats(int totalSeats) {
		this.totalSeats = totalSeats;
	}
	
	public void setReservedSeats(int reservedSeats) {
		this.reservedSeats = reservedSeats;
	}
	
	public void setMaleRatio(double maleRatio) {
		this.maleRatio = maleRatio;
	}
	
	public void setFemaleRatio(double femaleRatio) {
		this.femaleRatio = femaleRatio;
	}
}