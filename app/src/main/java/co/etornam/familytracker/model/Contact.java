package co.etornam.familytracker.model;

public class Contact {

	private String imageUrl;
	private String name;
	private String number;
	private String relation;
	private Object timestamp;

	public Contact() {
	}

	public Contact(String imageUrl, String name, String number, String relation, Object timestamp) {
		this.imageUrl = imageUrl;
		this.name = name;
		this.number = number;
		this.relation = relation;
		this.timestamp = timestamp;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getRelation() {
		return relation;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public Object getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Object timestamp) {
		this.timestamp = timestamp;
	}
}
