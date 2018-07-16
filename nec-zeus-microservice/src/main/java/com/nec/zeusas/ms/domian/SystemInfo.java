package com.nec.zeusas.ms.domian;

import java.io.File;
import java.io.Serializable;

public class SystemInfo implements Serializable {
	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	private int availableProcessors;
	private long freeMemory;
	private long maxMemory;
	private long totalMemory;
	private long totalSpace;
	private long usableSpace;

	
	public SystemInfo(){
	}
	
	
	public int getAvailableProcessors() {
		return availableProcessors;
	}

	public void setAvailableProcessors(int availableProcessors) {
		this.availableProcessors = availableProcessors;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public long getMaxMemory() {
		return maxMemory;
	}

	public void setMaxMemory(long maxMemory) {
		this.maxMemory = maxMemory;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public long getTotalSpace() {
		return totalSpace;
	}

	public void setTotalSpace(long totalSpace) {
		this.totalSpace = totalSpace;
	}

	public long getUsableSpace() {
		return usableSpace;
	}

	public void setUsableSpace(long usableSpace) {
		this.usableSpace = usableSpace;
	}

}
