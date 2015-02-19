package foo.fruitfox.data;

public class SessionData {

	private String sessionName;
	private String editLink;
	private String viewLink;

	public SessionData(String sessionName, String editLink, String viewLink) {
		super();
		this.sessionName = sessionName;
		this.editLink = editLink;
		this.viewLink = viewLink;
	}

	public String getSessionName() {
		return sessionName;
	}

	public void setSessionName(String sessionName) {
		this.sessionName = sessionName;
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
