package foo.fruitfox.data;

public class UserData {
	private String email;
	private String phone;
	private String verificationCode;
	private String GUID;

	public UserData(String email, String phone, String verificationCode,
			String GUID) {
		super();
		this.email = email;
		this.phone = phone;
		this.verificationCode = verificationCode;
		this.GUID = GUID;
	}

	public String getGUID() {
		return GUID;
	}

	public void setGUID(String GUID) {
		this.GUID = GUID;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVerificationCode() {
		return verificationCode;
	}

	public void setVerificationCode(String verificationCode) {
		this.verificationCode = verificationCode;
	}
}
