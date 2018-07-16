package com.nec.zeusas.common.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.nec.zeusas.core.entity.IEntity;

@Entity
@Table(name = "core_idgen")
public class IdGen implements IEntity, Cloneable {
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = -5657056499571156083L;

	@Id
	@Column(name = "SEQUENCEID", nullable = false)
	String sequenceId;
	@Column(name = "NAME")
	String name;
	@Column(name = "SUMMARY")
	String summary;
	@Column(name = "IDFORMAT")
	String idFormat;
	@Column(name = "LASTVALUE")
	Long lastValue;
	@Column(name = "START")
	Long start;
	@Column(name = "SIZE")
	int size;
	@Column(name = "STEP")
	int step;
	@Column(name = "RESETCYCLE")
	Integer resetCycle;
	@Column(name = "LASTUPDATE")
	Long lastUpdate;

	public String getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(String sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getIdFormat() {
		return idFormat;
	}

	public void setIdFormat(String idFormat) {
		this.idFormat = idFormat;
	}

	public Long getLastValue() {
		return lastValue;
	}

	public void setLastValue(Long lastValue) {
		this.lastValue = lastValue;
	}

	public Long getStart() {
		return start;
	}

	public void setStart(Long start) {
		this.start = start;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public Long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public int getResetCycle() {
		return resetCycle == null ? 0 : resetCycle.intValue();
	}

	public void setResetCycle(Integer resetCycle) {
		this.resetCycle = resetCycle;
	}

	@Override
	public IdGen clone() {
		IdGen cv = null;
		try {
			cv = (IdGen) super.clone();
			return cv;
		} catch (Exception e) {
			// NOP
		}
		cv = new IdGen();
		cv.idFormat = idFormat;
		cv.lastUpdate = lastUpdate;
		cv.step = step;
		cv.lastValue = lastValue;
		cv.sequenceId = sequenceId;
		cv.size = size;
		cv.name = name;
		cv.summary = summary;
		return cv;
	}
}
