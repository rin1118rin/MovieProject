package bean;

import java.io.Serializable;
import java.util.Date;

public class Schedule implements Serializable {
    private int scheduleId;
    private int movieId;
    private int screenNo;
    private Date startDatetime;
    private Date endDatetime;
    private String screeningFormat;

    // --- 追加：JSPが表示を求めているプロパティ ---
    private String movieTitle;
    private int reservedSeats;
    private int totalSeats;

    public int getScheduleId(){
        return scheduleId;
    }
    public void setScheduleId(int scheduleId){
        this.scheduleId = scheduleId;
    }
    public int getMovieId(){
        return movieId;
    }
    public void setMovieId(int movieId){
        this.movieId = movieId;
    }
    public int getScreenNo(){
        return screenNo;
    }
    public void setScreenNo(int screenNo){
        this.screenNo = screenNo;
    }
    public Date getStartDatetime(){
        return startDatetime;
    }
    public void setStartDatetime(Date startDatetime){
        this.startDatetime = startDatetime;
    }
    public Date getEndDatetime(){
        return endDatetime;
    }
    public void setEndDatetime(Date endDatetime){
        this.endDatetime = endDatetime;
    }
    public String getScreeningFormat(){
        return screeningFormat;
    }
    public void setScreeningFormat(String screeningFormat){
        this.screeningFormat = screeningFormat;
    }

    // --- 追加したプロパティの Getter / Setter ---
    public String getMovieTitle() {
        return movieTitle;
    }
    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }
    public int getReservedSeats() {
        return reservedSeats;
    }
    public void setReservedSeats(int reservedSeats) {
        this.reservedSeats = reservedSeats;
    }
    public int getTotalSeats() {
        return totalSeats;
    }
    public void setTotalSeats(int totalSeats) {
        this.totalSeats = totalSeats;
    }
}