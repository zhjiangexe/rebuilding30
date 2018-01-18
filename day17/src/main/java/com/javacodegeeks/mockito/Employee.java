package com.javacodegeeks.mockito;

import java.util.ArrayList;
import java.util.List;

public class Employee {
	private String firstName;
	private String lastName;
	private int age;
	private EngineerAware engineerAware;
	private List<String> skills;

	public Employee(String firstName, String lastName, int age) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.age = age;
		this.engineerAware = new Dev();
	}

	public int getAge() {
		return age;
	}

	public String getFullName() {
		return getFirstName() + " " + getLastName();
	}

	public String getLastName() {
		return lastName;
	}

	public String getFirstName() {
		return firstName;
	}	
	
	public void setAge(int age) {
		this.age = age;
	}

	public final void finalMoveTo(EngineerAware engineerAware) {
		System.out.println("Employee moves from " + this.engineerAware.getDesignation() + " to " + engineerAware.getDesignation());
		this.engineerAware = engineerAware;
	}
	
	public void moveTo(EngineerAware engineerAware) {
		System.out.println("Employee moves from " + this.engineerAware.getDesignation() + " to " + engineerAware.getDesignation());
		this.engineerAware = engineerAware;
	}
	
	private class Dev implements EngineerAware {
		@Override
		public Engineer getDesignation() {
			return Engineer.DEV;
		}		
	}
	
	public String getSkill(int index) {
		return skills.get(index);
	}
	
	public void addSkill(String skill1, String skill2, String skill3) {
		if (skills == null) {
			skills = new ArrayList<String>(3);
		}
		skills.add(0, skill1);
		skills.add(1, skill2);
		skills.add(2, skill3);
	}
}
