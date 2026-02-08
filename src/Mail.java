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

import java.net.*;
import java.io.*;
import java.util.*;
public class Mail extends Thread {
	String From=null; // eg mf1@ukc.ac.uk
	String To=null; // eg mf1@ukc.ac.uk
	String Subject="";
	String CC=null;
	String BCC=null;
	String Body=""; // nice if 80 col lines or shorter
	String Server=null; // form as lucy.ukc.ac.uk or 129.12.21.19
	// need to add provision for simple attachments!
	int mailPort=25; // SMPT port
	Socket mailSock = null;
	PrintStream outMail = null;
	
	public boolean openMail(String serve) {
		if (mailSock != null) closeMail();
		
		Server = serve;
		return openMail();
	}
	
	public boolean openMail(String serve, int port) {
		if (mailSock != null) closeMail();
		
		Server = serve;
		mailPort = port;
		return openMail();
	}
	
	public boolean openMail() {
		if (Server == null) return false; // may be able to determine???
		try {
			mailSock = new Socket(Server,mailPort); // SMPT port
		} catch (Exception e) {return false;}
		try {
			outMail = new PrintStream(mailSock.getOutputStream());
		} catch (Exception e) {return false;}
		return true;
	}
	
	public boolean mailLine(String s) {
		if (outMail == null) return false;
		outMail.println(s);
		return true;
	}
	
	public boolean mailHead(String f, String t, String c, String b) {
		From = f;
		To = t;
		CC = c;
		BCC = b;
		return mailHead();
	}
	
	
	public boolean mailHead(String f, String t) {
		From = f;
		To = t;
		CC = null;
		BCC = null;
		return mailHead();
	}
	
	public boolean mailHead() {
		if (outMail == null || From == null || To == null) return false;
		outMail.println("HELO");
		outMail.println("MAIL FROM: "+From);
		outMail.println("RCPT TO: "+To);
		outMail.println("DATA");
		outMail.println("From: "+From);
		outMail.println("To: "+To);
		if (Subject != null) outMail.println("Subject: "+Subject);
		else outMail.println("Subject: None");
		if (CC != null) outMail.println("cc: "+CC);
		if (BCC != null) outMail.println("bcc: "+BCC);
		outMail.println("");
		return true;
	}
	
	public void closeMail() {
		outMail.println(".");
		outMail.println("QUIT");
		outMail.flush();
		outMail.close();
		outMail = null;
		try {
			mailSock.close();
		} catch (Exception e) {}
		mailSock=null;
	}
	
	public String sendMail() {
		try {
			mailSock = new Socket(Server,mailPort); // SMPT port
		
			try {
				outMail = new PrintStream(mailSock.getOutputStream());
			
				outMail.println("HELO");
				outMail.println("MAIL FROM: "+From);
				outMail.println("RCPT TO: "+To);
				outMail.println("DATA");
				outMail.println("From: "+From);
				outMail.println("To: "+To);
				outMail.println("Subject: "+Subject);
				outMail.println("");
				outMail.println(Body);
				outMail.println(".");
				outMail.println("QUIT");
				outMail.flush();
				outMail.close();
				outMail = null;
				mailSock.close();
				mailSock=null;
			} catch (Exception e) {System.out.println("Failed in mail");
										return "Failed in mail";}
		} catch (Exception e) {System.out.println("Failed to make socket");
										return "Failed to make socket";}
		return ("ok");
	}
	
}
