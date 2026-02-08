/*
    A basic extension of the java.applet.Applet class
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
//import java.applet.*;
import java.net.*;
import com.apple.mrj.*;
import java.awt.event.*;
import java.lang.reflect.*;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;

public class KinshipEditor extends Frame implements KinEditor, ActionListener, MRJQuitHandler, MRJAboutHandler  {
 
   
   public static void main(String args[])
	{
	  //  Application theApp = new Application();
	   new KinshipEditor().init(); // .initApp(theApp);
	 // initApp();
	}
/*
   void initApp(Application app) {
	   init();
	  theApp = app;
	  if (theApp == null) theApp = new Application();
	  	  ApplicationAdapter theAppListener = new ApplicationAdapter() {
		 public void handleOpenFile(ApplicationEvent e) {
			String fname = e.getFilename();
			kinEditPanel1.loadFile(fname);
		 }
			 public void handleNewFile(ApplicationEvent e) {
				//String fname = e.getFilename();
				//kinEditPanel1.loadFile(fname);
			 }
			 public void handleOpenApplication(ApplicationEvent e) {
			init();
		 }
		 public void handleQuit(ApplicationEvent e) {
			if (kinEditPanel1.dirty) {
			   if (!kinEditPanel1.doWantToSave()) return; // cancelled
			}
			System.exit(0);
		 }
	  };
	  theApp.addApplicationListener(theAppListener);
   }
 */
  // Application theApp;

	 
   public void init() {
		//{{INIT_CONTROLS
	   GridBagLayout gridBagLayout;
		gridBagLayout = new GridBagLayout();
		setLayout(gridBagLayout);
		resize(746,492);
		setBackground(new Color(16777215));
		panel2 = new java.awt.Panel();
		panel2.setLayout(new BorderLayout(0,0));
		panel2.reshape(0,143,746,349);
		GridBagConstraints gbc;
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridheight = 2;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.SOUTH;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.insets = new Insets(0,0,0,0);
		((GridBagLayout)getLayout()).setConstraints(panel2, gbc);
		add(panel2);
		horizontalScrollbar1 = new java.awt.Scrollbar(Scrollbar.HORIZONTAL,0,100,0,18000);
		horizontalScrollbar1.setPageIncrement(100);
		horizontalScrollbar1.setLineIncrement(10);
		horizontalScrollbar1.reshape(0,333,746,16);
		panel2.add("South", horizontalScrollbar1);
		verticalScrollbar1 = new java.awt.Scrollbar(Scrollbar.VERTICAL,0,100,0,18000);
		verticalScrollbar1.setPageIncrement(100);
		verticalScrollbar1.setLineIncrement(10);
		verticalScrollbar1.reshape(730,0,16,333);
		panel2.add("East", verticalScrollbar1);
		kinEditPanel1 = new KinEditPanel();
		kinEditPanel1.setLayout(null);
		kinEditPanel1.reshape(0,0,730,333);
		kinEditPanel1.setBackground(new Color(16777215));
		panel2.add("Center", kinEditPanel1);
		panel1 = new java.awt.Panel();
		panel1.setLayout(null);
		panel1.reshape(0,0,746,140);
		panel1.setBackground(new Color(-2293764));
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.NORTH;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(0,0,0,0);
		((GridBagLayout)getLayout()).setConstraints(panel1, gbc);
		add(panel1);
		currYear = new java.awt.TextField();
		currYear.setVisible(false);
		currYear.reshape(123,89,60,28);
		currYear.setBackground(new Color(16777215));
		panel1.add(currYear);
		beginLabel = new java.awt.Label("Year");
		beginLabel.reshape(7,89,48,25);
		panel1.add(beginLabel);
		reasonLabel = new java.awt.Label("Reason");
		reasonLabel.setVisible(false);
		reasonLabel.reshape(139,62,58,25);
		panel1.add(reasonLabel);
		nameLabel = new java.awt.Label("Name");
		nameLabel.reshape(151,63,40,24);
		panel1.add(nameLabel);
		nameField = new java.awt.TextField();
		nameField.reshape(199,59,126,31);
		nameField.setBackground(new Color(16777215));
		panel1.add(nameField);
		birthLabel = new java.awt.Label("Birth Year");
		birthLabel.reshape(8,29,71,26);
		panel1.add(birthLabel);
		deathYear = new java.awt.TextField();
		deathYear.reshape(218,26,47,28);
		deathYear.setBackground(new Color(16777215));
		panel1.add(deathYear);
		deathLabel = new java.awt.Label("Death Year");
		deathLabel.reshape(144,29,74,27);
		panel1.add(deathLabel);
		birthYear = new java.awt.TextField();
		birthYear.reshape(82,26,47,28);
		birthYear.setBackground(new Color(16777215));
		panel1.add(birthYear);
		commentField = new java.awt.TextArea("NA",5,200,TextArea.SCROLLBARS_VERTICAL_ONLY);
		commentField.reshape(381,4,225,74);
		commentField.setBackground(new Color(16777215));
		panel1.add(commentField);
		label8 = new java.awt.Label("Comment");
		label8.reshape(353,81,75,24);
		panel1.add(label8);
		helpButton = new java.awt.Button();
		helpButton.setLabel("Help");
		helpButton.setVisible(true);
		helpButton.reshape(200,110,66,21);
		helpButton.setBackground(new Color(0xe8e8e8));
		panel1.add(helpButton);
		dataBaseButton = new java.awt.Button();
		dataBaseButton.setLabel("Database");
		dataBaseButton.setVisible(false);
		dataBaseButton.reshape(665,8,66,21);
		dataBaseButton.setBackground(new Color(16777215));
		panel1.add(dataBaseButton);
		// dataBaseButton.setVisible(true);
		causeChoice = new java.awt.Choice();
		causeChoice.addItem("NA");
		causeChoice.addItem("Death");
		causeChoice.addItem("Divorce");
		causeChoice.addItem("Other");
		causeChoice.setVisible(false);
		panel1.add(causeChoice);
		causeChoice.reshape(200,61,95,21);
		causeChoice.setBackground(new Color(16777215));
		
	//	csac.xml.XMLLabelField xf = new csac.xml.XMLLabelField("Lable 1","Lable1","Value","7");
	//	panel1.add(xf);
		//if (!inApplet) {
			// INIT_MENUS
			menuBar1 = new java.awt.MenuBar();
			kinMenu = new java.awt.Menu("File", true);
			kinMenu.add( (newItem = new java.awt.MenuItem("New", new MenuShortcut(78))));
			loadItem = new java.awt.MenuItem("Open", new MenuShortcut(79));
			kinMenu.add(loadItem);
			kinMenu.add(new java.awt.MenuItem("-"));
			saveItem = new java.awt.MenuItem("Save", new MenuShortcut(83));
			kinMenu.add(saveItem);
			saveAsItem = new java.awt.MenuItem("Save As...", new MenuShortcut(83,true));
			kinMenu.add(saveAsItem);
			kinMenu.add(new java.awt.MenuItem("-"));
			deleteAllItem = new java.awt.MenuItem("Clear all");
			kinMenu.add(deleteAllItem);
            kinMenu.add(new java.awt.MenuItem("-"));
            renderItem = new java.awt.MenuItem("Render Visible", new MenuShortcut(82,false));
            kinMenu.add(renderItem);
            renderChartItem = new java.awt.MenuItem("Render Chart", new MenuShortcut(82,true));
            kinMenu.add(renderChartItem);
			kinMenu.add(new java.awt.MenuItem("-"));
			quitItem = new java.awt.MenuItem("Quit", new MenuShortcut(81));
			kinMenu.add(quitItem);
			menuBar1.add(kinMenu);
			
			symbolMenu = new java.awt.Menu("Labels",true);
			noLabelItem = new java.awt.CheckboxMenuItem("No Label", false);
			noLabelItem.setShortcut(new MenuShortcut(78));
			initialsItem = new java.awt.CheckboxMenuItem("Initials",false);
			initialsItem.setShortcut( new MenuShortcut(73));
			firstItem = new java.awt.CheckboxMenuItem("First", false);
			firstItem.setShortcut( new MenuShortcut(70));
			lastItem = new java.awt.CheckboxMenuItem("Last", false);
			lastItem.setShortcut( new MenuShortcut(76));
			wholeItem = new java.awt.CheckboxMenuItem("Whole", true);
			wholeItem.setShortcut( new MenuShortcut(90));
			lastLabelItem = (CheckboxMenuItem) wholeItem;
			symbolMenu.add(noLabelItem);
			symbolMenu.add(initialsItem);
			symbolMenu.add(firstItem);
			symbolMenu.add(lastItem);
			symbolMenu.add(wholeItem);
			menuBar1.add(symbolMenu);
			setMenuBar(menuBar1);
			
			MRJApplicationUtils.registerAboutHandler(this);
			MRJApplicationUtils.registerQuitHandler(this);


	//	}
		
		editableButton = new java.awt.Checkbox("Editable");
		editableButton.reshape(433,83,93,20);
		panel1.add(editableButton);
		editableButton.setState(true);
		choice2 = new java.awt.Choice();
		choice2.setVisible(false);
		panel1.add(choice2);
		choice2.reshape(169,0,80,21);
		choice2.setBackground(new Color(0xe8e8e8));
		egoButton = new java.awt.Checkbox("Fix Ego");
		egoButton.reshape(530,79,79,26);
		panel1.add(egoButton);
		beginYear = new java.awt.TextField();
		beginYear.setText("NA");
		beginYear.reshape(7,59,47,28);
		beginYear.setBackground(new Color(16777215));
		panel1.add(beginYear);
		endYear = new java.awt.TextField();
		endYear.setText("NA");
		endYear.reshape(64,59,47,28);
		endYear.setBackground(new Color(16777215));
		panel1.add(endYear);
		stepButton = new java.awt.Button();
		stepButton.setLabel("Step to...");
		stepButton.reshape(60,92,80,21);
		stepButton.setBackground(new Color(0xe8e8e8));
		panel1.add(stepButton);
		//}}
		
		kinEditPanel1.init(this);
		panel1.reshape(0,0,566,140);
		
		theTimer = new Timer(1600,kinEditPanel1);
		//{{REGISTER_LISTENERS
		//}}
		macOSXRegistration();
		kinEditPanel1.dirty = false;
        setResizable(true);
        setVisible(true);
        // SymWindow aSymWindow = new SymWindow();
      //  this.addWindowListener(new SymWindow());
       // SymComponent aSymComponent = new SymComponent();
     //   this.addComponentListener(new SymComponent());
    }

  class SymWindow extends java.awt.event.WindowAdapter
{
  public void windowClosing(java.awt.event.WindowEvent event)
  {
            Object object = event.getSource();
            if (object == KinshipEditor.this) {
				handleQuit();
            }
            //  KintermFrame_WindowClosing(event);
            
  }
}

// ActionListener interface (for menus)
public void actionPerformed(ActionEvent newEvent) 
{
	if (newEvent.getActionCommand().equals(newItem.getActionCommand())) doNew();
	else if (newEvent.getActionCommand().equals(loadItem.getActionCommand())) doOpen();
//	else if (newEvent.getActionCommand().equals(miClose.getActionCommand())) doClose();
	else if (newEvent.getActionCommand().equals(saveItem.getActionCommand())) doSave();
	else if (newEvent.getActionCommand().equals(saveAsItem.getActionCommand())) doSaveAs();
//	else if (newEvent.getActionCommand().equals(miUndo.getActionCommand())) doUndo();
//	else if (newEvent.getActionCommand().equals(miCut.getActionCommand())) doCut();
//	else if (newEvent.getActionCommand().equals(miCopy.getActionCommand())) doCopy();
//	else if (newEvent.getActionCommand().equals(miPaste.getActionCommand())) doPaste();
//	else if (newEvent.getActionCommand().equals(miClear.getActionCommand())) doClear();
//	else if (newEvent.getActionCommand().equals(miSelectAll.getActionCommand())) doSelectAll();
}
public void doNew() {
	if (kinEditPanel1.dirty) {
		kinEditPanel1.doWantToSave();
	}
	if (kinEditPanel1.editable) {
		theTimer.pause();
		kinEditPanel1.deleteAll();
		kinEditPanel1.fixEgo = false;
		egoButton.setState(false);
	}
}

public void doOpen() {
	kinEditPanel1.loadFile();
}

public void doClose() {
}

public void doSave() {
	kinEditPanel1.saveFile();
}

public void doSaveAs() {
	kinEditPanel1.saveAsFile();
}

public void doUndo() {
}

public void doCut() {
}

public void doCopy() {
}

public void doPaste() {
}

public void doClear() {
}

public void doSelectAll() {
}


public void handleQuit() {
	if (kinEditPanel1.dirty) {
		kinEditPanel1.doWantToSave();
	}
	System.exit(0);
}

   public void handleMenuEvent(Event event) {  

	  if (event.target == newItem && event.id == Event.ACTION_EVENT) {
		  doNew();
	  }
	  if (event.target == loadItem && event.id == Event.ACTION_EVENT) {
			doOpen();
		}
		if (event.target == saveItem && event.id == Event.ACTION_EVENT) {
			doSave();
		}
		if (event.target == saveAsItem && event.id == Event.ACTION_EVENT) {
			doSaveAs();
		}
      if (event.target == renderItem && event.id == Event.ACTION_EVENT) {
        kinEditPanel1.renderChart(true);
      }
      if (event.target == renderChartItem && event.id == Event.ACTION_EVENT) {
        kinEditPanel1.renderChart(false);
      }
      if (event.target == deleteAllItem && event.id == Event.ACTION_EVENT) {
		  if (kinEditPanel1.dirty) {
			  kinEditPanel1.doWantToSave();
		  }
		  if (kinEditPanel1.editable) {
				theTimer.pause();
				kinEditPanel1.deleteAll();
				kinEditPanel1.fixEgo = false;
				egoButton.setState(false);
			}
		}
		if (event.target == noLabelItem && event.id == Event.ACTION_EVENT) {
			labelMenu_Action(event.target);
		}
		if (event.target == initialsItem && event.id == Event.ACTION_EVENT) {
			labelMenu_Action(event.target);
		}
		if (event.target == firstItem && event.id == Event.ACTION_EVENT) {
			labelMenu_Action(event.target);
		}
		if (event.target == lastItem && event.id == Event.ACTION_EVENT) {
			labelMenu_Action(event.target);
		}
		if (event.target == wholeItem && event.id == Event.ACTION_EVENT) {
			labelMenu_Action(event.target);
		}
		if (event.target == quitItem && event.id == Event.ACTION_EVENT) {
		   if (kinEditPanel1.dirty) {
			  kinEditPanel1.doWantToSave();
		   }
		   System.exit(0);
		}
		
		
	}

	public boolean handleEvent(Event event) {

		if (event.target == nameField && (event.id == Event.LOST_FOCUS ||
		event.id == Event.ACTION_EVENT)) {
			nameField_LostFocus(event);
			return true;
		}
		if (event.target == commentField && event.id == Event.LOST_FOCUS) {
			commentField_LostFocus(event);
			return true;
		}
		if (event.target == deathYear && event.id == Event.LOST_FOCUS) {
			deathYear_LostFocus(event);
			return true;
		}
		if (event.target == birthYear && event.id == Event.LOST_FOCUS) {
			birthYear_LostFocus(event);
			return true;
		}
		if (event.target == dataBaseButton && event.id == Event.ACTION_EVENT) {
			// dataBaseButton_Clicked(event);
			return true;
		}
		if (event.target == helpButton && event.id == Event.ACTION_EVENT) {
			Frame y = new Frame();
			y.reshape(40,40,480,350);
			HelpDialog helpDialog1 = new HelpDialog(y, "Kinship Editor Help", false);	
			helpDialog1.setFont(new Font("Serif", Font.BOLD, 12));
			y = null;
			if (helpDialog1.getHelp(getDocumentBase())) {
				helpDialog1.show();
				helpDialog1.toFront();
			}
		}

		if (event.target == deleteButton && event.id == Event.ACTION_EVENT) {
			kinEditPanel1.deleteAll();
			return true;
		}
		if (event.target == verticalScrollbar1 || event.target == horizontalScrollbar1) {
			Scrollbar_Scroll(event);
			return true;
		}
		if (event.target == causeChoice && event.id == Event.ACTION_EVENT) {
			causeChoice_Action(event);
			return true;
		}
		if (event.target == editableButton && event.id == Event.ACTION_EVENT) {
			editableButton_Action(event);
			return true;
		}
		if (event.target == choice2 && event.id == Event.ACTION_EVENT) {
			choice2_Action(event);
			return true;
		}
		if (event.target == choice1 && event.id == Event.ACTION_EVENT) {
			choice1_Action(event);
			return true;
		}
		if (event.target == beginYear && (event.id == Event.LOST_FOCUS ||
					event.id == Event.ACTION_EVENT)) {
			beginYear_LostFocus(event);
			return true;
		}
		if (event.target == endYear && (event.id == Event.LOST_FOCUS ||
					event.id == Event.ACTION_EVENT)) {
			endYear_EnterHit(event);
			return true;
		}
		if (event.target == stepButton && event.id == Event.ACTION_EVENT) {
			stepButton_Clicked(event);
			return true;
		}
		if (event.target == egoButton && event.id == Event.ACTION_EVENT) {
			egoButton_Action(event);
			return true;
		}
		// Menu actions passed down from AppletFrame
		// only operative in the application invocation of this code
		if (event.target instanceof MenuItem && event.id == Event.ACTION_EVENT) {
			handleMenuEvent(event);
			return true;
		}

		//storeInfo();
		return super.handleEvent(event);
	}
	//{{DECLARE_CONTROLS
	java.awt.Panel panel2;
	
	java.awt.Scrollbar horizontalScrollbar1;
	java.awt.Scrollbar verticalScrollbar1;
	KinEditPanel kinEditPanel1;
	java.awt.Panel panel1;
	java.awt.TextField currYear;
	java.awt.Label beginLabel;
	java.awt.Button deleteButton;
	java.awt.Label reasonLabel;
	java.awt.Label nameLabel;
	java.awt.TextField nameField;
	java.awt.Label birthLabel;
	java.awt.TextField deathYear;
	java.awt.Label deathLabel;
	java.awt.TextField birthYear;
	java.awt.TextArea commentField;
	java.awt.Label label8;
	java.awt.Button dataBaseButton;
	java.awt.Button helpButton;
	java.awt.Choice causeChoice;
	java.awt.Checkbox editableButton;
	java.awt.Choice choice1;
	java.awt.Label labelch2;
	java.awt.Choice choice2;
	java.awt.Checkbox egoButton;
	java.awt.TextField beginYear;
	java.awt.TextField endYear;
	java.awt.Button stepButton; 
	//}}
	
	//{{DECLARE_MENUS
	static java.awt.MenuBar menuBar1;
	java.awt.Menu kinMenu;
	java.awt.MenuItem newItem;
	java.awt.MenuItem loadItem;
	java.awt.MenuItem saveItem;
	java.awt.MenuItem saveAsItem;
    java.awt.MenuItem renderItem;
    java.awt.MenuItem renderChartItem;
	java.awt.MenuItem deleteAllItem;
	java.awt.MenuItem quitItem;

	java.awt.Menu symbolMenu;
	java.awt.MenuItem noLabelItem;
	java.awt.MenuItem initialsItem;
	java.awt.MenuItem wholeItem;
	java.awt.MenuItem firstItem;
	java.awt.MenuItem lastItem;
	//}}

	//DataBase dbFrame=null;
	Timer theTimer;
	String aURL=null;
	String canEdit=null;
	String[][] urlList=null;
	static java.net.URL documentBase=null;
	static java.net.URL codeBase=null;
	
	
	public String getURL() {
		return aURL;
	}



	Person infoPerson = null;
	Marriage infoMarriage = null;
	
	public void showInfo(Person parentList) {
		infoPerson = parentList;
		infoMarriage = null;
		reasonLabel.hide();
		causeChoice.hide();
		nameLabel.show();
		nameField.show();
		deathLabel.setText("Death Year");
		birthLabel.setText("Birth Year");
		nameField.setText(parentList.name);
		birthYear.setText(parentList.yob);
		deathYear.setText(parentList.yod);
		commentField.setText(parentList.comment);
	}
	
	public void showInfo(Marriage parentList) {
		int DIVORCE=2, DEATH=1, NA=0;
		
		infoPerson = null;
		reasonLabel.show();
		causeChoice.show();
		nameLabel.hide();
		nameField.hide();
		deathLabel.setText("End Year");
		birthLabel.setText("Begin Year");
		infoMarriage = parentList;
		String reason = parentList.reason;

	
		causeChoice.select(reason);

		birthYear.setText(parentList.begin);
		deathYear.setText(parentList.end);
		commentField.setText(parentList.comment);
	}

	void storeInfo() {
		boolean dirty = kinEditPanel1.dirty;
		String a="";
		//{{CONNECTION
		if (infoPerson != null) {
			if (!infoPerson.name.equals(a = nameField.getText())) {
				infoPerson.name = a;
				dirty = true;
			}
			if (!infoPerson.yob.equals(a = birthYear.getText())) {
				infoPerson.yob = a;
				dirty = true;
			}
			if (!infoPerson.yod.equals(a = deathYear.getText())) {
				infoPerson.yod = a;
				dirty = true;
			}
			if (!infoPerson.comment.equals(a = commentField.getText())) {
				infoPerson.comment = a;
				dirty = true;
			}
			 kinEditPanel1.dirty=dirty;
		} else if (infoMarriage != null) {
	//		infoMarriage.reason = marriageEnd.getCurrent().getLabel();
			if (!infoMarriage.reason.equals(a = causeChoice.getSelectedItem())) {
				infoMarriage.reason = a;
				dirty = true;
			}
			
			if (!infoMarriage.begin.equals(a = birthYear.getText())) {
				infoMarriage.begin = a;
				dirty = true;
			}
			if (!infoMarriage.end.equals(a = deathYear.getText())) {
				infoMarriage.end = a;
				dirty = true;
			}
			if (!infoMarriage.comment.equals(a = commentField.getText())) {
				infoMarriage.comment = a;
				dirty = true;
			}
			kinEditPanel1.dirty=dirty;
		}
		//}}
	}
	
	public void clearInfo() {
		if (infoPerson != null || infoMarriage != null) {
			storeInfo();
		}
		infoPerson = null;
		infoMarriage = null;
		nameField.setText("");
		birthYear.setText("");
		deathYear.setText("");
		commentField.setText("");
		causeChoice.select("NA");
		//marriageList.clear();
		//childList.clear();
		//sibList.clear();
		//parentList.clear();
		repaint();
	}
	
	void nameField_LostFocus(Event event) {
		//{{CONNECTION
		if (infoPerson != null) 
			infoPerson.name = nameField.getText();
		else if (infoMarriage != null) 
			infoMarriage.reason = causeChoice.getSelectedItem();
		if (kinEditPanel1.getDoLabel() != kinEditPanel1.NOLABEL) kinEditPanel1.repaint();

		//}}
	}

	void commentField_LostFocus(Event event) {
		//{{CONNECTION
		if (infoPerson != null) 
			infoPerson.comment = commentField.getText();
		else if (infoMarriage != null) 
			infoMarriage.comment = commentField.getText();
		//}}
	}

	void deathYear_LostFocus(Event event) {			 
		//{{CONNECTION
		if (infoPerson != null) 
			infoPerson.yod = deathYear.getText();
		else if (infoMarriage != null) 
			infoMarriage.end = deathYear.getText();
		kinEditPanel1.repaint();
		//}}
	}

	void birthYear_LostFocus(Event event) {			 
		//{{CONNECTION
		if (infoPerson != null) 
			infoPerson.yob = birthYear.getText();
		else if (infoMarriage != null) 
			infoMarriage.begin = birthYear.getText();
		kinEditPanel1.repaint();
		//}}
	}
	
	
	void causeChoice_Action(Event event) {

			 
		//{{CONNECTION
		infoMarriage.reason = causeChoice.getSelectedItem();
		kinEditPanel1.dirty=true;
		//}}
	}
/*
	void dataBaseButton_Clicked(Event event) {

			 
		//{{CONNECTION
		if (dbFrame == null) 
			dbFrame = new DataBase("Database", this, kinEditPanel1);
		dbFrame.show();
		//}}
	}
*/
	public void resetScroll() {
		horizontalScrollbar1.setValue(0);
		verticalScrollbar1.setValue(0);
	}
	
	public void setScroll(int h, int v) {
		horizontalScrollbar1.setValue(h);
		verticalScrollbar1.setValue(v);
	}

	void Scrollbar_Scroll(Event event) {

		
		//{{CONNECTION
		kinEditPanel1.setOrigin(-horizontalScrollbar1.getValue(),-verticalScrollbar1.getValue());
		kinEditPanel1.repaint();
		//}}
	}

	
	void editableButton_Action(Event event) {

			 
		//{{CONNECTION
		boolean x = editableButton.getState();
		if (x) kinEditPanel1.editable = true;
		else kinEditPanel1.editable = false;
		setEditable(x);
		kinEditPanel1.dirty=true;
		//}}
	}
	
	public void setEditable(boolean e) {
		egoButton.show(e);
		//deleteButton.show(e);
		nameField.setEditable(e);
		birthYear.setEditable(e);
		deathYear.setEditable(e);
		commentField.setEditable(e);
		beginYear.setEditable(e);
		endYear.setEditable(e);
	}

	public MenuItem getLabelChoice(String label) {
		int count = symbolMenu.countItems();
		int i;
		for (i=0;i<count;i++) {
			if (symbolMenu.getItem(i).getLabel().equals(label))
				return symbolMenu.getItem(i);
		}
		return null;
	}
	
	CheckboxMenuItem lastLabelItem = null;
	
	void labelMenu_Action(Object item) {
		//{{CONNECTION
		kinEditPanel1.setDoLabel(((MenuItem) item).getLabel());
		if (lastLabelItem != null) lastLabelItem.setState(false);
		lastLabelItem = (CheckboxMenuItem) item;
		lastLabelItem.setState(true);
		kinEditPanel1.repaint();
		//}}
	}

	void choice2_Action(Event event) {

			 
		//{{CONNECTION
		show(!isShowing());
		//}}
	}

	void choice1_Action(Event event) {
		show(!isShowing());
	}

	void beginYear_LostFocus(Event event) {

		 kinEditPanel1.setRefYear(0);
		//{{CONNECTION
		try {
			int x = Integer.parseInt(beginYear.getText());
			kinEditPanel1.setRefYear(x);
		} catch (Exception e) {
			beginYear.setText("NA");
			kinEditPanel1.setRefYear(0);
		}
		//}}
		kinEditPanel1.repaint();
	}

	void endYear_EnterHit(Event event) {

			 
		//{{CONNECTION
		try {
			int x = Integer.parseInt(endYear.getText());
			
		} catch (Exception e) {
			endYear.setText("NA");
		}
		//}}
	}

	void stepButton_Clicked(Event event) {
		int s, t;
			 
		//{{CONNECTION
		if (theTimer.getIndex() != -1) {
			if (theTimer.isExecuting()) {
				theTimer.pause();
			} else {
				theTimer.resume();
			}
			return;
		}
		theTimer.resume();
		try {
			s = Integer.parseInt(beginYear.getText());
			t = Integer.parseInt(endYear.getText());
		} catch (Exception e) {
			return;
		}
		if (t < s) {
			int q = s;
			s = t;
			t = q;
		}
		
		theTimer.start(s,t);
//		currYear.setVisible(true);
		//}}
	}

	void egoButton_Action(Event event) {

			 
		//{{CONNECTION
		kinEditPanel1.fixEgo = egoButton.getState();
		kinEditPanel1.dirty=true;
		//}}
	}


	public void xcomponentResized(java.awt.event.ComponentEvent event)
		{
			Object object = event.getSource();
			if (object == KinshipEditor.this)
				KinshipEditor_MouseDragged(event);
		}
	

	

	void KinshipEditor_MouseDragged(java.awt.event.ComponentEvent event)
	{
		// to do: code goes here.
        
		//{{CONNECTION
		// Repaint the KinshipEditor
		layout();
		//}}
	}
/*
    public void setBounds(int x, int y,int w,int h) {
      super.reshape(x,y,w,h);
  //    if (scrollKin != null) {
        //	System.out.println("Trying in setBounds");
  //      scrollKin.setSize(w,h-207);
  //      scrollKin.layout();
  //    }
    }
 */  
    public Image getImage(URL u, String x)
        // throws AWTException
    {
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
    }
	private static void initializeApp()
	{
        StringBuffer p = new StringBuffer(System.getProperty("user.dir"));
        int         pl = p.length();

        // If the system file separator isn't the URL file separator convert it.
        try
        {
            char ps = (System.getProperty("file.separator")).charAt(0);
            if(ps != '/')
                for(int counter = 0; counter < pl; counter++)
                {
                    if(ps == p.charAt(counter)) p.setCharAt(counter, '/');
                }
        } catch(StringIndexOutOfBoundsException e) {} 
 
        try {
            documentBase = new URL("file://" + p + "/");
        } catch (java.net.MalformedURLException e) {
        }
	}
	
	public URL getDocumentBase() {
		try {
			if (documentBase == null) initializeApp();
			return documentBase;
		} catch (Exception e) {return null;}
	}
	
	public URL getCodeBase() {
		try {
			if (documentBase == null) initializeApp();
			return (codeBase = documentBase);
		} catch (Exception e) {return null;}
	}

	public void DBDisplay(String key, String value) {
	
	}


	public void handleAbout()
	{
//		aboutBox.setResizable(false);
//		aboutBox.setVisible(true);
//		aboutBox.show();
	}

public static boolean MAC_OS_X = (System.getProperty("os.name").toLowerCase().startsWith("mac os x"));

public void macOSXRegistration() {
    if (MAC_OS_X) {
		try {
			Class osxAdapter = ClassLoader.getSystemClassLoader().loadClass("apple.dts.samplecode.osxadapter.OSXAdapter");
			
			Class[] defArgs = {KinshipEditor.class};
			Method registerMethod = osxAdapter.getDeclaredMethod("registerMacOSXApplication", defArgs);
			if (registerMethod != null) {
				Object[] args = { this };
				registerMethod.invoke(osxAdapter, args);
			}
			// This is slightly gross.  to reflectively access methods with boolean args, 
			// use "boolean.class", then pass a Boolean object in as the arg, which apparently
			// gets converted for you by the reflection system.
			defArgs[0] = boolean.class;
			Method prefsEnableMethod =  osxAdapter.getDeclaredMethod("enablePrefs", defArgs);
			if (prefsEnableMethod != null) {
				Object args[] = {Boolean.TRUE};
				prefsEnableMethod.invoke(osxAdapter, args);
			}
		} catch (NoClassDefFoundError e) {
			// This will be thrown first if the OSXAdapter is loaded on a system without the EAWT
			// because OSXAdapter extends ApplicationAdapter in its def
			System.err.println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
		} catch (ClassNotFoundException e) {
			// This shouldn't be reached; if there's a problem with the OSXAdapter we should get the 
			// above NoClassDefFoundError first.
			System.err.println("This version of Mac OS X does not support the Apple EAWT.  Application Menu handling has been disabled (" + e + ")");
		} catch (Exception e) {
			System.err.println("Exception while loading the OSXAdapter:");
			e.printStackTrace();
		}
    }
}

}
