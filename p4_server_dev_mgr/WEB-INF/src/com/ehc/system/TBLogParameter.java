package com.ehc.system;

import org.json.JSONObject;

public class TBLogParameter {

	private TBLogParameter() {}
	private String uName = "";
	private int uid = 0;
	private String pName = "";
	private int pid = 0;
	private int sid = 0;
	private String sName = "";
	private String pwd = "";
	private String oldPwd = "";
	private String newPwd = "";
	private String adminName="";
	private String note="";
	private JSONObject obj = new JSONObject();
	public String getAdminName() {
		return adminName;
	}
	public void setNote(String note) {
		this.note = note;
		obj.put("note", this.note);
	}
	public void setAdminName(String adminName) {
		this.adminName = adminName;
		obj.put("adminName", this.adminName);
	}

	

	public String getuName() {
		return uName;
	}

	public void setuName(String uName) {
		this.uName = uName;
		obj.put("uname", this.uName);
		
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
		obj.put("uid", this.uid);
	}

	public String getpName() {
		return pName;
	}

	public void setpName(String pName) {
		this.pName = pName;
		this.obj.put("pname", this.pName);
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
		obj.put("pid", this.pid);
	}

	public int getSid() {
		return sid;
	}

	public void setSid(int sid) {
		this.sid = sid;
		obj.put("sid", this.sid);
	}

	public String getsName() {
		return sName;
	}

	public void setsName(String sName) {
		this.sName = sName;
		obj.put("sname", this.sName);
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
		obj.put("pwd", this.pwd);
	}

	public String getOldPwd() {
		return oldPwd;
	}

	public void setOldPwd(String oldPwd) {
		this.oldPwd = oldPwd;
		obj.put("oldpwd", this.oldPwd);
	}

	public String getNewPwd() {
		return newPwd;
	}

	public void setNewPwd(String newPwd) {
		this.newPwd = newPwd;
		obj.put("newpwd", this.newPwd);
	}

	public static TBLogParameter getInstance() {
		return new TBLogParameter();
	}
	
	public void addParameter(String key,Object value){
		obj.put(key, value);
	}

	public String toString() {
		return obj.toString();
	}
}
