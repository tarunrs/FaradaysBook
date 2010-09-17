package com.tarunrs.FaradaysBook;

import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NotesAdapter extends BaseAdapter {
	
	private List<Note> notes;
	protected Context m_context;
	protected LayoutInflater m_inflater;
	
	public NotesAdapter (final Context context, List<Note> notes){
		this.notes = notes;
		m_context = context;
		m_inflater = LayoutInflater.from(m_context);
	}
		
	public int getCount() {
		// TODO Auto-generated method stu	
		return notes.size();
	}

	public Note getItem(int position) {
		// TODO Auto-generated method stub
		return notes.get(position);
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		
		  if (convertView == null) {
			  convertView = m_inflater.inflate(R.layout.list_item, null);
			  holder = new ViewHolder();
	  		  holder.content = (TextView) convertView.findViewById(R.id.content);
	  		  holder.date = (TextView) convertView.findViewById(R.id.date);
	  		  convertView.setTag(holder);
		  } else {
			  holder = (ViewHolder) convertView.getTag();
		  }
		  
		  holder.content.setText(notes.get(position).body);
		  holder.date.setText(notes.get(position).modified_at);
		  return convertView;
	}
	
	static class ViewHolder {
		 TextView content;
		 TextView date;
		 }

	public void add(Note test) {
		// TODO Auto-generated method stub
		this.notes.add(test);
	}
	
	public void clear() {
		// TODO Auto-generated method stub
		this.notes.clear();
	}
}
