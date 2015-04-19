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
		public TextView talkType;
		public TextView talkDuration;
		public TextView talkEvent;
		public TextView talkHasCoPresenters;
		public TextView talkNeedsProjector;
		public TextView talkNeedsTools;
		public TextView talkNotes;
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
	public TalkData getItem(int position) {
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
			viewHolder.talkType = (TextView) talkDataRow
					.findViewById(R.id.talkType);
			viewHolder.talkDuration = (TextView) talkDataRow
					.findViewById(R.id.talkDuration);
			viewHolder.talkEvent = (TextView) talkDataRow
					.findViewById(R.id.talkEvent);
			viewHolder.talkHasCoPresenters = (TextView) talkDataRow
					.findViewById(R.id.talkHasCoPresenters);
			viewHolder.talkNeedsProjector = (TextView) talkDataRow
					.findViewById(R.id.talkNeedsProjector);
			viewHolder.talkNeedsTools = (TextView) talkDataRow
					.findViewById(R.id.talkNeedsTools);
			viewHolder.talkNotes = (TextView) talkDataRow
					.findViewById(R.id.talkNotes);
			viewHolder.removeTalk = (Button) talkDataRow
					.findViewById(R.id.removeTalk);

			viewHolder.talkTitle.setText(talkList.get(position).getTitle());
			viewHolder.talkType.setText(talkList.get(position).getType());
			viewHolder.talkDuration.setText(talkList.get(position)
					.getDuration().length() > 0 ? talkList.get(position)
					.getDuration() : "N/A");
			viewHolder.talkEvent.setText(talkList.get(position).getEvent());
			viewHolder.talkHasCoPresenters.setText(talkList.get(position)
					.getHasCoPresenters() ? "Yes" : "No");
			viewHolder.talkNeedsProjector.setText(talkList.get(position)
					.getNeedsProjector() ? "Yes" : "No");
			viewHolder.talkNeedsTools.setText(talkList.get(position)
					.getNeedsTools() ? "Yes" : "No");
			viewHolder.talkNotes.setText(talkList.get(position).getNotes());

			talkDataRow.setTag(viewHolder);
		} else {
			ViewHolder viewHolder = (ViewHolder) talkDataRow.getTag();

			viewHolder.talkTitle.setText(talkList.get(position).getTitle());
			viewHolder.talkType.setText(talkList.get(position).getType());
			viewHolder.talkDuration.setText(talkList.get(position)
					.getDuration().length() > 0 ? talkList.get(position)
					.getDuration() : "N/A");
			viewHolder.talkEvent.setText(talkList.get(position).getEvent());
			viewHolder.talkHasCoPresenters.setText(talkList.get(position)
					.getHasCoPresenters() ? "Yes" : "No");
			viewHolder.talkNeedsProjector.setText(talkList.get(position)
					.getNeedsProjector() ? "Yes" : "No");
			viewHolder.talkNeedsTools.setText(talkList.get(position)
					.getNeedsTools() ? "Yes" : "No");
			viewHolder.talkNotes.setText(talkList.get(position).getNotes());
		}

		return talkDataRow;
	}
}
