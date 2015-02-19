package foo.fruitfox.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import foo.fruitfox.data.SessionData;
import foo.fruitfox.evend.R;

public class SessionsAdapter extends BaseAdapter {

	private Context context;
	private ArrayList<SessionData> sessions;

	public static class ViewHolder {
		public TextView sessionName;
		public String editLink;
		public String viewLink;
	}

	public SessionsAdapter(Context context, ArrayList<SessionData> sessions) {
		super();
		this.context = context;
		this.sessions = sessions;
	}

	@Override
	public int getCount() {
		return sessions.size();
	}

	@Override
	public Object getItem(int position) {
		return sessions.get(position);
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
			sessionView = inflater.inflate(R.layout.session_single_row, parent,
					false);

			ViewHolder viewHolder = new ViewHolder();
			viewHolder.sessionName = (TextView) sessionView
					.findViewById(R.id.sessionText);
			viewHolder.sessionName.setText(sessions.get(position)
					.getSessionName());
			viewHolder.editLink = sessions.get(position).getEditLink();
			viewHolder.viewLink = sessions.get(position).getViewLink();
			sessionView.setTag(viewHolder);
		} else {
			ViewHolder viewHolder = (ViewHolder) sessionView.getTag();
			viewHolder.sessionName.setText(sessions.get(position)
					.getSessionName());
			viewHolder.editLink = sessions.get(position).getEditLink();
			viewHolder.viewLink = sessions.get(position).getViewLink();
		}

		return sessionView;
	}

}
