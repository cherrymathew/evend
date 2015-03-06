package foo.fruitfox.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import foo.fruitfox.data.TalkData;
import foo.fruitfox.evend.R;

public class TalksAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<TalkData> talks;

	public static class ViewHolder {
		public TextView sessionName;
		public String editLink;
		public String viewLink;
	}

	public TalksAdapter(Context context, ArrayList<TalkData> talks) {
		super();
		this.context = context;
		this.talks = talks;
	}

	@Override
	public int getCount() {
		return talks.size();
	}

	@Override
	public Object getItem(int position) {
		return talks.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View sessionView = convertView;

		if (sessionView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			sessionView = inflater.inflate(R.layout.talk_single_row, parent,
					false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.sessionName = (TextView) sessionView
					.findViewById(R.id.talkText);
			viewHolder.sessionName.setText(talks.get(position).getTitle());
			viewHolder.editLink = talks.get(position).getEditLink();
			viewHolder.viewLink = talks.get(position).getViewLink();
			sessionView.setTag(viewHolder);
		} else {
			ViewHolder viewHolder = (ViewHolder) sessionView.getTag();
			viewHolder.sessionName.setText(talks.get(position).getTitle());
			viewHolder.editLink = talks.get(position).getEditLink();
			viewHolder.viewLink = talks.get(position).getViewLink();
		}

		return sessionView;
	}

}
