//
//  kinship_editor_1_1.java
//  kinship editor 1.1
//
//  Created by Michael Fischer on Sun Oct 20 2002.
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


import java.util.Locale;
import java.util.ResourceBundle;
import java.awt.*;
import java.awt.event.*;
import com.apple.mrj.*;

public class kinship_editor_1_1 extends Frame
                        implements  ActionListener,
                                    MRJAboutHandler,
                                    MRJQuitHandler {

    private Font font = new Font("serif", Font.ITALIC+Font.BOLD, 36);
	
    protected AboutBox aboutBox;
    protected ResourceBundle resbundle;
	
    // Declarations for menus
    static final MenuBar mainMenuBar = new MenuBar();
	
    protected Menu fileMenu;
    protected MenuItem miNew;
    protected MenuItem miOpen;
    protected MenuItem miClose;
    protected MenuItem miSave;
    protected MenuItem miSaveAs;
	
    protected Menu editMenu;
    protected MenuItem miUndo;
    protected MenuItem miCut;
    protected MenuItem miCopy;
    protected MenuItem miPaste;
    protected MenuItem miClear;
    protected MenuItem miSelectAll;

    
    public void addFileMenuItems() {
        miNew = new MenuItem (resbundle.getString("newItem"));
        miNew.setShortcut(new MenuShortcut(KeyEvent.VK_N, false));
        fileMenu.add(miNew).setEnabled(true);
        miNew.addActionListener(this);
		
        miOpen = new MenuItem (resbundle.getString("openItem"));
        miOpen.setShortcut(new MenuShortcut(KeyEvent.VK_O, false));
        fileMenu.add(miOpen).setEnabled(true);
        miOpen.addActionListener(this);
		
        miClose = new MenuItem (resbundle.getString("closeItem"));
        miClose.setShortcut(new MenuShortcut(KeyEvent.VK_W, false));
        fileMenu.add(miClose).setEnabled(true);
        miClose.addActionListener(this);
		
        miSave = new MenuItem (resbundle.getString("saveItem"));
        miSave.setShortcut(new MenuShortcut(KeyEvent.VK_S, false));
        fileMenu.add(miSave).setEnabled(true);
        miSave.addActionListener(this);
		
        miSaveAs = new MenuItem (resbundle.getString("saveasItem"));
        miSaveAs.setShortcut(new MenuShortcut(KeyEvent.VK_S, true));
        fileMenu.add(miSaveAs).setEnabled(true);
        miSaveAs.addActionListener(this);
		
        mainMenuBar.add(fileMenu);
    }
	
	
    public void addEditMenuItems() {
        miUndo = new MenuItem(resbundle.getString("undoItem"));
        miUndo.setShortcut(new MenuShortcut(KeyEvent.VK_Z, false));
        editMenu.add(miUndo).setEnabled(true);
        miUndo.addActionListener(this);
        editMenu.addSeparator();
		
        miCut = new MenuItem(resbundle.getString("cutItem"));
        miCut.setShortcut(new MenuShortcut(KeyEvent.VK_X, false));
        editMenu.add(miCut).setEnabled(true);
        miCut.addActionListener(this);
		
        miCopy = new MenuItem(resbundle.getString("copyItem"));
        miCopy.setShortcut(new MenuShortcut(KeyEvent.VK_C, false));
        editMenu.add(miCopy).setEnabled(true);
        miCopy.addActionListener(this);
		
        miPaste = new MenuItem(resbundle.getString("pasteItem"));
        miPaste.setShortcut(new MenuShortcut(KeyEvent.VK_V, false));
        editMenu.add(miPaste).setEnabled(true);
        miPaste.addActionListener(this);
		
        miClear = new MenuItem(resbundle.getString("clearItem"));
        editMenu.add(miClear).setEnabled(true);
        miClear.addActionListener(this);
        editMenu.addSeparator();
		
        miSelectAll = new MenuItem(resbundle.getString("selectAllItem"));
        miSelectAll.setShortcut(new MenuShortcut(KeyEvent.VK_A, false));
        editMenu.add(miSelectAll).setEnabled(true);
        miSelectAll.addActionListener(this);
		
        mainMenuBar.add(editMenu);
    }
	
    public void addMenus() {
        editMenu = new Menu(resbundle.getString("editMenu"));
        fileMenu = new Menu(resbundle.getString("fileMenu"));
        addFileMenuItems();
        addEditMenuItems();
        setMenuBar (mainMenuBar);
    }
	
	
    public kinship_editor_1_1() {
        super("");
        WindowAdpt WAdapter = new WindowAdpt();
        this.addWindowListener(WAdapter);
                                
        // The ResourceBundle below contains all of the strings used in this application.  ResourceBundles
        // are useful for localizing applications - new localities can be added by adding additional 
        // properties files.  
        resbundle = ResourceBundle.getBundle("kinship_editor_1_1strings", Locale.getDefault());
        setTitle (resbundle.getString("frameConstructor"));
        setLayout(null);
        addMenus();
		
        aboutBox = new AboutBox();
        Toolkit.getDefaultToolkit();
        MRJApplicationUtils.registerAboutHandler(this);
        MRJApplicationUtils.registerQuitHandler(this);
		
        setVisible(true);
            
    }
	
    public void paint(Graphics g) {
        g.setColor(Color.blue);
        g.setFont (font);
        g.drawString(resbundle.getString("message"), 40, 80);
    }
    
    public void handleAbout()
    {
        aboutBox.setResizable(false);
        aboutBox.setVisible(true);
        aboutBox.show();
    }
	
    public void handleQuit()
    {	
        // If the application needs to save document/state before exiting, do so here
        System.exit(0);
    }

    // ActionListener interface (for menus)
    public void actionPerformed(ActionEvent newEvent) 
    {
        if (newEvent.getActionCommand().equals(miNew.getActionCommand())) doNew();
        else if (newEvent.getActionCommand().equals(miOpen.getActionCommand())) doOpen();
        else if (newEvent.getActionCommand().equals(miClose.getActionCommand())) doClose();
        else if (newEvent.getActionCommand().equals(miSave.getActionCommand())) doSave();
        else if (newEvent.getActionCommand().equals(miSaveAs.getActionCommand())) doSaveAs();
        else if (newEvent.getActionCommand().equals(miUndo.getActionCommand())) doUndo();
        else if (newEvent.getActionCommand().equals(miCut.getActionCommand())) doCut();
        else if (newEvent.getActionCommand().equals(miCopy.getActionCommand())) doCopy();
        else if (newEvent.getActionCommand().equals(miPaste.getActionCommand())) doPaste();
        else if (newEvent.getActionCommand().equals(miClear.getActionCommand())) doClear();
        else if (newEvent.getActionCommand().equals(miSelectAll.getActionCommand())) doSelectAll();
    }
	
    public void doNew() {
    }
	
    public void doOpen() {
    }
	
    public void doClose() {
    }
	
    public void doSave() {
    }
	
    public void doSaveAs() {
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
	
    class WindowAdpt extends java.awt.event.WindowAdapter {
        public void windowClosing(java.awt.event.WindowEvent event) {
            handleQuit();
        }
    }

        
    public static void main(String args[]) {
        new kinship_editor_1_1();
    }
}
