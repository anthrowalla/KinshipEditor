//  KinshipEditor
//
//  Created by Michael D. Fischer on 11/07/2006.
//  Copyright (c) 2006, Centre for Social Anthropology and Computing, 
//  University of Kent. All rights reserved.
//
//
//  Redistribution and use in source and binary forms, with or without
//  modification, are permitted provided that the following conditions
//  are met:
//
//  Redistributions of source code must retain the above copyright
//  notice, this list of conditions and the following disclaimer.
//  Redistributions in binary form must reproduce the above copyright
//  notice, this list of conditions and the following disclaimer in the
//  documentation and/or other materials provided with the distribution.
//  Neither the name of the Centre for Social Anthropology and Computing,
//  University of Kent nor the names of its contributors may be used 
//  to endorse or promote products derived from this software without
//  specific prior written permission.  THIS SOFTWARE IS PROVIDED BY THE 
//  COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
//  WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
//  OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
//  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS 
//  BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, 
//  OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT
//  OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
//  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
//  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
//  NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
//  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
//  

import java.util.*;
import java.lang.*;

public class ListVector extends Vector implements ToXML {
	int index=-1;
	Hashtable properties=null;
	String tagName=null;
	
	public String getTag() {
		return tagName;
	}
	
	public void setTag(String s) {
		tagName = s;
	}
	
	public static String Eol = System.getProperty("line.separator");
	
	public ListVector() {
		super(10);
	}

	public ListVector(int i) {
		super(i);
	}

	public ListVector(int i, int j) {
		super(i,j);
	}
	
	public Object get() {
		if (index > -1 && index < size())
			return elementAt(index);
		else return null;
	}

	public Object prev() {
		if (index > 0 && index < size()+1) {
			index--;
			return get();
		}
		else return null;
	}
		
		
	public Object get(int i) {
		if (i > -1 && i < size()) {
			index = i;
			return get();
		}
		else return null;
	}

	public String toXML() {
		XMLBuffer sbuf = new XMLBuffer();
		String tag;
		
		if (getTag() != null) {
			tag = getTag();
		} else 
			tag = getClass().getName();
		if (!tag.equals("")) sbuf.put("<"+tag);
		Enumeration ev;
		if (properties != null)
			if ((ev = properties.keys()).hasMoreElements()) {
				for(;ev.hasMoreElements();) {
					Object ek = ev.nextElement();
					sbuf.append(" "+ek+"=\""+properties.get(ek)+"\"");
				}
			}
		if (!tag.equals("")) sbuf.append(">"+Eol);
		sbuf.inc();
		for(reset();isNext();) {
			Object o = getNext();
			try {
				if (o.getClass().getMethod("toXML",null)!= null) {
					ToXML t = (ToXML) o;
					sbuf.append(t.toXML());
				} else {
					if (o.getClass().getMethod("getTag",null)!= null) {
						String q = (String) o.getClass().getMethod("getTag",null).invoke(o,null);
						sbuf.put("<"+q+">"+o.toString()+"</"+q+">"+Eol);
					} else  {
						String q = o.getClass().getName();
						sbuf.put("<"+q+">"+o.toString()+q+">"+Eol);
					}
				}
			} catch (Exception e) {
				System.out.println(e.toString()); 
				sbuf.put("<"+o.getClass().getName()+">"+o.toString()+
					"</"+o.getClass().getName()+">"+Eol);
			}
		}
		sbuf.dec();
		if (!tag.equals("")) sbuf.put("</"+tag+">"+Eol);
		return sbuf.toString();
	}
	
	public void reset() {
		index = -1;
	}
	
	public boolean isNext() {
		return (index+1 < size());
	}
	
	public Object getNext() {
		if (isNext())
			return elementAt(++index);
		else return null;
	}	

	public synchronized Object clone(boolean deep) {
/*		if (deep) {
			ListVector l = (ListVector) this.clone();
			for (l.reset();l.isNext();) {
				Object o = l.getNext();
				try {
					l.replace(o.clone(deep));
				} catch(Exception e) {
					l.replace(o.clone());
				}
			}
		}
		else */
		return this.clone();
	}
	
	public void replace(Object k) {
		setElementAt(k,index);
	}
	
	public void remove() {
		delete();
	}
	
	public void delete() {
		removeElementAt(index);
		index--;
	}
	
	public ListVector append(ListVector s) {
		for(s.reset();s.isNext();) {
			addElement(s.getNext());
		}
		return this;
	}

	public boolean addUnique(Object a) {
		if (indexOf(a) == -1) {
			addElement(a);
			return true;
		} else
			return false;
	}
	
	public ListVector appendUnique(ListVector s) {
		for(s.reset();s.isNext();) {
			addUnique(s.getNext());
		}
		return this;
	}
	
	public void setProperty(String p, String v) {
		if (properties == null) properties = new Hashtable(10);
		properties.put(p,v);
	}
}

