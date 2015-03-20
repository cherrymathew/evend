package foo.fruitfox.data;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class TalkData {

	private String title;
	private String description;
	private DateTime date;
	private String type;
	private String duration;
	private String location;
	private String editLink;
	private String viewLink;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DateTime getDate() {
		return this.date;
	}

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

	public void setDate(String date) {
		String currentTimeZone = TimeZone.getDefault().getID();
		this.date = new DateTime(date, DateTimeZone.forID(currentTimeZone));
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

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getEditLink() {
		return editLink;
	}

	public void setEditLink(String editLink) {
		this.editLink = editLink;
	}

	public String getViewLink() {
		return viewLink;
	}

	public void setViewLink(String viewLink) {
		this.viewLink = viewLink;
	}
}
