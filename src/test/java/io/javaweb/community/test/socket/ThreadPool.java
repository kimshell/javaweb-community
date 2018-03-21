package io.javaweb.community.test.socket;

import java.util.LinkedList;

public class ThreadPool extends ThreadGroup{
	
	//线程池id
	private static int ThreadPollID;
	
	//线程池是否关闭
	private boolean isClosed = false;
	//任务队列
	private LinkedList<Runnable> workQueue;
	//线程id
	private int threadID;

	public ThreadPool(int poolSize) {
		//父级构造函数,设置线程池名称
		super("ThreadPool:" + (ThreadPollID ++));
		//设置为守护线程
		setDaemon(true);
		//实例化工作队列
		workQueue = new LinkedList<>();
		//创建并启动工作线程
		for(int i = 0 ; i < poolSize ; i++) {
			new WorkThread().start();
		}
	}
	
	//关闭线程池
	public synchronized void cose() {
		if(!isClosed) {
			isClosed = true;
			workQueue.clear();
			interrupt();
		}
	}
	
	public synchronized void execute(Runnable task) {
		if(isClosed) {
			throw new IllegalStateException();
		}
		if(task != null) {
			workQueue.add(task);
			//唤醒 正在 getTask() 中等待任务的工作线程
			notify();
		}
	}
	
	//从工作队列取出一个任务,工作线程会调用此方法
	protected synchronized Runnable getTask() throws InterruptedException{
		while(workQueue.size() == 0) {
			if(isClosed) {
				return null;
			}
			//没有任务,当前线程暂停
			wait();
		}
		return workQueue.removeFirst();
	}
	
	public void join() {
		synchronized (this) {
			isClosed = true;
			notifyAll();
		}
		Thread[] threads  = new Thread[activeCount()];
		//获取线程组中,所有或者的工作线程
		int count = enumerate(threads);
		for(int i = 0; i < count; i ++) {
			try {
				threads[i].join();
			}catch(InterruptedException exception) {
				
			}
		}
	}
	
	//内部类,工作线程
	private class WorkThread extends Thread {
		
		public WorkThread() {
			super(ThreadPool.this,"WorkThread" + (ThreadPool.this.threadID ++));
		}
		
		@Override
		public void run() {
			//线程未中断
			while(!isInterrupted()) {
				Runnable task = null;
				try {
					task = getTask();
				}catch(InterruptedException exception) {
					
				}
				if (task == null) {
					return;
				}
				try {
					task.run();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}


















