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
import java.util.StringTokenizer;

public class Person extends Object implements ToXML {
	public static int id_no=0;
	public static int size=16; // default size;
	public static String refYear=""; // reference year for drawing genealology

	public static Person [] folks = new Person[1000]; // need to check for upgrading over 1000!!!
	public static int folkIndex = -1;
    public static int maxx=-20000, maxy=-20000,minx=20000,miny=20000;

	int lsize = 0; // if not zero use this one - local to this entity
	int myId = ++id_no;
	public boolean drawn=false; // was this symbol drawn last time?

	String name = "New";
	String comment = "No Comment";
	String yob = "NA";
	String yobq = "9";
	String yod = "NA";
	String yodq = "9";

	public Point location=new Point(0,0);
	Kind sex = null;
	LinkedList marriages=null;
	Marriage parents=null;
	LinkedList parental_unions=null;

	public boolean selected=false;
	
	public static final int NOLABEL=0;
	public static final int FIRST=2;
	public static final int LAST = 3;
	public static final int INITIALS = 1;
	public static final int WHOLE = 4;

	public static int doLabel=NOLABEL;

	Person(Kind gend, Point l) {
		sex = gend;
		setLocation(l);
		name = "Person " + myId;
	}

    public void setLocation(Point p) {
      setLocation(p.x, p.y);
    }
    
    public void setLocation(int x, int y) {
      location.x = x;
      location.y = y;
      if (x > maxx) maxx = x;
      if (x < minx) minx = x;
      if (y > maxy) maxy = y;
      if (y < miny) miny = y;
    }
    
    public void setLocationX(int x) {
      setLocation(x,location.y);
    }
    
    public void setLocationY(int y) {
      setLocation(location.x,y);
    }

    public static void resetBoundingBox() {
      maxx=-20000; maxy=-20000; minx=20000; miny=20000;
      for (int i=0;i<=folkIndex;i++) {
        if (folks[i] != null) {
          int x=folks[i].location.x;
          int y=folks[i].location.y;
          if (x > maxx) maxx = x;
          if (x < minx) minx = x;
          if (y > maxy) maxy = y;
          if (y < miny) miny = y;
        }
      }
    }

    public LinkedList getMarriages() {
		return marriages;
	}
		
	public void setMarriages(LinkedList m) {
		marriages = m;
	}

	
	public void addMarriage(Marriage p) {
		if (marriages == null) marriages = new LinkedList(p);
		else if (!marriages.isHere(p)) marriages.extend(p);
// update marriage
		if (p.getSpouses() == null) p.setSpouses(new LinkedList(this));
		else if (!p.getSpouses().isHere(this)) 
				p.getSpouses().extend(this);
	}

	
	public void delMarriage(Marriage p) {
		if (marriages != null && marriages.isHere(p)) {
			marriages = (LinkedList) marriages.remove(p);
			if (p.getSpouses() != null) 
				p.setSpouses((LinkedList) p.getSpouses().remove(this));
		}
	}
	 
	public void setParents(Marriage p) {
            if (p == null) {
                parents = null;
                return;
            }
            if (parental_unions == null) parental_unions = new LinkedList(p);
            else if (!parental_unions.isHere(p)) parental_unions.extend(p);
		Marriage k = parents;
		parents = p;
		if (k != null) {
			// k.delSib(this); // do nothing for now
		}
		if (p != null) p.addSib(this);
	}
	
	public void delParents(Marriage p) {
            Marriage k = parents;
            if (parental_unions != null && parental_unions.isHere(p)) {
                parental_unions = (LinkedList) parental_unions.remove(p);
                if (p.sibset != null) 
                    p.delSib(this);
            }
            if (parental_unions.size() == 0) parents = null;
            else parents = (Marriage) parental_unions.last().getValue();
	}
	
	public Marriage getParents() {
		return parents;
	}
	
	public void delPerson() {
		Marriage k = parents;
		//if (k != null) k.delSib(this);
		parents = null;
 		LinkedList pu = parental_unions;
		while (pu != null) {
			k = (Marriage) pu.getValue();
                        if (k != null) k.delSib(this);
			pu =  pu.getNext();
		}
                parental_unions=null;
		LinkedList m = marriages;
		while (m != null) {
			Marriage x = (Marriage) m.getValue();
			delMarriage(x);
			m =  m.getNext();
		}
                marriages=null;
	}
	
	public void marryTo(Person p) {
		Marriage m;
		m = new Marriage(null);
		m.addSpouse(p);
		m.addSpouse(this);
		p.addMarriage(m);
		this.addMarriage(m);
	}


	public int getSize() {
		if (lsize > 0) return lsize;
		else return size;
	}

	public void setSize(int x) {
		lsize = x;
	}

	public Rectangle bounds() {
		return new Rectangle(location.x,location.y,getSize(),getSize());
	}
	
	public Point bottomHinge() {
		return new Point(location.x+ getSize()/2,location.y+getSize());
	}
	
	public Point topHinge() {
		return new Point(location.x+ getSize()/2,location.y);
	}
	
	public void drawSymbol(Graphics g, Rectangle pbounds, Color c) {
		Color x = g.getColor();
		g.setColor(c);
		drawSymbol(g,pbounds);
		g.setColor(x);
	}
	public boolean hasBegun() {
		if (!yob.equals("NA") && !refYear.equals("")) 
			return (yob.compareTo(refYear) <= 0);
		else return true;
	}
	public boolean hasEnded() {
		if (!yod.equals("NA") && !refYear.equals("")) 
			return(yod.compareTo(refYear) <= 0);
		else return false;
	}
	
	
	public void drawLabel(Graphics g, Rectangle theBounds) {
		String out, out1;
		StringTokenizer st = new StringTokenizer(name);
		if (doLabel == NOLABEL) return;
		if (doLabel == FIRST) {
			if (st.hasMoreTokens()) {
				out = st.nextToken()+ " ";
				while (st.hasMoreTokens()) {
					out = out + st.nextToken().substring(0,1);
				}
			} else out = "None";
		} else if (doLabel == LAST) {
			if (st.hasMoreTokens()) {
				out1 = st.nextToken();
				out = "";
				while (st.hasMoreTokens()) {
					out = out + out1.substring(0,1);
					out1 =  st.nextToken();
				}
				out = out + " " + out1;
			} else out = "None";
		} else if (doLabel == INITIALS) {
			if (st.hasMoreTokens()) {
				out = "";
				while (st.hasMoreTokens()) {
					out = out+ st.nextToken().substring(0,1);
				}
			} else out = "None";
		} else if (doLabel == WHOLE) {
			out = name;
			if (out.equals("")) out = "None";
		} else return;
		int w = g.getFontMetrics().stringWidth(out)/2;
		int x = theBounds.x + theBounds.width/2 - w;
		int y = theBounds.y + theBounds.height + 16;
		g.drawString(out,x,y);
	};
	
	public void drawSymbol(Graphics g, Rectangle pbounds) {
		Rectangle myBounds = bounds();
		// myBounds.translate(offset.x,offset.y);
		drawn=false;

		if (myBounds.intersects(pbounds)) {
			if (hasEnded()) {
				sex.symbol.drawEndSymbol(g,myBounds);
				drawLabel(g,myBounds);
				drawn = true;
			} else if (hasBegun()) {
				sex.symbol.drawSymbol(g,myBounds);
				drawLabel(g,myBounds);
				drawn=true;
			}
		}
	}
	
	public void paint(Graphics g) {
		if (selected) sex.symbol.drawSymbol(g,bounds(), Color.red);
		else sex.symbol.drawSymbol(g,bounds());	
	}
	
	public String toXML() {
		PrintFormat pf = new PrintFormat();
		personToXML(pf);
		return pf.toString();
	}

	public static String allToXML() {
		PrintFormat pf = new PrintFormat();
		personsToXML(pf);
		return pf.toString();
	}

	public void personToXML(PrintFormat pf) {
		pf.printf("  <person>"+XFile.Eol+"    <name>%s</name><id>%d</id><sex>%s</sex>"+XFile.Eol, name);
		pf.printF(myId); // id
		if (sex instanceof Female) pf.printF("Female");
		else if (sex instanceof Male) pf.printF("Male");
		else pf.printF("Neuter");

		if (yob.equals("")) {
			yob = "NA";
		}
		if (yod.equals("")) {
			yod = "NA";
		}
		pf.printf("    <stats><born q=\"%s\">%s</born><died q=\"%s\">%s</died></stats>"+XFile.Eol,
		yobq);
		pf.printF(yob.replace(' ','#'));
		pf.printF(yodq);
		pf.printF(yod.replace(' ','#'));

		pf.printf("    <location><x>%d</x><y>%d</y></location>"+XFile.Eol,location.x);
		pf.printF(location.y);

		if (comment.equals("")) {
			comment = "None";
		}

		pf.printf("    <comment>%s</comment>"+XFile.Eol+"  </person>"+XFile.Eol+XFile.Eol,comment);
	}

	public static void personsToXML(PrintFormat pf) {
		pf.printf("<people>"+XFile.Eol);
		
		for(int i = 0;i <= folkIndex;i++) {
			if (folks[i] != null) folks[i].personToXML(pf);
		}
		pf.printf("</people>"+XFile.Eol);
	}
	
	static Female fem = new Female();
	static Male	mem = new Male();
	static Neuter	nut = new Neuter();

	public static Person readXML(XFile sFile) {
		String name, sex, born, died, comment;
		String bornqual="9", diedqual="9";
		int pid,locx,locy;
		Person retp=null;
		
		name = sFile.readTagValue("name");
		pid = new Integer(sFile.readTagValue("id")).intValue();
		sex = sFile.readTagValue("sex");
		born = sFile.readTagValue("born").replace('#',' ');
		if (sFile.attributes.length > 1) bornqual = sFile.attributes[1][1];
		died = sFile.readTagValue("died").replace('#',' ');
		if (sFile.attributes.length > 1) diedqual = sFile.attributes[1][1];
		locx = new Integer(sFile.readTagValue("x")).intValue();
		locy = new Integer(sFile.readTagValue("y")).intValue();
		comment = sFile.readTagValue("comment");
		sFile.readUntilTag("/person");
		if (sex.equalsIgnoreCase("Female")) {
			retp = new Person(fem, new Point(locx,locy));
		} else if (sex.equalsIgnoreCase("Male")) {
			retp = new Person(mem, new Point(locx,locy));
		} else if (sex.equalsIgnoreCase("Neuter")) {
			retp = new Person(nut, new Point(locx,locy));
		} else {
			System.out.println("Bad sex: " + sex);
			return null;
		}
		if (retp == null) {
			System.out.println("Odd error in Person.readXML: " + sex);
			return null;
		}
		retp.name = name;
		retp.myId = pid;
		retp.comment = comment;
		retp.yob = born;
		retp.yod = died;
		retp.yobq = bornqual;
		retp.yodq = diedqual;
		Person.id_no = pid; // Check this out!!!
		folkIndex++;
		while (retp.myId-1 > folkIndex) {
			folks[folkIndex++] = null;
		}
		folks[folkIndex] = retp;
		//System.out.println("pid="+pid+" folks["+(pid-1)+"] = "+folks[pid-1]);
		return retp;
	}
	
	public static boolean readPeople(XFile sFile) {
		String[][] ntag;
		for(;;) {
			ntag = sFile.readTag();
			if (ntag[0][0].equalsIgnoreCase("/people")) break;
			if (!ntag[0][0].equalsIgnoreCase("person"))
				sFile.readUntilTag("person");
			Person qp = readXML(sFile);
			if (qp == null) {
				return false;
			}
		}
		return true;
	}
		
	static public int findFreePerson() {
		for(int i=folkIndex;i>=0;i--) {
			if (folks[i] == null) return i;
		}
		return ++folkIndex;
	}

	static public int findPerson(int x, int y) {
		int i=folkIndex;
		for(;i>=0;i--) {
			if (folks[i] == null) continue;
			if (folks[i].bounds().inside(x,y)) return i;
		}
		return -1;
	}
}
