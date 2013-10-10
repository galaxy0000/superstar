package com.galaxy.superstar;

import java.io.Serializable;

public class NearbyBuddyInfo implements Serializable{
	private static final long serialVersionUID = 1L;
	String id;
	String name;
	String skill_describe;
	String role_type;
	String pre_achieve;
	String small_imgpath;
	int distance;
}