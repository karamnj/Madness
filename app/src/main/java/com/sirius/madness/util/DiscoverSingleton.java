package com.sirius.madness.util;

import java.util.HashMap;

public class DiscoverSingleton
{
	private static DiscoverSingleton instance;

	public HashMap<Integer,String> filterDates;
	public HashMap<Integer,Integer> selfList;
	public HashMap<Integer,Long> selfDateList;
	public HashMap<Integer,String> slot;
	public Boolean notInitialized = true;
	public int selfItem = 0;

	public static void initInstance()
	{
		if (instance == null)
		{
			// Create the instance
			instance = new DiscoverSingleton();
		}
	}

	public static DiscoverSingleton getInstance()
	{
		// Return the instance
		return instance;
	}

	private DiscoverSingleton()
	{
		// Constructor hidden because this is a singleton
	}
	
	public void customSingletonMethod()
	{
		// Custom method
	}
}
