package foo.fruitfox.data;

import java.util.ArrayList;

public class UserData {
	private String email;
	private String phone;
	private Boolean[] eventDays;
	private Boolean pickUp;
	private Boolean accomodation;
	private ArrayList<SessionData> sessionDataList;

	public UserData(String email, String phone, Boolean[] eventDays,
			Boolean pickUp, Boolean accomodation,
			ArrayList<SessionData> sessionDataList) {
		super();
		this.email = email;
		this.phone = phone;
		this.eventDays = eventDays;
		this.pickUp = pickUp;
		this.accomodation = accomodation;
		this.sessionDataList = sessionDataList;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Boolean[] getEventDays() {
		return eventDays;
	}

	public void setEventDays(Boolean[] eventDays) {
		this.eventDays = eventDays;
	}

	public Boolean getPickUp() {
		return pickUp;
	}

	public void setPickUp(Boolean pickUp) {
		this.pickUp = pickUp;
	}

	public Boolean getAccomodation() {
		return accomodation;
	}

	public void setAccomodation(Boolean accomodation) {
		this.accomodation = accomodation;
	}

	public ArrayList<SessionData> getSessionDataList() {
		return sessionDataList;
	}

	public void setSessionDataList(ArrayList<SessionData> sessionDataList) {
		this.sessionDataList = sessionDataList;
	}

}
