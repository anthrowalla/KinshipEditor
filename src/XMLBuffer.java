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

public class XMLBuffer {
	StringBuffer b=new StringBuffer();
	
	public static String EOL = System.getProperty("line.separator");
	
	public XMLBuffer put(String s) {
		b.append(XMLIndent.getSpace()+s+EOL);
		return this;
	}

	public XMLBuffer append(String s) {
		b.append(s);
		return this;
	}

	public String toString() {
		return (b.toString());
	}
	
	public XMLBuffer inc() {
		XMLIndent.increment();
		return this;
	}

	public XMLBuffer dec() {
		XMLIndent.decrement();
		return this;
	}

	public XMLBuffer eol() {
		b.append(EOL);
		return this;
	}

	public XMLBuffer space() {
		b.append(XMLIndent.getSpace());
		return this;
	}
	
	public XMLBuffer tag(String t) {
		b.append("<"+t+">");
		return this;
	}

	public XMLBuffer itag(String t) {
		b.append("</"+t+">");
		return this;
	}
	
	public XMLBuffer format(String s, int len) {
		int p,sp=-1, st=0;
		String t;
		for (;;) {
			if (s.length() < len) {
				put(s);
				break;
			}
			t = s.substring(0,len);
			if ((p = t.lastIndexOf(' ')) != -1) {
				t = t.substring(0,p);
			} else if ((p = t.lastIndexOf('\t')) != -1) {
				t = t.substring(0,p);
			} else if ((p = t.lastIndexOf('\n')) != -1) {
				t = t.substring(0,p);
			} else if ((p = t.lastIndexOf('\r')) != -1) {
				t = t.substring(0,p);
			} 
			put(t);
			s = s.substring(t.length());
		}
		return this;
	}
}
