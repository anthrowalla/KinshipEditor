/*
    A basic extension of the java.awt.Dialog class
 */
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

import java.awt.*;

public class DoYouWantToSave extends Dialog {
	public int result=STASIS;
	
	public static int SAVE = 1;
	public static int CANCEL = 0;
	public static int DISCARD = -1;
	public static int STASIS = -999;
	
	public DoYouWantToSave(Frame parent, boolean modal) {

	    super(parent, modal);

		//{{INIT_CONTROLS
		setLayout(null);
		resize(insets().left + insets().right + 412,insets().top + insets().bottom + 118);
		savebutton1 = new java.awt.Button("Save");
		savebutton1.reshape(insets().left + 83,insets().top + 60,60,23);
		add(savebutton1);
		discardbutton2 = new java.awt.Button("Discard");
		discardbutton2.reshape(insets().left + 274,insets().top + 60,60,23);
		add(discardbutton2);
		cancelbutton3 = new java.awt.Button("Cancel");
		cancelbutton3.reshape(insets().left + 180,insets().top + 59,60,23);
		cancelbutton3.setVisible(false);
		add(cancelbutton3);
		label1 = new java.awt.Label("You have made changes. Do you want to save your work?");
		label1.reshape(insets().left + 12,insets().top + 11,400,42);
		add(label1);
		setTitle("Do you want to save?");
		//}}
	}

	public DoYouWantToSave(Frame parent, String title, boolean modal) {
	    this(parent, modal);
	    setTitle(title);
	}

    public synchronized void show() {
    	Rectangle bounds = getParent().bounds();
    	Rectangle abounds = bounds();

    	move(bounds.x + (bounds.width - abounds.width)/ 2,
    	     bounds.y + (bounds.height - abounds.height)/2);

    	super.show();
    }

	public boolean handleEvent(Event event) {
	    if(event.id == Event.WINDOW_DESTROY) {
	        hide();
	        return true;
	    }
		if (event.target == savebutton1 && event.id == Event.ACTION_EVENT) {
			result = SAVE;
			this.hide();
			return true;
		}
		if (event.target == cancelbutton3 && event.id == Event.ACTION_EVENT) {
			result = CANCEL;
			this.hide();
			return true;
		}
		if (event.target == discardbutton2 && event.id == Event.ACTION_EVENT) {
			result = DISCARD;
			this.hide();
			return true;
		}
		return super.handleEvent(event);
	}

	//{{DECLARE_CONTROLS
	java.awt.Button savebutton1;
	java.awt.Button discardbutton2;
	java.awt.Button cancelbutton3;
	java.awt.Label label1;
	//}}
	
	public int getResult() {
		return result;
	}

}
