package com.prom.suppandmedschedular.db.classes;

public class BaseSteroid {

	private int id;
	private String name;
	private float hwzInvasion;
	private float hwzEvasion;
	private float kInf;
	private float kEv;
	private float c0;
    
    public BaseSteroid()
    {
    	
    }
    
    public BaseSteroid(int id, String name)
    {
    	this.id = id;
    	this.name = name;
    }
    
    public BaseSteroid(int id, String name, float hwzInvasion, float hwzEvasion, float kInf, float kEv, float c0)
    {
    	this.id = id;
    	this.name = name;
    	this.hwzInvasion = hwzInvasion;
    	this.hwzEvasion = hwzEvasion;
    	this.kInf = kInf;
    	this.kEv = kEv;
    	this.c0 = c0;
    }
    
    public String toString()
    {
    	return name;
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getHwzInvasion() {
		return hwzInvasion;
	}

	public void setHwzInvasion(float hwzInvasion) {
		this.hwzInvasion = hwzInvasion;
	}

	public float getHwzEvasion() {
		return hwzEvasion;
	}

	public void setHwzEvasion(float hwzEvasion) {
		this.hwzEvasion = hwzEvasion;
	}

	public float getkInf() {
		return kInf;
	}

	public void setkInf(float kInf) {
		this.kInf = kInf;
	}

	public float getkEv() {
		return kEv;
	}

	public void setkEv(float kEv) {
		this.kEv = kEv;
	}

	public float getC0() {
		return c0;
	}

	public void setC0(float c0) {
		this.c0 = c0;
	}
}
