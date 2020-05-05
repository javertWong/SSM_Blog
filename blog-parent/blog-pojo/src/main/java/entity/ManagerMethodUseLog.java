package entity;

import java.util.Date;

public class ManagerMethodUseLog {
	private String managerMethodName;
	private Date startDate;
	private Long lastTime;
	private String ip;
	public String getManagerMethodName() {
		return managerMethodName;
	}
	public void setManagerMethodName(String managerMethodName) {
		this.managerMethodName = managerMethodName;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Long getLastTime() {
		return lastTime;
	}
	public void setLastTime(Long lastTime) {
		this.lastTime = lastTime;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
}
