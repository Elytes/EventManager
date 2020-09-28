package com.eventmanager;

/**
 * Interface that describe what method the {@link EventManager} will call when the appropriated event is thrown, and at which priority it'll get called
 * 
 * @author Elytes
 */
public interface EventCaller
{
	/**
	 * Method that describe what happen when being called by {@link EventManager}
	 * 
	 * @param eventHandler instance of EventHandler that is registered as listener of this event
	 * @param event object that represent the thrown event
	 */
	public void call(EventListener eventHandler, Event event);
	
	/**
	 * Method that describe when this {@link EventCaller} should be called
	 * 
	 * @return this {@link EventCaller} priority
	 */
	public default int getEventPriority()
	{
		return 0;
    }
}
