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

public class LabeledField extends java.awt.Panel
{
	Label l1, l2;
	boolean inited = false;
	Label theLabel;
	TextField theEntry;
	Color bordColour=Color.gray;
	int hwid=4;
	int vwid=4;
	Panel p;
	Panel display; // a place to draw things etc .. a blank panel

	
	public void init() {
		inited = true;
		p = new Panel();
		this.setLayout(new BorderLayout());
		p.setLayout(new FlowLayout());
		theLabel = new Label("Label");
		theEntry = new TextField("Entry");
		//add("East",theLabel);
		//add("West",theEntry);
		p.add("East",theLabel);
		p.add("West",theEntry);
		add("South",p); // need insets
		display = new Panel();
		add("Center",display);
		//p.layout();
		layout();
		p.layout();
			//{{INIT_CONTROLS
		setLayout(null);
		setSize(220,41);
		//}}
}

	/*public void reshape(int x, int y, int w, int h) {
		super.reshape(x,y,w,h);
		layout();
	}*/
	public Insets insets() {
		return new Insets(hwid,vwid,hwid,vwid);
	}
	
	public Dimension minimumsize() {
		return new Dimension(hwid*2 + 72, vwid*2+20);
	}
		public Dimension preferredsize() {
		return new Dimension(hwid*2 + 80, vwid*2+25);
	}

	
	public void paint(Graphics g) {
		if (!inited) init();
		Color x = g.getColor();
		g.setColor(Color.gray);
		g.fillRoundRect(1,1,bounds().width-2,bounds().height-2,8,8);
//		g.fillRoundRect(-hwid,1,bounds().width+hwid,bounds().height+vwid,8,8);
		g.setColor(Color.white);
		g.fillRoundRect(hwid,vwid,bounds().width-4-hwid,bounds().height-4-vwid,8,8);
//		g.fillRoundRect(1,vwid,bounds().width,bounds().height,8,8);
		g.setColor(x);
		// p.paint(g);
		//theEntry.update(g);
		super.paint(g);
	}
	
	public void setTheLabel(Label theLabel) {
		this.theLabel = theLabel;
	}

	public Label getTheLabel() {
		return theLabel;
	}
	
	public void setTheEntry(TextField theEntry) {
		this.theEntry = theEntry;
	}

	public TextField getTheEntry() {
		return theEntry;
	}
	public void setHwid(int hwid) {
		this.hwid = hwid;
	}

	public int getHwid() {
		return hwid;
	}
	
	public void setVwid(int vwid) {
		this.vwid = vwid;
	}

	public int getVwid() {
		return vwid;
	}

	public void setBordColour(Color bordColour) {
		this.bordColour = bordColour;
	}

	public Color getBordColour() {
		return bordColour;
	}

	public void setDisplay(Panel display) {
		this.display = display;
	}

	public Panel getDisplay() {
		return display;
	}
	//{{DECLARE_CONTROLS
	//}}
}
