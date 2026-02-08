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

public class XMLIndent {
	static int level=0;
	static int tabstop=2;
	static String space = "";
	static String spacer = "                                                                             ";
	static String Eol = System.getProperty("line.separator");
	
	public static int getLevel() { return level;}
	public static int getTab() { return tabstop;}
	public static void setLevel(int i) { level=i;}

	public static void setTab(int i) {
		tabstop=i;
	}
	
	public static int getIndent() {
		int x = tabstop*level;
		if (x > spacer.length()) x = spacer.length();
		return x;
	}
	
	public static String getSpace() {
		return space;
	}
	
	public static void increment() {
		level++;
		space = spacer.substring(0,getIndent());
	}
	
	public static void decrement() {
		level--;
		if (level < 0) level = 0;
		space = spacer.substring(0,getIndent());
	}
	
	public static String pp(String s) {
		return space + s + Eol;
	}
}
