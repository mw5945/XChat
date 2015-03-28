package com.wlm.db;

public class TopicInfo {
	String TID;
	String Name;
	String Pic;
	String Description;
	int Population;
	int Status;//0 弃用 1 主菜单 2 已有备选 3 新增备选
	
	public TopicInfo(){
		
	}
	
	public String getTID() {
		return TID;
	}

	public void setTID(String tID) {
		TID = tID;
	}

	public String getName() {
		return Name;
	}

	public void setName(String name) {
		Name = name;
	}

	public String getPic() {
		return Pic;
	}

	public void setPic(String pic) {
		Pic = pic;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public int getPopulation() {
		return Population;
	}

	public void setPopulation(int population) {
		Population = population;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int status) {
		Status = status;
	}

	public TopicInfo(String tID, String name, String pic, String description,
			int population, int status) {
		super();
		TID = tID;
		Name = name;
		Pic = pic;
		Description = description;
		Population = population;
		Status = status;
	}
}