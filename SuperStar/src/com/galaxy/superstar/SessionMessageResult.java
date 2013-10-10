package com.galaxy.superstar;

import java.io.Serializable;

public class SessionMessageResult implements Serializable{
	private static final long serialVersionUID = 1L;
	int status;
	String msg;
	SessionMessage data;
}