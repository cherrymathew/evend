package foo.fruitfox.adapters;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import foo.fruitfox.data.UserData;
import foo.fruitfox.evend.R;
import foo.fruitfox.helpers.StorageHelper;

public class EventCalendarAdapter extends BaseAdapter {

	private Context context;
	private DateTime startDate;
	private DateTime endDate;

	static class ViewHolder {
		public TextView dayText;
	}

	public EventCalendarAdapter(Context context, String startDate,
			String endDate) {
		super();
		String currentTimeZone = TimeZone.getDefault().getID();

		this.context = context;
		this.startDate = new DateTime(startDate,
				DateTimeZone.forID(currentTimeZone));
		this.endDate = new DateTime(endDate,
				DateTimeZone.forID(currentTimeZone));
	}

	@Override
	public int getCount() {
		int totalDays = Days.daysBetween(startDate.toLocalDate(),
				endDate.toLocalDate()).getDays() + 1;
		return totalDays;
	}

	@Override
	public Object getItem(int position) {
		return startDate.plus(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View dayView = convertView;
		String identifier = StorageHelper.PreferencesHelper
				.getIdentifier(this.context);
		UserData userData = StorageHelper.PreferencesHelper.getUserData(
				this.context, identifier);
		Boolean[] eventDays = userData.getEventDaysAttending();

		if (dayView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			dayView = inflater.inflate(R.layout.calendar_single_day, parent,
					false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.dayText = (TextView) dayView.findViewById(R.id.dayText);
			viewHolder.dayText.setText(Integer.toString(startDate.plusDays(
					position).getDayOfMonth()));

			if (eventDays[position] == true) {
				viewHolder.dayText.setBackgroundColor(Color.GREEN);
			} else {
				viewHolder.dayText.setBackgroundColor(Color.TRANSPARENT);
			}

			dayView.setTag(viewHolder);

		} else {
			ViewHolder viewHolder = (ViewHolder) dayView.getTag();
			viewHolder.dayText.setText(Integer.toString(startDate.plusDays(
					position).getDayOfMonth()));

			if (eventDays[position] == true) {
				viewHolder.dayText.setBackgroundColor(Color.GREEN);
			} else {
				viewHolder.dayText.setBackgroundColor(Color.TRANSPARENT);
			}
		}

		return dayView;
	}
}
