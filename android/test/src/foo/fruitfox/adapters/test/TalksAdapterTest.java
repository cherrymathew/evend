package foo.fruitfox.adapters.test;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.test.AndroidTestCase;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import foo.fruitfox.adapters.TalksAdapter;
import foo.fruitfox.data.TalkData;
import foo.fruitfox.evend.R;

public class TalksAdapterTest extends AndroidTestCase {

	List<TalkData> talkDataList;
	Context actualContext;
	TalksAdapter ta;

	public TalksAdapterTest() {

	}

	protected void setUp() throws Exception {
		super.setUp();
		actualContext = getContext();
		talkDataList = new ArrayList<TalkData>();

		for (int i = 0; i < 2; i++) {
			TalkData talkData = new TalkData("Title " + (i + 1), "2015-05-"
					+ (i + 1));
			talkDataList.add(talkData);
		}

		ta = new TalksAdapter(actualContext, talkDataList);
	}

	public void testTalksAdapterGetCount() {
		int totalCount = 2;

		assertEquals("count value did not match", totalCount, ta.getCount());
	}

	public void testTalksAdapterGetItem() {
		TalkData expectedTalkData = talkDataList.get(0);
		TalkData actualTalkData = ta.getItem(0);

		assertEquals("data value did not match",
				expectedTalkData.getDate("yyyy-MM-dd"),
				actualTalkData.getDate("yyyy-MM-dd"));
		assertEquals("title value did not match", expectedTalkData.getTitle(),
				actualTalkData.getTitle());
	}

	public void testTalksAdapterGetItemId() {
		int expectedPosition = 1;

		assertEquals("position value did not match", expectedPosition,
				ta.getItemId(1));
	}

	public void testTalksAdapterGetView() {
		View view = ta.getView(1, null, null);
		TalkData talkData = talkDataList.get(1);

		TextView talkTitle = (TextView) view.findViewById(R.id.talkTitle);
		TextView talkDate = (TextView) view.findViewById(R.id.talkDate);
		Button removeTalk = (Button) view.findViewById(R.id.removeTalk);

		assertNotNull("talkTitle is null", talkTitle);
		assertNotNull("talkDate is null", talkDate);
		assertNotNull("removeTalk is null", removeTalk);

		assertEquals("talkTitle value did not match", talkData.getTitle(),
				talkTitle.getText().toString());
		assertEquals("talkDate value did not match",
				talkData.getDate("dd-MM-yyyy"), talkDate.getText().toString());
	}
}
