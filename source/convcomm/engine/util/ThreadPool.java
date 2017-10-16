package convcomm.engine.util;

import java.util.LinkedList;

/**
**	@author David Brackeen from Developing Games in Java copyright 2004
**	@version 1.0
**	A thread pool is a group of a limtited number of threads that are used to execute tasks.
**/
public class ThreadPool extends ThreadGroup
{
	private boolean isAlive;
	private LinkedList<Runnable> taskQueue;
	private int threadID;
	private Thread[] threads;
	private static int threadPoolID;
	
	/**
	**	Creates a new ThreadPool.
	**	@param numThreads The number of threads in this pool.
	**/
	public ThreadPool(int numThreads)
	{
		super("ThreadPool-" + (threadPoolID++));
		setDaemon(true);
		
		isAlive = true;
		
		taskQueue = new LinkedList<Runnable>();
		threads = new Thread[numThreads];
		for (int i = 0; i < numThreads; ++i)
		{
			threads[i] = new PooledThread();
			threads[i].start();
		}
	}
	
	/**
	**		Requests a new task to run. This method returns immediately, and the task executes
	**	on the next available idle thrad in this ThreadPool.
	**		<p>Tasks start execution in the order they are received.</p>
	**	@param task The task to run. If null, no action is taken.
	**	@throws IllegalStateException if the ThreadPool is closed.
	**/
	public synchronized void runTask(Runnable task)
	{
		if (!isAlive)
			throw new IllegalStateException();
		if (task != null)
			taskQueue.add(task);
		notify();
	}
	
	protected synchronized Runnable getTask() throws InterruptedException
	{
		while (taskQueue.size() == 0)
		{
			if (!isAlive)
				return null;
			wait();
		}
		return taskQueue.removeFirst();
	}
	
	/**
	**		Closes this ThreadPool and returns immediately. All threads are stopped, and any
	**	waiting tasks are not executed. Once a ThreadPool is closed, no more tasks can be
	**	run on this ThreadPool.
	**/
	public synchronized void close()
	{
		if (isAlive)
		{
			isAlive = false;
			taskQueue.clear();
			interrupt();
		}
	}
	
	/**
	**		Stops all threads and clears the task queue, but allows new tasks to be added.
	**/
	public synchronized void reset()
	{
		System.out.println("reset called.");
		if (isAlive)
		{
			taskQueue.clear();
			for (Thread t : threads)
			{
				System.out.println("\tAttempting to stop thread: " + t);
				if (t != null)
					t.interrupt();
			}
		}
	}
	
	/**
	**		Closes this ThreadPool and waits for all running threads to finish. Any waiting tasks are
	**	executed.
	**/
	public void join()
	{
		// Notify all waiting threads that this ThreaPool is no longer alive
		synchronized (this)
		{
			isAlive = false;
			notifyAll();
		}
		
		// Wait for all threads to finish
		Thread[] threads = new Thread[activeCount()];
		int count = enumerate(threads);
		for (Thread t : threads)
		{
			try
			{
				t.join();
			}
			catch (InterruptedException ie)
			{
				
			}
		}
	}
	
	protected void threadStarted() { }
	
	/**
	**		A PooledThread is a Thread in a ThreadPool group, designed to run tasks.
	**/
	private class PooledThread extends Thread
	{
		public PooledThread()
		{
			super(ThreadPool.this, "PooledThread-" + (threadID++));
		}
		
		public void run()
		{
			while (!isInterrupted())
			{
				Runnable task = null;
				try
				{
					task = getTask();
				}
				catch (InterruptedException ie)
				{
					
				}
				
				if (task == null)
					return;
				
				try
				{
					task.run();
				}
				catch (Throwable t)
				{
					uncaughtException(this, t);
				}
			}
		}
	}//end PooledThread class
}