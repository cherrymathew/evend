package foo.fruitfox.data;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class PickupData {
	private String location;
	private DateTime pickupDateTime;
	private String seatsCount;

	public PickupData() {
		super();
		this.location = "";
		this.seatsCount = "";
		this.pickupDateTime = null;
	}

	public PickupData(String location, String pickupDate, String pickupTime,
			String seatsCount) {
		super();
		String currentTimeZone = TimeZone.getDefault().getID();

		this.location = location;
		this.seatsCount = seatsCount;
		this.pickupDateTime = new DateTime(pickupDate + "T" + pickupTime,
				DateTimeZone.forID(currentTimeZone));

	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public DateTime getPickupDateTime() {
		return pickupDateTime;
	}

	public void setPickupDateTime(DateTime pickupDateTime) {
		this.pickupDateTime = pickupDateTime;
	}

	public String getSeatsCount() {
		return seatsCount;
	}

	public void setSeatsCount(String seatsCount) {
		this.seatsCount = seatsCount;
	}

	/**
	 * 
	 * @param pickupTime
	 *            string matching the passed time pattern.
	 * @param pattern
	 *            for the time string to be set. All the patterns adhere to the
	 *            Joda Time string standards.
	 * @see Link
	 *      http://joda-time.sourceforge.net/apidocs/org/joda/time/format/
	 *      DateTimeFormat.html
	 */
	public void setPickupTime(String pattern, String pickupTime) {
		if (pattern.length() > 0 && pickupTime.length() > 0) {
			String dateString = "";
			String currentTimeZone = TimeZone.getDefault().getID();
			DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd");

			if (this.pickupDateTime != null) {
				dateString = dtf.print(this.pickupDateTime);
			} else {
				dateString = "1970-01-01";
			}

			dtf = DateTimeFormat.forPattern("yyyy-MM-dd " + pattern);
			this.pickupDateTime = dtf.parseDateTime(dateString + " "
					+ pickupTime);
			this.pickupDateTime.withZone(DateTimeZone.forID(currentTimeZone));
		}
	}

	/**
	 * @param pattern
	 *            for the time string to be returned. All the patterns adhere to
	 *            the Joda Time string standards.
	 * 
	 * @see Link
	 *      http://joda-time.sourceforge.net/apidocs/org/joda/time/format/
	 *      DateTimeFormat.html
	 * @return The formatted time string according to the pattern passed.
	 */
	public String getPickupTime(String pattern) {
		String timeString = "";

		if (this.pickupDateTime != null) {
			DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
			timeString = dtf.print(this.pickupDateTime);
		}

		return timeString;
	}

	/**
	 * 
	 * @param pickupDate
	 *            string matching the passed date pattern.
	 * @param pattern
	 *            for the date string to be set. All the patterns adhere to the
	 *            Joda Time string standards.
	 * @see Link
	 *      http://joda-time.sourceforge.net/apidocs/org/joda/time/format/
	 *      DateTimeFormat.html
	 */
	public void setPickupDate(String pattern, String pickupDate) {
		if (pattern.length() > 0 && pickupDate.length() > 0) {
			String timeString = "";
			String currentTimeZone = TimeZone.getDefault().getID();
			DateTimeFormatter dtf = DateTimeFormat.forPattern("HH:mm:ss");

			if (this.pickupDateTime != null) {
				timeString = dtf.print(this.pickupDateTime);
			} else {
				timeString = "00:00:00";
			}

			dtf = DateTimeFormat.forPattern(pattern + " HH:mm:ss");
			this.pickupDateTime = dtf.parseDateTime(pickupDate + " "
					+ timeString);
			this.pickupDateTime.withZone(DateTimeZone.forID(currentTimeZone));
		}
	}

	/**
	 * @param pattern
	 *            for the date string to be returned. All the patterns adhere to
	 *            the Joda Time string standards.
	 * 
	 * @see Link
	 *      http://joda-time.sourceforge.net/apidocs/org/joda/time/format/
	 *      DateTimeFormat.html
	 * @return The formatted time string according to the pattern passed.
	 */
	public String getPickupDate(String pattern) {
		String dateString = "";

		if (this.pickupDateTime != null) {
			DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
			dateString = dtf.print(this.pickupDateTime);
		}

		return dateString;
	}
}
