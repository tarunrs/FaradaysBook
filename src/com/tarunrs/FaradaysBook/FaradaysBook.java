package com.tarunrs.FaradaysBook;


import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TabHost;

public class FaradaysBook extends TabActivity implements OnClickListener {
	
	private DataHelper dh;
	NotesAdapter n_adapter;
	NotesAdapter t_adapter;
	List<Note> notes=null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        this.dh = new DataHelper(this);
        this.dh.deleteAll();
      /*this.dh = new DataHelper(this);
        this.dh.deleteAll();
        List<Note> names = this.dh.selectAll();
        StringBuilder sb = new StringBuilder();
        sb.append("Names in database:\n");
        Note test = null;
        for (Note name : names) {
          sb.append(name.content + "\n");
          test= name;
        }
        n_adapter = new NotesAdapter(this, names);
        setListAdapter(n_adapter);
        n_adapter.add(test);
        n_adapter.notifyDataSetChanged();*/
        deleteAuthNotes();
        notes = this.dh.selectAllCache();
        notes = this.dh.selectAllAuth();
        Log.d("cache", notes.toString());
        ListView lvn = null, lvt =null;
        Resources res = getResources(); // Resource object to get Drawables
        TabHost tabHost = getTabHost();  // The activity TabHost
        TabHost.TabSpec spec;  // Resusable TabSpec for each tab   
        Button butn = null;
        // Create an Intent to launch an Activity for the tab (to be reused)
         // Initialize a TabSpec for each tab and add it to the TabHost
        lvn = (ListView)findViewById(R.id.notesList);
        lvt = (ListView)findViewById(R.id.tagsList);

        n_adapter = new NotesAdapter(this, notes);
        t_adapter = new NotesAdapter(this, notes);
        lvn.setAdapter(n_adapter);
        lvt.setAdapter(t_adapter);
        
        spec = tabHost.newTabSpec("notes").setIndicator("Notes",
                          res.getDrawable(R.drawable.ic_tab_notes))
                      .setContent(R.id.notesList);
        tabHost.addTab(spec);
        // Do the same for the other tabs
        spec = tabHost.newTabSpec("tags").setIndicator("Tags",
                          res.getDrawable(R.drawable.ic_tab_tags))
                      .setContent(R.id.tagsList);
        tabHost.addTab(spec);
        tabHost.setCurrentTab(0);
        
        butn = (Button) findViewById(R.id.createNewNote);
        butn.setOnClickListener(this);
        butn = (Button) findViewById(R.id.syncWithWeb);
        butn.setOnClickListener(this);
        //getNotes();
        //n_adapter.add(getNotes());
        //n_adapter.notifyDataSetChanged();
    }
	public void onClick(View v) {
		Log.d("asd","asd2ad");
		// TODO Auto-generated method stub
		switch(v.getId())
		{
			case R.id.createNewNote:
				Intent i = new Intent(this, AddNote.class);
				startActivity(i);
				break;
					
			case R.id.syncWithWeb:
				Log.d("syn","syncing");
				syncWithWeb();
				break;
			default : break;
		
		}
		
	}
	
	public void syncWithWeb(){
		syncWebWithCache();
		syncAuthWithWeb();	
	}
	
	
	public void syncAuthWithWeb(){
		List<Note> notesFromWeb = getAllNotesFromWeb();
		List<Note> notesFromAuth = null;
		if(notesFromWeb != null && notesFromWeb.size() > 0 ){
			this.dh.deleteAllAuth();
			 for(int i=0; i<notesFromWeb.size(); i++)
		        {
		        	Log.d("db", "Inserting a note");
		            this.dh.insertToAuth(notesFromWeb.get(i));
		            Log.d("db", "Insert complete");
		        }
		}
		n_adapter.clear();
		notesFromAuth = this.dh.selectAllAuth();
		Log.d("db", "Selected");
		for(int i =0; i<notesFromAuth.size(); i++)
			n_adapter.add(notesFromAuth.get(i));
		Log.d("db", "Notifying");
		n_adapter.notifyDataSetChanged();
	}
	
	public void syncWebWithCache(){
		List<Note> notesFromCache= this.dh.selectAllCache();

		if(notesFromCache != null && notesFromCache.size() > 0){
			 for(int i=0; i<notesFromCache.size(); i++)
		        {
		        	Log.d("db", "Posting a note");
		            postNoteToWeb(notesFromCache.get(i));
		            Log.d("db", "Posting complete");
		        }
			 Log.d("db", "Synced All to web");
		}
		Log.d("db", "Sync with web complete");
	}
	
	public void postNoteToWeb(Note note){
        
		WebService webService = new WebService("http://www.faradaysbook.com/api/1.0/note/new");
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("auth_email", "tarunrs@gmail.com"));
        params.add(new BasicNameValuePair("auth_password", "ed4e7298b1f3f1a3e233e7385e788d85"));
        params.add(new BasicNameValuePair("privacy", note.privacy));
        params.add(new BasicNameValuePair("body", note.body));

        String response = null;
        response = webService.webInvoke("", params);
        
        Log.d("Response After Post",response);
	
}
	
	
	
	public void deleteAuthNotes(){
		this.dh.deleteAllAuth();		
	}
	
	public List<Note> getAllNotesFromWeb(){
		  WebService webService = new WebService("http://www.faradaysbook.com/api/1.0/notes");
		  Map<String, String> params = new HashMap<String, String>();

		  params.put("auth_email", "tarunrs@gmail.com");
		  params.put("auth_password", md5("tarun123"));
		  String response = webService.webGet("", params);
		  Log.d("resposse", response);
 
		  try{
	             //Parse Response into our objec
			  	// response ="{[{'body':'my first note','modified_at':'2010/09/06 16:29:10 +0000','id':6,'user_id':2,'privacy':null}]}";
			    // alert = new Gson().fromJson(response, Note.class);
	             Type collectionType = new TypeToken<List<Note>>(){}.getType();
	             List<Note> retNoteList = new Gson().fromJson(response, collectionType);
	             return retNoteList;
	      }
         catch(Exception e)
         {
             Log.d("Error: ", e.getMessage());
             return null;
         }
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