package foo.fruitfox.data;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class UserData {
	private String email;
	private String phone;
	private String registrationIdType;
	private String firstName;
	private String lastName;
	private String verificationCode;
	private String authToken;
	private Boolean[] eventDaysAttending;
	private DateTime attendanceStartDate;
	private DateTime attendanceEndDate;
	private Boolean needsPickUp;
	private Boolean needsAccommodation;
	private Boolean hasTalk;
	private Boolean hasOauth;
	private Boolean isVerified;
	private Boolean isFinalized;
	private Boolean isAttendingMainConference;
	private Boolean isAttendingPreConference;
	private Boolean isAttendingLearnToCode;
	private Boolean isTeachingSchoolOutreach;
	private Boolean isTeachingLearnToCode;
	private UserOauthData oauthData;
	private AccommodationData accommodationData;
	private PickupData pickupData;
	private List<TalkData> talkDataList;

	public UserData(String registrationIdType, String data) {
		super();

		this.registrationIdType = registrationIdType;

		if (registrationIdType.equalsIgnoreCase("email")) {
			this.email = data;
		} else if (registrationIdType.equalsIgnoreCase("phone")) {
			this.phone = data;
		}

		this.needsPickUp = false;
		this.needsAccommodation = false;
		this.hasTalk = false;
		this.hasOauth = false;
		this.isVerified = false;
		this.isFinalized = false;
		this.talkDataList = new ArrayList<TalkData>();
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
		this.isFinalized = false;
		this.talkDataList = new ArrayList<TalkData>();
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

	/**
	 * 
	 * @return Always returns FirstName + " " + LastName
	 */
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
		return this.hasTalk;
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
		return isVerified;
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

	public AccommodationData getAccommodationData() {
		return accommodationData;
	}

	public void setAccommodationData(AccommodationData accommodationData) {
		this.accommodationData = accommodationData;
	}

	public PickupData getPickupData() {
		return pickupData;
	}

	public void setPickupData(PickupData pickupData) {
		this.pickupData = pickupData;
	}

	public Boolean getIsFinalized() {
		return isFinalized;
	}

	public void setIsFinalized(Boolean isFinalized) {
		this.isFinalized = isFinalized;
	}

	public DateTime getAttendanceStartDate() {
		return attendanceStartDate;
	}

	public void setAttendanceStartDate(DateTime attendanceStartDate) {
		this.attendanceStartDate = attendanceStartDate;
	}

	/**
	 * @param pattern
	 *            for the date string to be returned. All the patterns adhere to
	 *            the Joda Time string standards.
	 * 
	 * @see Link
	 *      http://joda-time.sourceforge.net/apidocs/org/joda/time/format/
	 *      DateTimeFormat.html
	 * @return The formatted date string according to the pattern passed.
	 */
	public String getAttendanceStartDate(String pattern) {
		String dateString = "";

		if (this.attendanceStartDate != null) {
			DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
			dateString = dtf.print(this.attendanceStartDate);
		}

		return dateString;
	}

	/**
	 * 
	 * @param date
	 *            string matching the passed date pattern.
	 * @param pattern
	 *            for the date string to be returned. All the patterns adhere to
	 *            the Joda Time string standards.
	 * @see Link
	 *      http://joda-time.sourceforge.net/apidocs/org/joda/time/format/
	 *      DateTimeFormat.html
	 */
	public void setAttendanceStartDate(String pattern, String date) {
		if (date.length() > 0 && pattern.length() > 0) {
			String currentTimeZone = TimeZone.getDefault().getID();
			DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
			this.attendanceStartDate = dtf.parseDateTime(date);
			this.attendanceStartDate.withZone(DateTimeZone
					.forID(currentTimeZone));
		}
	}

	public DateTime getAttendanceEndDate() {
		return attendanceEndDate;
	}

	public void setAttendanceEndDate(DateTime attendanceEndDate) {
		this.attendanceEndDate = attendanceEndDate;
	}

	/**
	 * @param pattern
	 *            for the date string to be returned. All the patterns adhere to
	 *            the Joda Time string standards.
	 * 
	 * @see Link
	 *      http://joda-time.sourceforge.net/apidocs/org/joda/time/format/
	 *      DateTimeFormat.html
	 * @return The formatted date string according to the pattern passed.
	 */
	public String getAttendanceEndDate(String pattern) {
		String dateString = "";

		if (this.attendanceEndDate != null) {
			DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
			dateString = dtf.print(this.attendanceEndDate);
		}

		return dateString;
	}

	/**
	 * 
	 * @param date
	 *            string matching the passed date pattern.
	 * @param pattern
	 *            for the date string to be returned. All the patterns adhere to
	 *            the Joda Time string standards.
	 * @see Link
	 *      http://joda-time.sourceforge.net/apidocs/org/joda/time/format/
	 *      DateTimeFormat.html
	 */
	public void setAttendanceEndDate(String pattern, String date) {
		if (date.length() > 0 && pattern.length() > 0) {
			String currentTimeZone = TimeZone.getDefault().getID();
			DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
			this.attendanceEndDate = dtf.parseDateTime(date);
			this.attendanceEndDate
					.withZone(DateTimeZone.forID(currentTimeZone));
		}
	}

	public void setEventDaysAttending(DateTime eventStartDate,
			DateTime eventEndDate) {
		int daysCount = Days.daysBetween(eventStartDate.toLocalDate(),
				eventEndDate.toLocalDate()).getDays() + 1;

		this.eventDaysAttending = new Boolean[daysCount];
		java.util.Arrays.fill(this.eventDaysAttending, false);

		for (int i = 0; i < daysCount; i++) {
			if (eventStartDate.plusDays(i).isAfter(attendanceEndDate)) {
				break;
			}

			if (attendanceStartDate.isEqual(eventStartDate.plusDays(i))
					|| eventStartDate.plusDays(i).isAfter(attendanceStartDate)) {
				eventDaysAttending[i] = true;
			}
		}
	}

	public Boolean getIsAttendingMainConference() {
		return isAttendingMainConference;
	}

	public void setIsAttendingMainConference(Boolean isAttendingMainConference) {
		this.isAttendingMainConference = isAttendingMainConference;
	}

	public Boolean getIsAttendingPreConference() {
		return isAttendingPreConference;
	}

	public void setIsAttendingPreConference(Boolean isAttendingPreConference) {
		this.isAttendingPreConference = isAttendingPreConference;
	}

	public Boolean getIsAttendingLearnToCode() {
		return isAttendingLearnToCode;
	}

	public void setIsAttendingLearnToCode(Boolean isAttendingLearnToCode) {
		this.isAttendingLearnToCode = isAttendingLearnToCode;
	}

	public Boolean getIsTeachingSchoolOutreach() {
		return isTeachingSchoolOutreach;
	}

	public void setIsTeachingSchoolOutreach(Boolean isTeachingSchoolOutreach) {
		this.isTeachingSchoolOutreach = isTeachingSchoolOutreach;
	}

	public Boolean getIsTeachingLearnToCode() {
		return isTeachingLearnToCode;
	}

	public void setIsTeachingLearnToCode(Boolean isTeachingLearnToCode) {
		this.isTeachingLearnToCode = isTeachingLearnToCode;
	}
}
