package com.maven.common.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUser<M extends BaseUser<M>> extends Model<M> implements IBean {

	public void setId(java.lang.Integer id) {
		set("id", id);
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public void setAccount(java.lang.String account) {
		set("account", account);
	}
	
	public java.lang.String getAccount() {
		return getStr("account");
	}

	public void setPassword(java.lang.String password) {
		set("password", password);
	}
	
	public java.lang.String getPassword() {
		return getStr("password");
	}

	public void setName(java.lang.String name) {
		set("name", name);
	}
	
	public java.lang.String getName() {
		return getStr("name");
	}

	public void setSex(java.lang.Integer sex) {
		set("sex", sex);
	}
	
	public java.lang.Integer getSex() {
		return getInt("sex");
	}

	public void setUserType(java.lang.Integer userType) {
		set("user_type", userType);
	}
	
	public java.lang.Integer getUserType() {
		return getInt("user_type");
	}

	public void setRelation(java.lang.Integer relation) {
		set("relation", relation);
	}
	
	public java.lang.Integer getRelation() {
		return getInt("relation");
	}

}