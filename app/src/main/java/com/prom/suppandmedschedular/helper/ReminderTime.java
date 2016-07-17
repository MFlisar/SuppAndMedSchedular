package com.prom.suppandmedschedular.helper;

public class ReminderTime {

	private int hour;
	private int minutes;
	
	public ReminderTime()
	{
		hour = -1;
		minutes = -1;
	}
	
	public ReminderTime(int hour, int minutes)
	{
		this.hour = hour;
		this.minutes = minutes;
	}
	
	public String toString()
	{
		if (hour == -1 && minutes == -1)
			return "";
		else
			return String.valueOf(hour) + ":" + String.valueOf(minutes);
	}
	
	public void fromString(String string)
	{
		if (string == null || string.equals(""))
		{
			hour = minutes = -1;
		}
		else
		{
			String[] parts = string.split(":");
			hour = Integer.parseInt(parts[0]);
			minutes = Integer.parseInt(parts[1]);
		}
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinutes() {
		return minutes;
	}

	public void setMinutes(int minutes) {
		this.minutes = minutes;
	}
}
