package com.tarunrs.FaradaysBook;

public class Note {
	String modified_at = "";
	String body = "";
	int  id ;
	int user_id ;
	String privacy = "";
	public Note(String body, String modified_at) {
		// TODO Auto-generated constructor stub
		this.body =body;
		this.modified_at = modified_at;
	}
	public Note(int id, int user_id, String body, String modified_at, String privacy) {
		// TODO Auto-generated constructor stub
		this.body = body ;
		this.modified_at = modified_at ;
		this.id = id ;
		this.user_id  = user_id  ;
		this.privacy = privacy;
	}
	public Note() {
		// TODO Auto-generated constructor stub
		this.body = "" ;
		this.modified_at = "";
		this.id =0;
		this.user_id  =0;
		this.privacy = "private";
	}
}
