package foo.fruitfox.data;

import java.util.List;

public class UserData {
	private String email;
	private String phone;
	private String registrationIdType;
	private String firstName;
	private String lastName;
	private String verificationCode;
	private String authToken;
	private Boolean[] eventDaysAttending;
	private Boolean needsPickUp;
	private Boolean needsAccommodation;
	private Boolean hasTalk;
	private Boolean hasOauth;
	private Boolean isVerified;
	private UserOauthData oauthData;
	private List<TalkData> talkDataList;

	public UserData(String registrationIdType, String data) {
		super();

		this.registrationIdType = registrationIdType;

		if (registrationIdType == "email") {
			this.email = data;
		} else if (registrationIdType == "phone") {
			this.phone = data;
		}

		this.needsPickUp = false;
		this.needsAccommodation = false;
		this.hasTalk = false;
		this.hasOauth = false;
		this.isVerified = false;
	}

	public UserData(String oauthProvider, String oauthAccessToken,
			String oauthUserId) {
		super();

		this.registrationIdType = "oauth";
		this.oauthData = new UserOauthData(oauthProvider, oauthAccessToken,
				oauthUserId);

		this.needsPickUp = false;
		this.needsAccommodation = false;
		this.hasTalk = false;
		this.hasOauth = true;
		this.isVerified = false;
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

	public String getRegistrationIdType() {
		return registrationIdType;
	}

	public void setRegistrationIdType(String registrationIdType) {
		this.registrationIdType = registrationIdType;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullName() {
		return this.firstName + " " + this.lastName;
	}

	public String getVerificationCode() {
		return this.verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}

	public Boolean[] getEventDaysAttending() {
		return eventDaysAttending;
	}

	public void setEventDaysAttending(Boolean[] eventDaysAttending) {
		this.eventDaysAttending = eventDaysAttending;
	}

	public Boolean getNeedsPickUp() {
		return needsPickUp;
	}

	public void setNeedsPickUp(Boolean needsPickUp) {
		this.needsPickUp = needsPickUp;
	}

	public Boolean getNeedsAccommodation() {
		return needsAccommodation;
	}

	public void setNeedsAccommodation(Boolean needsAccommodation) {
		this.needsAccommodation = needsAccommodation;
	}

	public Boolean getHasTalk() {
		return hasTalk;
	}

	public void setHasTalk(Boolean hasTalk) {
		this.hasTalk = hasTalk;
	}

	public Boolean getHasOauth() {
		return hasOauth;
	}

	public void setHasOauth(Boolean hasOauth) {
		this.hasOauth = hasOauth;
	}

	public Boolean getIsVerified() {
		return this.isVerified;
	}

	public void setIsVerified(Boolean isVerified) {
		this.isVerified = isVerified;
	}

	public UserOauthData getOauthData() {
		return oauthData;
	}

	public void setOauthData(UserOauthData oauthData) {
		this.oauthData = oauthData;
	}

	public List<TalkData> getTalkDataList() {
		return talkDataList;
	}

	public void setTalkDataList(List<TalkData> talkDataList) {
		this.talkDataList = talkDataList;
	}

	public String getAuthToken() {
		return authToken;
	}

	public void setAuthToken(String authToken) {
		this.authToken = authToken;
	}

}
