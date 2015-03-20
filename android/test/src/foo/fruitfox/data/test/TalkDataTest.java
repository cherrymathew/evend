package foo.fruitfox.data.test;

import junit.framework.TestCase;

import org.joda.time.DateTime;

import foo.fruitfox.data.TalkData;

public class TalkDataTest extends TestCase {
	TalkData talkData;

	public TalkDataTest(String name) {
		super(name);
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.talkData = new TalkData("Title 1", "2015-01-01");
	}

	public void testTalkDataConstructor1() {
		TalkData testTalkData = new TalkData();

		assertEquals("title did not match", "", testTalkData.getTitle());
	}

	public void testTalkDataConstructor2() {
		TalkData testTalkData = new TalkData("Title");

		assertEquals("title did not match", "Title", testTalkData.getTitle());
	}

	public void testTalkDataConstructor3() {
		TalkData testTalkData = new TalkData("Title", "2015-01-01");

		assertEquals("title did not match", "Title", testTalkData.getTitle());
		assertEquals("date did not match", "2015-01-01",
				testTalkData.getDate("yyyy-MM-dd"));
	}

	public void testTalkDataMemberTile() {
		String title = "Example Title";

		talkData.setTitle(title);

		assertEquals("title did not match", title, talkData.getTitle());
	}

	public void testTalkDataMemberDescription() {
		String description = "Example Description";

		talkData.setDescription(description);

		assertEquals("description did not match", description,
				talkData.getDescription());
	}

	public void testTalkDataMemberType() {
		String type = "Example Type";

		talkData.setType(type);

		assertEquals("type did not match", type, talkData.getType());
	}

	public void testTalkDataMemberDuration() {
		String duration = "120 min";

		talkData.setDuration(duration);

		assertEquals("duration did not match", "120 min",
				talkData.getDuration());
	}

	public void testTalkDataMemberLocation() {
		String location = "Example location";

		talkData.setLocation(location);

		assertEquals("location did not match", "Example location",
				talkData.getLocation());
	}

	public void testTalkDataMemberEditLink() {
		String editLink = "http://www.google.com/";

		talkData.setEditLink(editLink);

		assertEquals("editLink did not much", editLink, talkData.getEditLink());
	}

	public void testTalkDataMemberViewLink() {
		String viewLink = "http://www.google.com";

		talkData.setViewLink(viewLink);

		assertEquals("viewLink did not match", viewLink, talkData.getViewLink());
	}

	public void testTalkDataMemberDate() {
		DateTime date = new DateTime("2015-01-01");

		talkData.setDate(date);

		assertEquals("date object did not match", date, talkData.getDate());
		assertEquals("date value did not match", date.toString("yyyy-MM-dd"),
				talkData.getDate().toString("yyyy-MM-dd"));
	}

	public void testTalkDataMethodGetDate() {
		DateTime date = new DateTime("2015-01-01");
		String dateString = "2015-01-01";
		String pattern = "yyyy-MM-dd";

		talkData.setDate(date);

		assertEquals("date value did not match", dateString,
				talkData.getDate(pattern));
	}

	public void testTalkDataMethodSetDate() {
		String date = "2015-01-01";
		String pattern = "yyyy-MM-dd";

		talkData.setDate(pattern, date);

		assertEquals("date value did not match", date, talkData.getDate()
				.toString(pattern));
	}
}
