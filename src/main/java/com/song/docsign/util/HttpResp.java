package com.song.docsign.util;

public class HttpResp {

	private int status;
	
	private String body;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}
	
	public String toString(){
		return "status="+status+",body="+body;
	}
}
