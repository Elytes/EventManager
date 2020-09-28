package com.eventmanager;

/**
 * Interface that represent a listening class that listen to events
 * 
 * @author Elytes
 * 
 */
public interface EventListener
{
	/**
	 * Method that allow you to register this object to be able to listen for thrown events
	 * This method create an {@link EventCaller} for every methods in this object that contains the {@link EventListening} tag and which got an Event related class as first parameter
	 */
	public default void registerEvents()
	{
		EventManager.getEventManager().register(this);
	}
	
	/**
	 * Method that allow you to unregister all {@link EventCaller} related to this object, so your methods wont receive thrown events anymore
	 */
	public default void unregisterEvents()
	{
		EventManager.getEventManager().unregister(this);
	}
}
