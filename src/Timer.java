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

public class Timer implements Runnable, java.io.Serializable {
	
	protected TimerTarget			target;
    protected boolean				executing;
    protected boolean				isDesignTime;
    protected int					delay;
    public boolean paused=false;
    protected int					repeatFrom;
    protected int					repeatTo;
    protected int					repeatIndex;
    transient protected Thread	thread;

	
	public Timer()
    {
		this(1000, null);
    }
	
	public Timer(int d, TimerTarget t)
    {
        delay			= d;
        target			= t;
        boolean 		executing=false;
        repeatFrom = -1;
        repeatTo = -1;
        repeatIndex = -1;
        isDesignTime	= java.beans.Beans.isDesignTime();
        if(!isDesignTime)
	        thread = new Thread(this);
    }

	public void setDelay(int d) {
		delay = d;
	}
	public void setTimerTarget(TimerTarget t) {
		target = t;
	}
	
	public void setFromTo(int f, int t) {
		repeatFrom = f;
		repeatTo = t;
	}

	public void start() {
		paused = false;
		resume();
	}

 	synchronized public void pause()
    {
    	if(thread.isAlive())
    		thread.suspend();

    	if(thread.isAlive())
    		thread.suspend();
    	executing = false;
    	paused = true;
    }
   
    synchronized public void resume()
    {
    	if(!executing)
    	{
	    	executing = true;
	    	paused = false;
	    	if(thread.isAlive())
			    thread.resume();
			else
				thread.start();
		}
	}

    synchronized public void start(int f, int t)
    {
	    setFromTo(f,t);
	    paused = false;
		executing=true;
		start();
    }
	synchronized public void restart()
	{
		stop();
		executing=true;
		paused = false;
		start();
	}
    synchronized public void stop()
    {
	    if(!isDesignTime && thread.isAlive())
	    {
	    	thread.interrupt();
	    	executing=false;
	    }
    }

	public boolean isExecuting() {
		return executing;
	}
    /**
     * The thread body.  This method is called by the Java virtual machine in response to a
     * start call by the user.
     * @see #start()
     * @see #start(int)
     * @see #start(boolean)
     * @see #start(int, boolean)
     * @see #stop
     */
    public void run()
    {
		if(!executing)
		{
			thread.suspend();
		}
		while(executing)
		{
			do
			{
				try
				{
					thread.sleep(delay);
					if (executing)
					{
						doFromTo();
					}
				}
				catch (InterruptedException e) { return; }
			}
			while (executing);
			if(!executing)
			{
				thread.suspend();
			}		
		}
	}

	public void doFromTo() {
		if (repeatIndex == -1) repeatIndex = repeatFrom;
		if (repeatIndex <= repeatTo) {
			target.doTimerTarget(repeatIndex);
			repeatIndex++;
		} else {
			repeatIndex = -1;
			executing = false;
			target.clearTimerTarget();
		}
		
	}

	public int getIndex() {
		return repeatIndex;
	}

}
