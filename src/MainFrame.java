//
//  MainFrame.java
//  KinshipEditor
//
//  Revised by Michael D. Fischer on 11/07/2006.
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

import java.awt.*;
import java.net.*;
import java.util.*;
//import org.csac.io.*;
import java.awt.event.*;
//import com.apple.eawt.Application;
//import com.apple.eawt.ApplicationAdapter;
//import com.apple.eawt.ApplicationEvent;

public class MainFrame extends Frame {


	Label splashLabel = null;
	
	public void MainFrame() {
	}

	ImagePanel ip=null;
	
	public void init() {
		setLayout(null);
		setVisible(false);
		int w=500,h=200;
		setSize(w,h);
		setLocation(100,200); // change this to centre
	/*	splashLabel = new Label("Starting up CSAC's KinshipEditor...");
		splashLabel.setSize(100,30);
		add(splashLabel);
		splashLabel.setLocation(w/2-(splashLabel.getSize().width/2),h/2-(splashLabel.getSize().height/2));
	*/	setTitle("KinshipEditor v3.0");
		setBackground(new Color(0xffffff));
		URL imgURL = getClass().getResource("logoKinship.png");

		 ip = new ImagePanel(imgURL);
		ip.setSize(490,120);
		add(ip);
		ip.setLocation(w/2-(ip.getSize().width/2),50);
		setVisible(true);
	}
	
	
	public static void main(String args[])
	{

	   MainFrame m = new MainFrame();
	   //Application theApp = getApp();
	   m.init();
			m.repaint();
			m.hide();
			m.show();
			//KinshipEditor.main(args);
			KinshipEditor k = new KinshipEditor();
			k.init();
			m.hide();
	}

	public Image getImage(URL u, String x)
  {
	   URL url=null;
	   try {
		  url = new URL( u,x);
	   }  catch (java.net.MalformedURLException e) {
	   }
	   Image image = getToolkit().getImage(url);
	   if (image != null)
	   {
		        MediaTracker mt = new MediaTracker(this);
		  if (mt != null)
		  {
			 try
		  {
			 mt.addImage(image, 0);
			 mt.waitForAll();
		  }
			 catch (InterruptedException ie)
		  {
		  }
			 			        if (mt.isErrorAny())
								{
								   System.err.println("Error loading image " + image.toString());
								   return null;
								}
			 			       return image;

			 //resize(image.getWidth(this) + bevel * 3 + 2, image.getHeight(this) + bevel * 3 + 2);
		  }
	   }
	   return null;
	}
/*
	   static Application getApp() {
		  Application theApp = new Application();
		  ApplicationAdapter theAppListener = new ApplicationAdapter();
		  theApp.addApplicationListener(theAppListener);
		  return theApp;
	   }
*/	
}
