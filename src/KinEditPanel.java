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
import java.net.*;
import java.io.*;

public class KinEditPanel extends Panel implements TimerTarget {
	
	KinshipEditor supervisor = null; // must cull out ... make interface? 
	public boolean dirty=false;
	static String Eol = System.getProperty("line.separator");
	
	public void init(KinshipEditor k) {
		supervisor=k;
		//{{INIT_CONTROLS
		setLayout(null); 
		resize(498,457);
		setBackground(new Color(16777215));
		personMenu = new java.awt.List(4,false);
		personMenu.addItem("Female");
		personMenu.addItem("Male");
		personMenu.addItem("Neuter");
		personMenu.addItem("Union");
		personMenu.setVisible(false);
		add(personMenu);
		personMenu.reshape(314,233,78,74);
		personMenu.setFont(new Font("Dialog", Font.PLAIN, 10));
		//}}
	//	folks = new Person[1000]; // make more dynamic!!!
	//	folkIndex = -1;
		//knots = new Marriage[700]; // make more dynamic!!!
		Marriage.knotIndex = -1;
		selectLine = null;
	}

	public boolean handleEvent(Event event) {
		event.x -= originX;
		event.y -= originY;
		if (event.target == this && event.id == Event.MOUSE_DOWN) {
			KinshipEditor_MouseDown(event);
			return true;
		}
		if (event.target == personMenu && event.id == Event.MOUSE_EXIT) {
			personMenu_MouseExit(event);
			return true;
		}
		if (event.target == personMenu && event.id == Event.LIST_SELECT) {
			personMenu_ListSelect(event);
			return true;
		}
		if (event.target == this && event.id == Event.MOUSE_DRAG) {
			KinshipEditor_MouseDrag(event);
			return true;
		}
		if (event.target == this && event.id == Event.MOUSE_UP) {
			KinshipEditor_MouseUp(event);
			return true;
		}
		if (event.target == this && event.id == Event.KEY_PRESS) {
			KinEditPanel_KeyPress(event);
			return true;
		}
		if (event.target == this && event.id == Event.KEY_RELEASE) {
			KinEditPanel_KeyRelease(event);
			return true;
		}
		return super.handleEvent(event);
	}

	//{{DECLARE_CONTROLS
	java.awt.List personMenu;
	//}}
	
	Point lastLoc = new Point(-1,-1);

    int whichFolk = -1;
	int whichKnot = -1;
	int tiedKnot = -1;
	Line selectLine;
	int whichHalf = -1;
	
	boolean fixEgo=false;
	boolean editable=true;
	
	int originX=0;
	int originY=0;
	
	int refYear=0;
	
	public static final int NOLABEL=0;
	public static final int FIRST=2;
	public static final int LAST = 3;
	public static final int INITIALS = 1;
	public static final int WHOLE = 4;
	
	int doLabel=INITIALS;
	
	/** Sets up the simulated timeline for the genealogy
	**	@param k the current reference year
	**/
	public void doTimerTarget(int k) {
		setRefYear(k);
		if (!supervisor.currYear.isVisible()) supervisor.currYear.show(true);
		supervisor.currYear.setText(new Integer(k).toString());
		update(getGraphics());
	}
	
	public void clearTimerTarget() {
		supervisor.currYear.setText("");
		supervisor.currYear.hide();
		// should probably also set the refernce year for Marriage and Person
		setRefYear(0);
	}
	
	/** returns the labelling mode as an int
	**/
	public int getDoLabel() {
		return doLabel;
	}
	
	/** sets up the labelling mode
	**  @param c the label to interpret
	**/
	public void setDoLabel(String c) {
		doLabel = NOLABEL;
		if (c.equals("No Label")) doLabel = NOLABEL;
		else if (c.equals("Initials")) doLabel = INITIALS;
		else if (c.equals("First")) doLabel = FIRST;
		else if (c.equals("Last")) doLabel = LAST;
		else if (c.equals("Whole")) doLabel = WHOLE;
		Person.doLabel = doLabel;
		dirty=true;
		repaint();
	}

	/** sets up the labelling mode in the supervisor
	**  @param c the labelling state to represent
	**/
	public void setSuperDoLabel(int c) {
		MenuItem mi = supervisor.symbolMenu.getItem(c);
		if (mi != null) supervisor.labelMenu_Action(mi);
	}
    
    /** sets the reference year in the Person and Marriage objects
	**  @param x the reference year
	**/
	public void setRefYear(int x) {
		refYear = x;
		if (refYear <= 0) {
			Person.refYear = "";
			Marriage.refYear = "";
		} else {
			Person.refYear = refYear + "";
			Marriage.refYear = refYear + "";
		}
	}
	
	public void setOrigin(int x, int y) {
		originX = x;
		originY = y;
	}
	
	public void doFixEgo(int which) {
		if (whichFolk == -1) {
			whichFolk = which;
			return;
		}
		// do some alt action
	} 
	
	Point lastPersonLoc = null;

	void KinshipEditor_MouseDown(Event event) {
		
		//{{CONNECTION
		int which=-1;
		if ((which = Person.findPerson(event.x, event.y)) >= 0) {
			if (personMenu.isShowing()) personMenu.show(false);
			if (whichFolk == which) {
				lastLoc = new Point(event.x,event.y);
			} else {
				if (!fixEgo) {whichFolk = which;}
			}
			lastPersonLoc = new Point(Person.folks[which].location);
			if (fixEgo) doFixEgo(which);
			whichKnot = -1;
			if (editable) supervisor.storeInfo();
			repaint();
			return;	
		} else if (!fixEgo) {whichFolk = -1;}

		if ((which = findMarriage(event.x, event.y)) >= 0) {
			if (personMenu.isShowing()) personMenu.show(false);
			if (whichKnot == which) {
				lastLoc = new Point(event.x,event.y);
			} else {
				if (!fixEgo) {whichKnot = which;}
			}
			if (!fixEgo) {whichFolk = -1;}
			if (editable) supervisor.storeInfo();
			repaint();
			return;	
		} else whichKnot = -1;
		supervisor.clearInfo();
		if (personMenu.isShowing()) {
			personMenu.show(false);
			lastLoc = new Point(-1,-1);
		} else if (editable) {
			lastLoc = new Point(event.x,event.y);
			personMenu.move(event.x+originX,event.y+originY);
			personMenu.deselect(personMenu.getSelectedIndex());
			personMenu.show(true);
			supervisor.storeInfo();
		}
		//}}
	}

	void personMenu_MouseExit(Event event) {

		//{{CONNECTION
		personMenu.hide();
		lastLoc = new Point(-1,-1);
		//}}
	}

	void personMenu_ListSelect(Event event) {
		//{{CONNECTION
		int theIndex=0;
		if (!personMenu.isVisible()) return;
		personMenu.setVisible(false);
		personMenu.select((theIndex = personMenu.getSelectedIndex()));
		// personMenu.hide();
		int i;
		System.out.println("entering personMenu_ListSelect");
		switch(theIndex) {
			case 0: // female
				Person.folks[i=Person.findFreePerson()] = new Person(Person.fem,new Point(lastLoc.x, lastLoc.y));
				Person.folks[i].myId=i+1;
					break;
			case 1: // male
				Person.folks[i=Person.findFreePerson()] = new Person(Person.mem,new Point(lastLoc.x, lastLoc.y));
				Person.folks[i].myId=i+1;
					break;
			case 2: // neuter
				Person.folks[i=Person.findFreePerson()] = new Person(Person.nut,new Point(lastLoc.x, lastLoc.y));
				Person.folks[i].myId=i+1;
					break;
			case 3: // marriage
				Marriage.knots[i=findFreeMarriage()] = new Marriage(new Point(lastLoc.x, lastLoc.y));
				Marriage.knots[i].mid = i+1;
				// need to fill in
					break;
		}
		dirty=true;
		repaint();
		//}}
	}
	
	int findFreeMarriage() {
		for(int i=Marriage.knotIndex;i>=0;i--) {
			if (Marriage.knots[i] == null) return i;
		}
		return ++Marriage.knotIndex;
	}
		
	public int findMarriage(int x, int y) {
		int i=Marriage.knotIndex;
		for(;i>=0;i--) {
			if (Marriage.knots[i] == null) continue;
			if (Marriage.knots[i].bounds().inside(x,y)) return i;
		}
		return -1;
	}
	
	int lastFolk=-1;
	int lastKnot=-1;

    Image theImage=null;
    int jpegs=0;
    //int maxx, maxy,minx,miny;
    
    public void renderChart(boolean doBounds) {
      if (loading) return;
      Rectangle myRect;
      
      if (doBounds) theImage = createImage(bounds().width,bounds().height);
      else {
        Person.resetBoundingBox();
        theImage = createImage(Person.maxx-Person.minx+80, Person.maxy-Person.miny+80);
      }
      Graphics g = theImage.getGraphics();
      
       if (doBounds) g.translate(originX, originY);
       else g.translate(-Person.minx+20, -Person.miny+20);
      
      if (doBounds) myRect = bounds();
      else myRect = new Rectangle(Person.minx-40, Person.miny-40, (Person.maxx-Person.minx)+80, (Person.maxy-Person.miny)+80); // determine bounds
        if (doBounds) myRect.move(-originX,-originY);
      paint0(g, myRect);
      
        FileOutputStream dataOut=null;
        String fname = sFile.getFileName();
        if (fname.equals("")) fname="New";
        if (fname.lastIndexOf('.') != -1) {
          fname=fname.substring(0,fname.lastIndexOf('.'));
        }
        if (whichFolk != -1) {
          fname=fname+"_"+Person.folks[whichFolk].name;
        } else fname=fname+"_NoEgo";
        if (refYear != 0) fname=fname+"_"+refYear;
        int serial=1;

        File outFile;
        outFile = new File(fname+"_"+(serial++)+".jpeg"); // base on ego name and year + bounds
        while (outFile.exists()) {
          outFile = null;
          outFile = new File(fname+"_"+(serial++)+".jpeg");
        }
        try {
          dataOut = new FileOutputStream(outFile);
        } catch(IOException e) {e.printStackTrace();}
        JpegEncoder jpg = new JpegEncoder(theImage,90, dataOut);
        jpg.Compress();
        try {
          dataOut.close();
        } catch(IOException e) {e.printStackTrace();}
        jpg = null;
        outFile=null;
        dataOut = null;
     theImage = null;
    }

    public void paint(Graphics g) {
        if (loading) return;
		g.translate(originX, originY);
		Rectangle myRect = bounds();
		myRect.move(-originX,-originY);

        paint0(g, myRect);
    }
    
    public void paint0(Graphics g, Rectangle myRect) {
		Rectangle theRect;
		int i=Person.folkIndex;
		
		for(;i>=0;i--) {
			if (Person.folks[i] != null) {
				if (i == whichFolk) {
					Person.folks[i].drawSymbol(g,myRect, Color.red);
					if (lastFolk != whichFolk) showInfo(Person.folks[i]);
				}
				else {
					Person.folks[i].drawSymbol(g,myRect);
				}
			}
		}
        
        lastFolk = whichFolk;
		whichHalf = -1;
		for(i=Marriage.knotIndex;i>=0;i--) {
			if (Marriage.knots[i] != null) {
		    theRect = Marriage.knots[i].bounds();
				if (i == whichKnot) {
					Marriage.knots[i].drawSymbol(g,myRect, Color.red);
					if (lastKnot != whichKnot) showInfo(Marriage.knots[i]);
				}
				else {
					Marriage.knots[i].drawSymbol(g,myRect, Color.black);
				}
				Marriage.knots[i].drawLines(g);
				if (selectLine != null) {
					theRect = new Rectangle(theRect);
					theRect.height *= 2;
					theRect.y -= theRect.height/4;
					if(theRect.inside(selectLine.toP.x, selectLine.toP.y)) {
						tiedKnot = i;
						Rectangle a = new Rectangle(theRect.x,theRect.y,theRect.width, 
							theRect.height/2);
						Rectangle b = new Rectangle(theRect.x,theRect.y+theRect.height/2,
							theRect.width, theRect.height/2);
						Color cx = g.getColor();
						g.setColor(Color.green);
						if (a.inside(selectLine.toP.x, selectLine.toP.y) ) {
							g.fillOval(a.x,a.y,a.width,a.height);
							whichHalf = 1;
						} else if (b.inside(selectLine.toP.x, selectLine.toP.y) ) {
							g.setColor(Color.magenta);
							g.fillOval(b.x,b.y,b.width,b.height);
							whichHalf = 2;
						}
						g.setColor(cx);
					}
				}
			}
		}
		lastKnot = whichKnot;
		if (selectLine != null) selectLine.paint(g);
		else if (whichFolk == -1 && whichKnot == -1) clearInfo();
	}

	public void showInfo(Person p) {
		if (!loading) supervisor.showInfo(p);
	}
	public void showInfo(Marriage p) {
		if (!loading) supervisor.showInfo(p);
	}

	public void clearInfo() {
		if (!loading) supervisor.clearInfo();
	}

	void KinshipEditor_MouseDrag(Event event) {
     // super.handleEvent(event);
      if (!editable || fixEgo) return; // can't edit;
		//{{CONNECTION
		if (event.shiftDown()) {
			if (whichKnot != -1) {
				int dx = Marriage.knots[whichKnot].location.x - event.x+10;
				int dy = Marriage.knots[whichKnot].location.y - event.y+10;
				Marriage.knots[whichKnot].deltaMove(dx,dy);
				whichFolk = -1;
				dirty=true;
				repaint();
			} else if (whichFolk != -1) {
				selectLine = new Line(new Point(Person.folks[whichFolk].location.x+10, Person.folks[whichFolk].location.y+10),
					new Point(event.x, event.y));
				repaint();
			}
		
		} else if (event.metaDown()) {
			if (whichKnot != -1) {
				int dx = Marriage.knots[whichKnot].location.x - event.x+10;
				int dy = Marriage.knots[whichKnot].location.y - event.y+10;
				Marriage.knots[whichKnot].lineageDeltaMove(dx,dy);
				whichFolk = -1;
				dirty=true;
				repaint();
			} else if (whichFolk != -1) {
				selectLine = new Line(new Point(Person.folks[whichFolk].location.x+10, Person.folks[whichFolk].location.y+10), new Point(event.x, event.y));
				repaint();
			}
		
		} else{
			selectLine = null;
			if (whichFolk != -1) {
				Person.folks[whichFolk].location.x = event.x-10;
				Person.folks[whichFolk].location.y = event.y-10;
				whichKnot = -1;
				dirty=true;
//				selectLine = new Line(new Point(Person.folks[whichFolk].location.x+10, Person.folks[whichFolk].location.y+10),
//									  new Point(event.x, event.y));
				selectLine = new Line(lastPersonLoc, new Point(event.x, event.y));
				repaint();
			} else if (whichKnot != -1) {
				Marriage.knots[whichKnot].location.x = event.x-10;
				Marriage.knots[whichKnot].location.y = event.y-10;
				dirty=true;
				repaint();
            } else {
             // super.handleEvent(event);
            }
        }
	}
	
	void KinshipEditor_MouseUp(Event event) {
		if (!editable) return;
	
		if (event.shiftDown() || event.metaDown() || !event.controlDown()) {
			if (selectLine != null && whichHalf > 0 && whichFolk > -1) {
				Rectangle theRect = new Rectangle(Marriage.knots[tiedKnot].location.x, 
												  Marriage.knots[tiedKnot].location.y,20,20);
				theRect = new Rectangle(Marriage.knots[tiedKnot].bounds());
				theRect.height *=2;
				theRect.y -= theRect.height/4;
				if(theRect.inside(selectLine.toP.x, selectLine.toP.y)) {
					Rectangle a = new Rectangle(theRect.x,theRect.y,theRect.width, 
												theRect.height/2);
					Rectangle b = new Rectangle(theRect.x,theRect.y+theRect.height/2,
												theRect.width, theRect.height/2);
					Marriage mx = Marriage.knots[tiedKnot];
					Person px = Person.folks[whichFolk];
					if (a.inside(selectLine.toP.x, selectLine.toP.y) ) {
						if (!mx.isSpouse(px)) {
							if (mx.isSib(px)) {
								mx.delSib(px);
								if (lastPersonLoc.y > mx.location.y) lastPersonLoc.y = mx.location.y + (mx.location.y - lastPersonLoc.y);
							}
							mx.addSpouse(px);
							px.setLocation(lastPersonLoc);
							dirty=true;
						} else {
							mx.delSpouse(px);
							px.setLocation(lastPersonLoc);
							dirty=true;
						}
					} else if (b.inside(selectLine.toP.x, selectLine.toP.y) ) {
						if (!mx.isSib(px)) {
							if (mx.isSpouse(px)) {
								mx.delSpouse(px);
								if (lastPersonLoc.y < mx.location.y) lastPersonLoc.y = mx.location.y + (mx.location.y - lastPersonLoc.y);
							}
							mx.addSib(px);
							px.setLocation(lastPersonLoc);
							dirty=true;
						} else {
							mx.delSib(px);
							px.setLocation(lastPersonLoc);
							dirty=true;
						}
					}
				}
			}		
			whichHalf = -1;
			tiedKnot = -1;
			selectLine = null;
			repaint();
		} else if (event.controlDown()) {
			int which=-1;
			if ((which = Person.findPerson(event.x, event.y)) >= 0) {
				if (whichFolk == which) {
					lastLoc = new Point(event.x,event.y);
				} else {
					whichFolk = which;
				}
				Person.folks[whichFolk].delPerson();
				Person.folks[whichFolk] = null;
				//	folks[whichFolk].location = new Point(-100,-100);
				whichFolk = -1;
				dirty=true;
				repaint();
				return;	
			} else whichFolk = -1;
			
			if ((which = findMarriage(event.x, event.y)) >= 0) {
				if (whichKnot == which) {
					lastLoc = new Point(event.x,event.y);
				} else {
					whichKnot = which;
				}
				Marriage.knots[whichKnot].delMarriage();
				Marriage.knots[whichKnot] = null;
				// knots[whichKnot].location = new Point(-100,-100);;
				whichKnot = -1;
				dirty=true;
				repaint();
				return;	
			} else whichKnot = -1;
			
		}
		//}}
	}
	
	
	void xKinshipEditor_MouseUp(Event event) {
		if (!editable) return;
		//{{CONNECTION
		if (event.shiftDown() || event.metaDown()) {
			if (selectLine != null && whichHalf > 0 && whichFolk > -1) {
				Rectangle theRect = new Rectangle(Marriage.knots[tiedKnot].location.x, 
						Marriage.knots[tiedKnot].location.y,20,20);
				theRect = new Rectangle(Marriage.knots[tiedKnot].bounds());
				theRect.height *=2;
				theRect.y -= theRect.height/4;
				if(theRect.inside(selectLine.toP.x, selectLine.toP.y)) {
					Rectangle a = new Rectangle(theRect.x,theRect.y,theRect.width, 
						theRect.height/2);
					Rectangle b = new Rectangle(theRect.x,theRect.y+theRect.height/2,
						theRect.width, theRect.height/2);
					if (a.inside(selectLine.toP.x, selectLine.toP.y) ) {
						if (event.shiftDown()) {
							Marriage.knots[tiedKnot].addSpouse(Person.folks[whichFolk]);
							dirty=true;
						} else {
							Marriage.knots[tiedKnot].delSpouse(Person.folks[whichFolk]);
							dirty=true;
						}
					} else if (b.inside(selectLine.toP.x, selectLine.toP.y) ) {
						if (event.shiftDown()) {
							Marriage.knots[tiedKnot].addSib(Person.folks[whichFolk]);
							dirty=true;
						} else {
							Marriage.knots[tiedKnot].delSib(Person.folks[whichFolk]);
							dirty=true;
						}
					}
				}
			}		
			whichHalf = -1;
			tiedKnot = -1;
			selectLine = null;
			repaint();
		} else if (event.controlDown()) {
			int which=-1;
			if ((which = Person.findPerson(event.x, event.y)) >= 0) {
				if (whichFolk == which) {
					lastLoc = new Point(event.x,event.y);
				} else {
					whichFolk = which;
				}
				Person.folks[whichFolk].delPerson();
				Person.folks[whichFolk] = null;
			//	folks[whichFolk].location = new Point(-100,-100);
				whichFolk = -1;
				dirty=true;
				repaint();
				return;	
			} else whichFolk = -1;
	
			if ((which = findMarriage(event.x, event.y)) >= 0) {
				if (whichKnot == which) {
					lastLoc = new Point(event.x,event.y);
			} else {
					whichKnot = which;
				}
				Marriage.knots[whichKnot].delMarriage();
				Marriage.knots[whichKnot] = null;
				// knots[whichKnot].location = new Point(-100,-100);;
				whichKnot = -1;
				dirty=true;
				repaint();
				return;	
			} else whichKnot = -1;

		}
		//}}
	}

	void KinEditPanel_KeyPress(Event event) {
		// to do: place event handler code here.
	}

	void KinEditPanel_KeyRelease(Event event) {
		// to do: place event handler code here.
	}
	
	XFile sFile = null;
	
	public void saveAsFile() {
		sFile = new XFile();
		if (sFile.Choose(XFile.WRITE))
			saveFile();
		else return;
	}

	public void saveFile() {
		// if (dirty == false) return;
		if (sFile == null) {
			saveAsFile();
			return;
		}
		PrintFormat pf = new PrintFormat(true);
		sFile.Open(XFile.WRITE);

	//	pf.printf("<!DOCTYPE kindata SYSTEM \"kinedit.dtd\">"+Eol+Eol);
		pf.printf("<?xml version=\"1.0\"?>"+Eol);
		pf.printf("<!DOCTYPE kindata>"+Eol+Eol);
		pf.printf("<!--  Kinship Editor Save File - Do not edit by hand!  -->"+Eol+Eol);
		pf.printf("<kindata>"+Eol);
		
	// write Person and Marriage data
		
		Person.personsToXML(pf);
		pf.printf(Eol);
		Marriage.unionsToXML(pf);
		pf.printf(Eol);
		
	// Write parameter data	
	
		pf.printf("<parameters>"+Eol);
		pf.printf("  <origin><x>%d</x><y>%d</y></origin>"+Eol, originX);
		pf.printF(originY);
		pf.printf("  <ego>%d</ego>"+Eol, whichFolk+1);
		pf.printf("  <marriage>%d</marriage>"+Eol, whichKnot+1);
		pf.printf("  <label>%d</label>"+Eol, doLabel);
		pf.printf("  <beginyear>%s</beginyear>"+Eol, supervisor.beginYear.getText());
		
		pf.printf("  <endyear>%s</endyear>"+Eol, supervisor.endYear.getText());
		
		if (editable) pf.printf("  <editable>true</editable>"+Eol);
		else pf.printf("  <editable>false</editable>"+Eol);
		if (fixEgo) pf.printf("  <egofixed>true</egofixed>"+Eol);
		else pf.printf("  <egofixed>false</egofixed>"+Eol);
		pf.printf("</parameters>"+Eol);
		pf.printf("</kindata>"+Eol);
		
		sFile.WriteBytes(pf.toString());
		sFile.Close();
		dirty = false;
	}
	
       public String fixXML(String in) {
            StringBuffer sb = new StringBuffer();
            for (int i=0;i< in.length();i++) {
                char c = in.charAt(i);
                switch (c) {
                    case '&' : sb.append("&amp;");
                                break;
                     case '\'' : sb.append("&apos;");
                                break;
                   default:	sb.append(c);
                }
            }
            return "";
        }
        
	public synchronized void deleteAll() {
		
		
		if (dirty && !doWantToSave()) return; // cancelled
		
		whichFolk = -1;
		whichKnot = -1;
		Marriage.knotIndex = -1;
		Person.folkIndex = -1;
		lastFolk = -1;
		lastKnot = -1;
		dirty=false;
		clearInfo();
		supervisor.resetScroll();
		originX = 0;
		originY = 0;
		repaint();
	}

	boolean doWantToSave() {
		Frame y = new Frame();
		y.reshape(40,40,480,350);
		DoYouWantToSave sdiag = new DoYouWantToSave(y, "Do you want to save?", true);	
		sdiag.setFont(new Font("Serif", Font.BOLD, 12));
		y = null;
		sdiag.show();
		sdiag.toFront();
		while (sdiag.getResult() == DoYouWantToSave.STASIS);
		int x = sdiag.getResult();
		sdiag = null;
		if (x == 0) return false;
		if (x == 1) saveFile();
		dirty = false;
		return true;
	}
	
	boolean loading=false;
	public  boolean loadFile(String fname) { // for XML version
	   		if (dirty == true) {
			   if (!doWantToSave()) {repaint();return false;} // cancelled
			}
			sFile = new XFile(fname);
			sFile.Delimiter = 32;
					//if (sFile.Choose(XFile.READ))
			if (!sFile.Open(XFile.READ))
			   return false; // Cancelled
			return loadFile(sFile);
	}

	public  boolean loadFile() { // for XML version
	   		if (dirty == true) {
			if (!doWantToSave()) {repaint();return false;} // cancelled
		}
		
		sFile = new XFile();
		sFile.Delimiter = 32;
		
		if (sFile.Choose(XFile.READ))
			sFile.Open(XFile.READ);
		else return false; // Cancelled
		return loadFile(sFile);
	}

	public  boolean loadFile(XFile sFile) {
	   String aLine;
	   String[][] ntag;
	   String errMess="None";
	   loading=true;
		dirty = false;
		if (personMenu.isVisible()) personMenu.setVisible(false);
		
		while ((aLine = sFile.ReadLine()) != null){
			if (aLine.trim().toUpperCase().startsWith("<!DOCTYPE KINDATA SYSTEM \"Kinedit.dtd\"")) break;
			else if (aLine.trim().toUpperCase().startsWith("<?XML VERSION=\"1.0\"?>")) {
				if ((aLine = sFile.ReadLine()) != null) if (aLine.trim().toUpperCase().startsWith("<!DOCTYPE KINDATA>")) break;
			}// else if (aLine.indexOf("<H3>People</H3>") != -1) return loadOldFile(sFile); // compatibility with old files
		}
		
		if (aLine == null) {
			errMess="Probably not a KinEditor File: ";
			System.out.println(errMess);
			loading=false;
			return false;
		}
			//System.out.println("Loading");		
		deleteAll();

		Person.folkIndex = -1;
		Marriage.knotIndex = -1;
		setOrigin(0,0);
	
		if (!sFile.readUntilTag("people")) {
			errMess="Couldn't find people... Probably not a KinEditor File: ";
			System.out.println(errMess);
			loading=false;
			sFile.Close();
			return false;
		}
		
		if (!Person.readPeople(sFile)) {
			sFile.Close();
			return loading = false;
		}
		
		ntag = sFile.readTag();
		//System.out.println(" Reading Unions ");
		if (!ntag[0][0].equalsIgnoreCase("unions")) {
			errMess="Found People but no Unions: ";
													Marriage.knotIndex = -1;
												//	sFile.Close();
												//	loading=false;
												//	return true; // let it go
		} else Marriage.readUnions(sFile); // should check for error?
		
		ntag = sFile.readTag();
			//System.out.println("Final Stages");
		if (!ntag[0][0].equalsIgnoreCase("parameters")) sFile.readUntilTag("parameters");
			//System.out.println("Found Parameters");
		editable = true;
		fixEgo = false;

		originX = new Integer(sFile.readTagValue("x")).intValue();
		originY = new Integer(sFile.readTagValue("y")).intValue();
		whichFolk = new Integer(sFile.readTagValue("ego")).intValue()-1;
		whichKnot = new Integer(sFile.readTagValue("marriage")).intValue()-1;
		doLabel = new Integer(sFile.readTagValue("label")).intValue();
			//System.out.println("Read Params");

		if (doLabel > WHOLE) doLabel=WHOLE;
		Person.doLabel = doLabel;
		setSuperDoLabel(doLabel);
		
		supervisor.beginYear.setText(sFile.readTagValue("beginyear").replace('#',' '));
		supervisor.endYear.setText(sFile.readTagValue("endyear").replace('#',' '));
		supervisor.beginYear_LostFocus(null);
		supervisor.endYear_EnterHit(null);
		
		if (sFile.readTagValue("editable").equalsIgnoreCase("true")) editable=true;
		else editable=false;
		if (sFile.readTagValue("egofixed").equalsIgnoreCase("true")) fixEgo=true;
		else fixEgo=false;
		
		supervisor.editableButton.setState(editable);
		supervisor.setEditable(editable);
		supervisor.egoButton.setState(fixEgo);
		int ox = originX;
		int oy = originY;
			//setOrigin(0,0);
		supervisor.setScroll(-ox, -oy);
			//	this.getGraphics().translate(ox,oy);
		supervisor.Scrollbar_Scroll(null);
		loading=false;
		sFile.Close();
		repaint();
			//System.out.println("Done");

		return true;
	}
}
