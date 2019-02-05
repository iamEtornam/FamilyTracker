package co.etornam.familytracker.model;

public class Profile {
	private String firstName;
	private String otherName;
	private String dateOfBirth;
	private String homeAddress;
	private String workAddress;
	private String mobileNumber;
	private String profileImgUrl;
	private int gender;
	private Object timestamp;

	public Profile() {
	}

	public Profile(String firstName, String otherName, String dateOfBirth, String homeAddress, String workAddress, String mobileNumber, String profileImgUrl, int gender, Object timestamp) {
		this.firstName = firstName;
		this.otherName = otherName;
		this.dateOfBirth = dateOfBirth;
		this.homeAddress = homeAddress;
		this.workAddress = workAddress;
		this.mobileNumber = mobileNumber;
		this.profileImgUrl = profileImgUrl;
		this.gender = gender;
		this.timestamp = timestamp;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getOtherName() {
		return otherName;
	}

	public void setOtherName(String otherName) {
		this.otherName = otherName;
	}

	public String getDateOfBirth() {
		return dateOfBirth;
	}

	public void setDateOfBirth(String dateOfBirth) {
		this.dateOfBirth = dateOfBirth;
	}

	public String getHomeAddress() {
		return homeAddress;
	}

	public void setHomeAddress(String homeAddress) {
		this.homeAddress = homeAddress;
	}

	public String getWorkAddress() {
		return workAddress;
	}

	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getProfileImgUrl() {
		return profileImgUrl;
	}

	public void setProfileImgUrl(String profileImgUrl) {
		this.profileImgUrl = profileImgUrl;
	}

	public int getGender() {
		return gender;
	}

	public void setGender(int gender) {
		this.gender = gender;
	}

	public Object getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Object timestamp) {
		this.timestamp = timestamp;
	}
}