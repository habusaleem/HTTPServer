import java.util.*;

public class ReThread implements Runnable {
	
	protected Runnable target;
	
	public ReThread (Runnable target) {
		this.target = target;
	}
	
	private static Vector threads = new Vector ();
	private ReThread reThread;
	private Thread thread;
	
	private static int id = 0;
	private static synchronized int getID() {return id++;}
	
	public synchronized void start() {
		if ((thread == null) && (reThread == null)) {
			synchronized (threads) {
				if (threads.isEmpty()) {
					thread = new Thread (this, "ReThread-" + getID());
					thread.start();
				} else {
					reThread = (ReThread) threads.lastElement();
					threads.setSize(threads.size() - 1);
					reThread.start0 (this);
				}
			}
		}
	}
	
	protected synchronized void start0 (ReThread reThread) {
		this.reThread = reThread;
		target = reThread.target;
		notify();
	}
	
	public synchronized void interrupt () {
		if ((target != null) && ((thread != null) ^ (reThread != null))) {
			if (thread != null) {
				thread.interrupt();
			} else {
				reThread.interrupt0(this);
			}
		}
	}
	
	protected synchronized void interrupt0(ReThread reThread) {
		if ((target != null) && (reThread == this.reThread)) {
			thread.interrupt();
		}
	}
	
	public void run () {
		while (true) {
			try {
				target.run();
			} catch (RuntimeException ex) {
				ex.printStackTrace();
			}
			waitForTarget();
		}
	}
	
	protected synchronized void waitForTarget () {
		target = null;
		threads.addElement(this);
		while (target == null) {
			try {
				wait();
				
			} catch (InterruptedException ignored){
				
			}
		}
	}

}
