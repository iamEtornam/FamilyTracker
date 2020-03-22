package co.etornam.familytracker.model;

public class Health {
	private String bloodgroup;
	private String diabetic;
	private String medication;
	private String medinfo;
	private String allergy;
	private String allergyinfo;
	private String bloodpressure;
	private String bleeder;
	private String donor;
	private String doctorname;
	private String doctornumber;
	private String companyname;
	private String insurancenumber;

	public Health() {
	}

	public Health(String bloodgroup, String diabetic, String medication, String medinfo, String allergy, String allergyinfo, String bloodpressure, String bleeder, String donor, String doctorname, String doctornumber, String companyname, String insurancenumber) {
		this.bloodgroup = bloodgroup;
		this.diabetic = diabetic;
		this.medication = medication;
		this.medinfo = medinfo;
		this.allergy = allergy;
		this.allergyinfo = allergyinfo;
		this.bloodpressure = bloodpressure;
		this.bleeder = bleeder;
		this.donor = donor;
		this.doctorname = doctorname;
		this.doctornumber = doctornumber;
		this.companyname = companyname;
		this.insurancenumber = insurancenumber;
	}

	public String getBloodgroup() {
		return bloodgroup;
	}

	public void setBloodgroup(String bloodgroup) {
		this.bloodgroup = bloodgroup;
	}

	public String getDiabetic() {
		return diabetic;
	}

	public void setDiabetic(String diabetic) {
		this.diabetic = diabetic;
	}

	public String getMedication() {
		return medication;
	}

	public void setMedication(String medication) {
		this.medication = medication;
	}

	public String getMedinfo() {
		return medinfo;
	}

	public void setMedinfo(String medinfo) {
		this.medinfo = medinfo;
	}

	public String getAllergy() {
		return allergy;
	}

	public void setAllergy(String allergy) {
		this.allergy = allergy;
	}

	public String getAllergyinfo() {
		return allergyinfo;
	}

	public void setAllergyinfo(String allergyinfo) {
		this.allergyinfo = allergyinfo;
	}

	public String getBloodpressure() {
		return bloodpressure;
	}

	public void setBloodpressure(String bloodpressure) {
		this.bloodpressure = bloodpressure;
	}

	public String getBleeder() {
		return bleeder;
	}

	public void setBleeder(String bleeder) {
		this.bleeder = bleeder;
	}

	public String getDonor() {
		return donor;
	}

	public void setDonor(String donor) {
		this.donor = donor;
	}

	public String getDoctorname() {
		return doctorname;
	}

	public void setDoctorname(String doctorname) {
		this.doctorname = doctorname;
	}

	public String getDoctornumber() {
		return doctornumber;
	}

	public void setDoctornumber(String doctornumber) {
		this.doctornumber = doctornumber;
	}

	public String getCompanyname() {
		return companyname;
	}

	public void setCompanyname(String companyname) {
		this.companyname = companyname;
	}

	public String getInsurancenumber() {
		return insurancenumber;
	}

	public void setInsurancenumber(String insurancenumber) {
		this.insurancenumber = insurancenumber;
	}
}
