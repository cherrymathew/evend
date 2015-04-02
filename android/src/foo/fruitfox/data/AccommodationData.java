package foo.fruitfox.data;

public class AccommodationData {
	private String accommodationType;
	private String bedsCount;

	public AccommodationData() {
		super();
		this.accommodationType = "";
		this.bedsCount = "";
	}

	public AccommodationData(String accommodationType, String bedsCount) {
		super();
		this.accommodationType = accommodationType;
		this.bedsCount = bedsCount;
	}

	public String getAccommodationType() {
		return accommodationType;
	}

	public void setAccommodationType(String accommodationType) {
		this.accommodationType = accommodationType;
	}

	public String getBedsCount() {
		return bedsCount;
	}

	public void setBedsCount(String bedsCount) {
		this.bedsCount = bedsCount;
	}

}
