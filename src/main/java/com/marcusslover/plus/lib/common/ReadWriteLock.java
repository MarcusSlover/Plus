package com.marcusslover.plus.lib.common;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Generic ReadWrite Reentrant Lock
 */
public class ReadWriteLock {
	private final java.util.concurrent.locks.ReadWriteLock lock = new ReentrantReadWriteLock();

	public Lock getReadLock() {
		return this.lock.readLock();
	}

	public Lock getWriteLock() {
		return this.lock.writeLock();
	}

	public void readLock() {
		this.lock.readLock().lock();
	}

	public void readUnlock() {
		this.lock.readLock().unlock();
	}

	public void writeLock() {
		this.lock.writeLock().lock();
	}

	public void writeUnlock() {
		this.lock.writeLock().unlock();
	}
}