import java.awt.*;
import java.awt.Toolkit;
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


public class RenderString extends Component {
	String myString="this";
	int pointsize = 12;
	int style = Font.PLAIN;
	Font theFont=new Font("Timez",style,pointsize); 
	FontMetrics theMetrics=null;
	Image theImage=null;
	
	RenderString(String st, int sy, int ps) {
		myString = st;
		pointsize = ps;
		style=sy;
		theFont = new Font("Timez",style,pointsize);
		setFont(theFont);
		
		initMetrics();
		// theImage = createImage
	}
	
	public void setString(String s) {
		myString = s;
	}
	
	
	private void initMetrics() {
		theMetrics = getFontMetrics(theFont);
		
	}
	
	 public void setDimensionz() {
		int x,y;
		
		x = theMetrics.stringWidth(myString)+4;
	
	
	
	
		y = theMetrics.getMaxDescent() + theMetrics.getMaxAscent() + theMetrics.getLeading();
		//setSize(x,y);
		
		theImage = createImage(x,y);
		Graphics h = theImage.getGraphics();
		h.setFont(theFont);
		h.drawString(myString,2,y-3);
	}
	
	public void update() {
		
	}
	
	public void repaint() {
	//	paint(getGraphics());
	}
	public void paint(Graphics g) {
	//	g.drawImage(theImage,0,0,this);
	}
	
	public Image getImage() {
		return theImage;
	}
	
}
