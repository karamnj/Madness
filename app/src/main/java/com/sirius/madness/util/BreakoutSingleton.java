package com.sirius.madness.util;

import java.util.HashMap;

public class BreakoutSingleton
{
	private static BreakoutSingleton instance;

	public HashMap<Integer,String> filterDates;
	public HashMap<Integer,Integer> selfList;
	public HashMap<Integer,Long> selfDateList;
	public Boolean notInitialized = true;
	public int selfItem = 0;

	public static void initInstance()
	{
		if (instance == null)
		{
			// Create the instance
			instance = new BreakoutSingleton();
		}
	}

	public static BreakoutSingleton getInstance()
	{
		// Return the instance
		return instance;
	}
	
	private BreakoutSingleton()
	{
		// Constructor hidden because this is a singleton
	}
	
	public void customSingletonMethod()
	{
		// Custom method
	}
}
