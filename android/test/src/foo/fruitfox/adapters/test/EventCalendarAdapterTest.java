package foo.fruitfox.adapters.test;

import org.joda.time.DateTime;
import org.joda.time.Days;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.test.AndroidTestCase;
import android.view.View;
import android.widget.TextView;
import foo.fruitfox.adapters.EventCalendarAdapter;
import foo.fruitfox.evend.R;

public class EventCalendarAdapterTest extends AndroidTestCase {

	EventCalendarAdapter eca;
	Context actualContext;
	String startDate = "2015-05-23";
	String endDate = "2015-06-07";
	Boolean[] eventDays;

	public EventCalendarAdapterTest() {

	}

	protected void setUp() throws Exception {
		actualContext = getContext();
		int daysCount = Days.daysBetween(new DateTime(startDate).toLocalDate(),
				new DateTime(endDate).toLocalDate()).getDays() + 1;
		eventDays = new Boolean[daysCount];

		// We make even days true and odd days false
		for (int i = 0; i < eventDays.length; i++) {
			if (i % 2 == 0) {
				eventDays[i] = true;
			} else {
				eventDays[i] = false;
			}
		}

		eca = new EventCalendarAdapter(actualContext, startDate, endDate,
				eventDays);

	}

	public void testEventCalendarAdapterGetCount() {
		int totalCount = 16;

		assertEquals("count value did not match", totalCount, eca.getCount());
	}

	public void testEventCalendarAdapterGetItem() {
		DateTime expectedDate = new DateTime("2015-05-28");
		DateTime actualDate = eca.getItem(5);

		assertEquals("date value did not match",
				expectedDate.toString("yyyy-MM-dd"),
				actualDate.toString("yyyy-MM-dd"));
	}

	public void testEventCalendarAdapterGetItemId() {
		int expectedPosition = 5;

		assertEquals("position value did not match", expectedPosition,
				eca.getItemId(5));
	}

	public void testEventCalendarAdapterGetView() {
		View viewEven = eca.getView(0, null, null);
		View viewOdd = eca.getView(3, null, null);

		TextView dayTextEven = (TextView) viewEven.findViewById(R.id.dayText);
		TextView dayTextOdd = (TextView) viewOdd.findViewById(R.id.dayText);

		assertNotNull("dayTextEven is null", dayTextEven);
		assertNotNull("dayTextOdd is null", dayTextOdd);

		String dayTextEvenValue = dayTextEven.getText().toString();
		String dayTextOddValue = dayTextOdd.getText().toString();

		int dayTextEvenColor = ((ColorDrawable) dayTextEven.getBackground())
				.getColor();
		int dayTextOddColor = ((ColorDrawable) dayTextOdd.getBackground())
				.getColor();

		assertEquals("day (odd) value did not match", "23", dayTextEvenValue);
		assertEquals("day (even) value did not match", "26", dayTextOddValue);
		assertEquals("background color (odd) value did not match",
				Color.TRANSPARENT, dayTextOddColor);
		assertEquals("background color (even) value did not match",
				Color.GREEN, dayTextEvenColor);

	}
}
