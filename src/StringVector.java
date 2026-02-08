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

import java.util.Vector;
import java.lang.*;

public class StringVector extends ListVector {
	int index=-1;
	public static String Eol = System.getProperty("line.separator");

	public StringVector() {
		super(10);
	}
	public StringVector(int i) {
		super(i);
	}
	public StringVector(String i) {
		addElement(i);
	}
	public StringVector(int i, int j) {
		super(i,j);
	}
	public String getSymbol(int i){
		return (String) elementAt(i);
	}
	public void put(int i, String s) {
		if (size() < i+1) setSize(i+10);
		setElementAt(s,i);
	}
	public void add(String s) {
		addElement(s);
	}
	public String toXML() {
		if (getTag().equals("")) setTag("<Strings>");
		return super.toXML();
	}
	
	public void reset() {
		index = -1;
	}
	
	public boolean isNext() {
		return (index+1 < size());
	}
	
	public String getNextString() {
		if (isNext())
			return (String) elementAt(++index);
		else return null;
	}	
	
	public String getString() {
		if (index > -1 && index < size())
			return (String) elementAt(index);
		else return null;
	}	

	public synchronized Object clone(boolean deep) {
		return this.clone();
	}
	
	public void replace(String k) {
		setElementAt(k,index);
	}
	
	public void delete() {
		removeElementAt(index);
		index--;
	}

	public StringVector append(StringVector s) {
		for(s.reset();s.isNext();) {
			addElement(s.getNextString());
		}
		return this;
	}
}
