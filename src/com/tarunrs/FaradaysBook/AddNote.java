package com.tarunrs.FaradaysBook;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class AddNote extends Activity implements OnClickListener{
	private DataHelper dh;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dh = new DataHelper(this);
        setContentView(R.layout.add_note);
        Button newNoteBtn = (Button) findViewById(R.id.addNoteBtn);
        newNoteBtn.setOnClickListener(this);
        
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.addNoteBtn:
				EditText etNote = (EditText) findViewById(R.id.newNoteContent);      
		        CheckBox cbIsPrivate = (CheckBox) findViewById(R.id.isPrivate);
		        String body = etNote.getText().toString();
		        String privacy = "private";
		        if(cbIsPrivate.isChecked() == false)
		        	privacy = "public";
		        Log.d("Note", body);
		        Log.d("privacy", privacy);
		        postNote(body, privacy);
				break;
			default: break;
			
		}
	}
	
	public void postNote(String body, String privacy){
         
			Note note = new Note();
			note.body = body;
			note.privacy = privacy;
			this.dh.insertToCache(note);
			List <Note> lnote = this.dh.selectAllCache();
			Log.d("in post note from cache db",lnote.toString());
			
			/*WebService webService = new WebService("http://www.faradaysbook.com/api/1.0/note/new");
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("auth_email", "tarunrs@gmail.com"));
            params.add(new BasicNameValuePair("auth_password", "ed4e7298b1f3f1a3e233e7385e788d85"));
            params.add(new BasicNameValuePair("privacy", privacy));
            params.add(new BasicNameValuePair("body", body));

            String response = null;
            response = webService.webInvoke("", params);
            
            Log.d("RESPONS123E",response);
		*/
	}
	
	public static final String md5(final String s) {
		    try {
		        // Create MD5 Hash
		        MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
		        digest.update(s.getBytes());
		        byte messageDigest[] = digest.digest();
		        // Create Hex String
		        StringBuffer hexString = new StringBuffer();
		        for (int i = 0; i < messageDigest.length; i++) {
		            String h = Integer.toHexString(0xFF & messageDigest[i]);
		            while (h.length() < 2)
		                h = "0" + h;
		            hexString.append(h);
		        }
		        return hexString.toString();
		    } catch (NoSuchAlgorithmException e) {
		        e.printStackTrace();
		    }
		    return "";
		}	

}
