package com.eventmanager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Class that manage registering, unregistering and call of events
 * 
 * @author Elytes
 *
 */
public class EventManager
{
	/**
	 * Global instance of this class
	 */
	protected static EventManager _eventManager;
	/**
	 * HashMap that permit to retrieve all {@link EventCaller} from an class that extends Event and an EventHandler object
	 */
	protected HashMap<Class<? extends Event>, HashMap<EventListener, ArrayList<EventCaller>>> _eventHandlers = new HashMap<Class<? extends Event>, HashMap<EventListener, ArrayList<EventCaller>>>();
	
	/**
	 * Method that instantiate an event type if it's not already registered in {@link #_eventHandlers}
	 * 
	 * @param event the event to instantiate
	 */
	public void instantiateEvent(Class<? extends Event> event)
	{
		if(_eventHandlers.get(event) == null)
		{
			_eventHandlers.put(event, new HashMap<EventListener, ArrayList<EventCaller>>());
		}
	}
	
	/**
	 * Method that allow you to unregister all {@link EventCaller} related to an {@link EventListener}, so methods of this {@link EventListener} wont receive thrown events anymore
	 * 
	 * @param eventHandler the EventHandler to unregister
	 */
	public void unregister(EventListener eventHandler)
	{
		for(Class<? extends Event> event : _eventHandlers.keySet())
		{
			if(_eventHandlers.get(event) != null)
			{
				_eventHandlers.get(event).remove(eventHandler);
			}
		}
	}
	
	/**
	 * Method that allow you to register an EventHandler to be able to listen for thrown events
	 * This method create {@link EventCaller} for every methods in the EventHandler that doesn't contains the {@link EventListening} tag and which got an Event related class
	 * 
	 * @param eventHandler the EventHandler to register
	 */
	@SuppressWarnings("unchecked")
	public void register(EventListener eventHandler)
	{
		// For each method in this EventHandler class
		for(Method method : eventHandler.getClass().getMethods())
		{
			// If parameters count of this method is == 1
			if(method.getParameterCount() == 1)
			{
				// If the first parameter of this method is related to Event class
				if(Event.class.isAssignableFrom(method.getParameterTypes()[0]))
				{
					// If this method contains EventListening tag
					if(method.isAnnotationPresent(EventListening.class))
					{
						// Getting event type of the first parameter
						Class<? extends Event> methodEventType = (Class<? extends Event>) method.getParameterTypes()[0];
						// Registering event for the event methodEventType and this the eventHandler
						registerEvent
						(
							methodEventType,
							eventHandler,
							new EventCaller()
							{
								@Override
								public void call(EventListener eventHandler, Event event)
								{
									try
									{
										// We call the method with eventHandler as instance and event as the event thrown
										method.invoke(eventHandler, event);
									}
									catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
									{
										e.printStackTrace();
									}
								}

								@Override
								public int getEventPriority()
								{
									// Return the priority value of EventPriority annotation if present, else 0
									return method.isAnnotationPresent(EventPriority.class) ? method.getAnnotation(EventPriority.class).priority() : 0;
								}
							}
						);
					}
				}
			}
		}
	}
	
	/**
	 * Method that allow you to register an event and use your own EventCaller
	 * 
	 * @param event type of {@link Event} to listen
	 * @param eventHandler instance of {@link EventListener}
	 * @param eventCaller {@link EventCaller} to call when an event of type defined by event parameter is thrown
	 */
	public void registerEvent(Class<? extends Event> event, EventListener eventHandler, EventCaller eventCaller)
	{
		instantiateEvent(event);
		addEventCaller(event, eventHandler, eventCaller);
		sortEventCallers(event, eventHandler);
	}
	
	/**
	 * Method that sort {@link EventCaller} of a specific event and eventHandler
	 * 
	 * @param event type of {@link Event}
	 * @param eventHandler instance of the {@link EventListener}
	 */
	protected void sortEventCallers(Class<? extends Event> event, EventListener eventHandler)
	{
		_eventHandlers.get(event).get(eventHandler).sort
		(
			new Comparator<EventCaller>()
			{
				@Override
				public int compare(EventCaller eventCaller1, EventCaller eventCaller2)
				{
					int retour = 0;
					// If priority of eventCaller1 > priority of eventCaller2
					if(eventCaller1.getEventPriority() > eventCaller2.getEventPriority())
					{
						// Lower the index of this EventCaller
						retour = -1;
					}
					else if(eventCaller1.getEventPriority() < eventCaller2.getEventPriority())
					{
						// Higher the index of this EventCaller
						retour = 1;
					}
					return retour;
				}		
			}
		);
	}
	
	/**
	 * Method that register an EventCaller by it's event type and it's {@link EventListener}
	 * 
	 * @param event type of {@link Event} to listen
	 * @param eventHandler the instance of {@link EventListener}
	 * @param eventCaller the {@link EventCaller} to register
	 */
	protected void addEventCaller(Class<? extends Event> event, EventListener eventHandler, EventCaller eventCaller)
	{
		if(_eventHandlers.get(event).get(eventHandler) == null)
		{
			_eventHandlers.get(event).put
			(
				eventHandler, 
				new ArrayList<EventCaller>()
			);
		}
		_eventHandlers.get(event).get(eventHandler).add(eventCaller);
	}
	
	/**
	 * Call an {@link Event} by it's object
	 * 
	 * @param event The {@link Event} that'll be fired
	 */
	public void callEvent(Event event)
	{
		instantiateEvent(event.getClass());
		for(EventListener eventHandler : _eventHandlers.get(event.getClass()).keySet())
		{
			for(EventCaller eventCaller : _eventHandlers.get(event.getClass()).get(eventHandler))
			{
				eventCaller.call(eventHandler, event);
			}
		}
	}
	
	/**
	 * Get global instance of {@link EventManager}
	 * 
	 * @return global instance of {@link EventManager}
	 */
	public static EventManager getEventManager()
	{
		if(_eventManager == null)
		{
			_eventManager = new EventManager();
		}
		return _eventManager;
	}
}
