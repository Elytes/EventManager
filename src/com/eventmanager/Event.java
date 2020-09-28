package com.eventmanager;

/**
 * Class that represent a thrown event 
 * 
 * @author Elytes
 */
public class Event
{
	/**
	 * Variable that store the actual cancelled state of this Event
	 */
	protected boolean _cancelled;
	
	/**
	 * Cancel this event or not, only lower priority {@link EventCaller} will be able to check the new state
	 * @param cancelled new cancelled state of this Event
	 */
	public void setCancelled(boolean cancelled)
	{
		_cancelled = cancelled;
	}
	
	/**
	 * Check if this event is cancelled or not, this event state is relative to previous {@link #setCancelled(boolean)} operations of higher priority {@link EventCaller}
	 * @return cancelled state of this event
	 */
	public boolean isCancelled()
	{
		return _cancelled;
	}
}
