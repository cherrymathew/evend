package foo.fruitfox.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import foo.fruitfox.data.TalkData;
import foo.fruitfox.evend.R;

public class TalksAdapter extends BaseAdapter {

	private Context context;
	private List<TalkData> talkList;

	public static class ViewHolder {
		public TextView talkTitle;
		public TextView talkDate;
		public Button removeTalk;
	}

	public TalksAdapter(Context context, List<TalkData> talkList) {
		super();
		this.context = context;
		this.talkList = talkList;
	}

	@Override
	public int getCount() {
		return talkList.size();
	}

	@Override
	public Object getItem(int position) {
		return talkList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View talkDataRow = convertView;

		if (talkDataRow == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			talkDataRow = inflater.inflate(R.layout.talk_single_row, parent,
					false);

			ViewHolder viewHolder = new ViewHolder();

			viewHolder.talkTitle = (TextView) talkDataRow
					.findViewById(R.id.talkTitle);
			viewHolder.talkDate = (TextView) talkDataRow
					.findViewById(R.id.talkDate);
			viewHolder.removeTalk = (Button) talkDataRow
					.findViewById(R.id.removetTalk);

			viewHolder.talkTitle.setText(talkList.get(position).getTitle());

			viewHolder.talkDate.setText(talkList.get(position).getDate(
					"dd-MM-yyyy"));
			// viewHolder.talkDate
			// .setOnFocusChangeListener((OnFocusChangeListener) context);
			// viewHolder.talkDate.setOnClickListener((OnClickListener)
			// context);

			talkDataRow.setTag(viewHolder);
		} else {
			ViewHolder viewHolder = (ViewHolder) talkDataRow.getTag();

			viewHolder.talkTitle.setText(talkList.get(position).getTitle());
			// viewHolder.talkTitle
			// .setOnFocusChangeListener((OnFocusChangeListener) context);
			// viewHolder.talkTitle.setOnClickListener((OnClickListener)
			// context);

			viewHolder.talkDate.setText(talkList.get(position).getDate(
					"dd-MM-yyyy"));
			// viewHolder.talkDate
			// .setOnFocusChangeListener((OnFocusChangeListener) context);
			// viewHolder.talkDate.setOnClickListener((OnClickListener)
			// context);
		}

		return talkDataRow;
	}
}
