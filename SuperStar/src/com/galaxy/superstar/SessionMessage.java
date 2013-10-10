package com.galaxy.superstar;

import java.io.Serializable;

public class SessionMessage implements Serializable{
	private static final long serialVersionUID = 1L;
	String user_id;
	String username;
	String realname;
	String small_imgpath;
	String my_imgpath;
	SingleMessage[] message;
}