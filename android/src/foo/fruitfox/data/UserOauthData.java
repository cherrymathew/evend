package foo.fruitfox.data;

public class UserOauthData {
	private String oauthProvider;
	private String oauthAccessToken;
	private String oauthUserId;

	public UserOauthData(String oauthProvider, String oauthAccessToken,
			String oauthUserId) {
		super();
		this.oauthProvider = oauthProvider;
		this.oauthAccessToken = oauthAccessToken;
		this.oauthUserId = oauthUserId;
	}

	public String getOauthProvider() {
		return oauthProvider;
	}

	public void setOauthProvider(String oauthProvider) {
		this.oauthProvider = oauthProvider;
	}

	public String getOauthAccessToken() {
		return oauthAccessToken;
	}

	public void setOauthAccessToken(String oauthAccessToken) {
		this.oauthAccessToken = oauthAccessToken;
	}

	public String getOauthUserId() {
		return oauthUserId;
	}

	public void setOauthUserId(String oauthUserId) {
		this.oauthUserId = oauthUserId;
	}

}