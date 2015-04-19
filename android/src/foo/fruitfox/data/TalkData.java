package foo.fruitfox.data;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TalkData {

	private String title;
	private String notes;
	private DateTime date;
	private String type;
	private String duration;
	private String event;
	private Boolean hasCoPresenters;
	private Boolean needsProjector;
	private Boolean needsTools;

	public TalkData(String title, String date) {
		super();
		String currentTimeZone = TimeZone.getDefault().getID();

		this.title = title;
		this.date = new DateTime(date, DateTimeZone.forID(currentTimeZone));
	}

	public TalkData(String title) {
		super();

		this.title = title;
	}

	public TalkData() {
		super();
		this.title = "";
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public DateTime getDate() {
		return this.date;
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
	public String getDate(String pattern) {
		String dateString = "";

		if (this.date != null) {
			DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
			dateString = dtf.print(this.date);
		}

		return dateString;
	}

	public void setDate(DateTime date) {
		this.date = date;
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
	public void setDate(String pattern, String date) {
		if (date.length() > 0 && pattern.length() > 0) {
			String currentTimeZone = TimeZone.getDefault().getID();
			DateTimeFormatter dtf = DateTimeFormat.forPattern(pattern);
			this.date = dtf.parseDateTime(date);
			this.date.withZone(DateTimeZone.forID(currentTimeZone));
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public Boolean getHasCoPresenters() {
		return hasCoPresenters;
	}

	public void setHasCoPresenters(Boolean hasCoPresenters) {
		this.hasCoPresenters = hasCoPresenters;
	}

	public Boolean getNeedsProjector() {
		return needsProjector;
	}

	public void setNeedsProjector(Boolean needsProjector) {
		this.needsProjector = needsProjector;
	}

	public Boolean getNeedsTools() {
		return needsTools;
	}

	public void setNeedsTools(Boolean needsTools) {
		this.needsTools = needsTools;
	}

}
