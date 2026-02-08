//
//  OSXAdapter.java
//  kinship editor 1.1
//
//  Created by Michael Fischer on 23/10/2005.
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

import com.apple.eawt.*;

public class OSXAdapter extends ApplicationAdapter {
	
	// pseudo-singleton model; no point in making multiple instances
	// of the EAWT application or our adapter
	private static OSXAdapter            theAdapter;
	private static com.apple.eawt.Application    theApplication;
	
	// reference to the app where the existing quit, about, prefs code is
	private KinshipEditor                  mainApp;
	
	private OSXAdapter (KinshipEditor inApp) {
		mainApp = inApp;
	}
	
	// implemented handler methods.  These are basically hooks into existing 
	// functionality from the main app, as if it came over from another platform.
	public void handleAbout(ApplicationEvent ae) {
		if (mainApp != null) {
			ae.setHandled(true);
			mainApp.handleAbout();
		} else {
			throw new IllegalStateException("handleAbout: MyApp instance detached from listener");
		}
	}
	
	public void handlePreferences(ApplicationEvent ae) {
		if (mainApp != null) {
			// mainApp.preferences();
			ae.setHandled(true);
		} else {
			throw new IllegalStateException("handlePreferences: MyApp instance detached from listener");
		}
	}
	
	public void handleQuit(ApplicationEvent ae) {
		if (mainApp != null) {
			/*  
			/  You MUST setHandled(false) if you want to delay or cancel the quit.
			/  This is important for cross-platform development -- have a universal quit
			/  routine that chooses whether or not to quit, so the functionality is identical
			/  on all platforms.  This example simply cancels the AppleEvent-based quit and
			/  defers to that universal method.
			*/
			ae.setHandled(false);
			mainApp.handleQuit();
		} else {
			throw new IllegalStateException("handleQuit: KinshipEditor instance detached from listener");
		}
	}
	
	
	// The main entry-point for this functionality.  This is the only method
	// that needs to be called at runtime, and it can easily be done using
	// reflection (see MyApp.java) 
	public static void registerMacOSXApplication(KinshipEditor inApp) {
		if (theApplication == null) {
			theApplication = new com.apple.eawt.Application();
		}      
		
		if (theAdapter == null) {
			theAdapter = new OSXAdapter(inApp);
		}
		theApplication.addApplicationListener(theAdapter);
	}
	
	// Another static entry point for EAWT functionality.  Enables the 
	// "Preferences..." menu item in the application menu. 
	public static void enablePrefs(boolean enabled) {
		if (theApplication == null) {
			theApplication = new com.apple.eawt.Application();
		}
		theApplication.setEnabledPreferencesMenu(enabled);
	}
}
