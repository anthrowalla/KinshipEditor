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

class StringStream extends Object {

	String prog=null;
	int offset=0;
	
	public final boolean TOLOWER=true;
	public final boolean TOUPPER=false;
	public final boolean TRIM=true;
	public final boolean NOTRIM=false;
	public final boolean NOBLANKS=true;
	public final boolean BLANKS=false;

	void setStream(String s) {
		prog = new String(s);

	}

	void setStream(String s, boolean upper_lower) {
		if (upper_lower) 
			setStream(s.toLowerCase());
		else setStream(s.toUpperCase());
	}
	
	String getStream() {
		return prog;
	}
	
	int getOffset() {
		return offset;
	}
	
	void setOffset(int o) {
		offset = o;
	}
	
	String sfgets() { // prog is line offSet pointer
		return sfgets(false); // don't trim
	}	
	
	String subit(String a, int s, int e) {
		StringBuffer k = new StringBuffer(e-s+1);
		
		for (int i = s;i<e;i++) k.append(a.charAt(i));
	
		return k.toString();
		
	}
	
	String sfgets(boolean trim) { // prog is line offSet pointer
												// return next offset.
		int pr;
		String xline=null;
		
		if (prog == null) return null;
		if (prog.length() <= this.offset) return(null); // empty
		if ((pr = prog.indexOf('\n',this.offset)) == -1) pr = prog.indexOf('\r',this.offset);
/*		if (pr == -1) {
			line = prog.substring(this.offset,prog.length());
		} else {
			line = prog.substring(this.offset,pr);
		}*/
		
		// fix following .... too many strings!!!!!!
		if (pr == -1) {
			xline = new String(prog.substring(this.offset,prog.length()));
		} else {
			xline = new String(prog.substring(this.offset,pr));
		}
		//line = "47";
		this.offset = pr+1;
		if (trim) return xline.trim();
		else return xline;
	}

	String sfgets(boolean trim, boolean noblanks) {
		if (noblanks) {
			String line;
			do 
				line = sfgets(trim);
			while (line == "");
			return line;
		} else return sfgets(trim);
	}
	
}
