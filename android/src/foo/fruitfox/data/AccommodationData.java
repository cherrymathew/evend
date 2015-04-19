package foo.fruitfox.data;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class AccommodationData {
	private String accommodationType;
	private String bedsCount;
	private DateTime accommodationStartDate;
	private String daysCount;
	private Boolean hasTent;
	private Boolean hasSleeplingBag;
	private Boolean hasMatress;
	private Boolean hasPillow;
	private Boolean hasFamily;
	private String familyDetails;

	public AccommodationData() {
		super();
		this.accommodationType = "";
		this.bedsCount = "";
		this.accommodationStartDate = null;
		this.daysCount = "";
		this.hasTent = false;
		this.hasSleeplingBag = false;
		this.hasMatress = false;
		this.hasPillow = false;
		this.hasFamily = false;
	}

	public AccommodationData(String accommodationType, String bedsCount) {
		super();
		this.accommodationType = accommodationType;
		this.bedsCount = bedsCount;
	}

	public String getAccommodationType() {
		return accommodationType;
	}

	public void setAccommodationType(String accommodationType) {
		this.accommodationType = accommodationType;
	}

	public String getBedsCount() {
		return bedsCount;
	}

	public void setBedsCount(String bedsCount) {
		this.bedsCount = bedsCount;
	}

	public DateTime getStartDate() {
		return accommodationStartDate;
	}

	public void setStartDate(DateTime startDate) {
		this.accommodationStartDate = startDate;
	}

	public String getDaysCount() {
		return daysCount;
	}

	public void setDaysCount(String daysCount) {
		this.daysCount = daysCount;
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
	public String getStartDate(String pattern) {
		String dateString = "";

		if (this.accommodationStartDate != null) {
			DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
			dateString = dtf.print(this.accommodationStartDate);
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
	public void setStartDate(String pattern, String date) {
		if (date.length() > 0 && pattern.length() > 0) {
			String currentTimeZone = TimeZone.getDefault().getID();
			DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
			this.accommodationStartDate = dtf.parseDateTime(date);
			this.accommodationStartDate.withZone(DateTimeZone
					.forID(currentTimeZone));
		}
	}

	public DateTime getAccommodationStartDate() {
		return accommodationStartDate;
	}

	public void setAccommodationStartDate(DateTime accommodationStartDate) {
		this.accommodationStartDate = accommodationStartDate;
	}

	public Boolean getHasTent() {
		return hasTent;
	}

	public void setHasTent(Boolean hasTent) {
		this.hasTent = hasTent;
	}

	public Boolean getHasSleeplingBag() {
		return hasSleeplingBag;
	}

	public void setHasSleeplingBag(Boolean hasSleeplingBag) {
		this.hasSleeplingBag = hasSleeplingBag;
	}

	public Boolean getHasMatress() {
		return hasMatress;
	}

	public void setHasMatress(Boolean hasMatress) {
		this.hasMatress = hasMatress;
	}

	public Boolean getHasPillow() {
		return hasPillow;
	}

	public void setHasPillow(Boolean hasPillow) {
		this.hasPillow = hasPillow;
	}

	public Boolean getHasFamily() {
		return hasFamily;
	}

	public void setHasFamily(Boolean hasFamily) {
		this.hasFamily = hasFamily;
	}

	public String getFamilyDetails() {
		return familyDetails;
	}

	public void setFamilyDetails(String familyDetails) {
		this.familyDetails = familyDetails;
	}

}
