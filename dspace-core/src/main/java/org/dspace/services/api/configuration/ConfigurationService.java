/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.services.api.configuration;

import org.dspace.configuration.ConfigurationPropertyChangeHandler;


/**
 * 
 * @author João Melo
 */
public interface ConfigurationService {

    /**
     * Get a configuration property (setting) from the system as a 
     * specified type.
     * 
     * @param <T>
     * @param name the property name
     * @param type the type to return the property as
     * @return the property value OR null if none is found
     * @throws UnsupportedOperationException if the type cannot be converted to the requested type
     */
    public <T> T getProperty(String name, Class<T> type);

    /**
     * Get a configuration property (setting) from the system as a 
     * specified type.
     * 
     * @param <T>
     * @param name the property name
     * @param type the type to return the property as
     * @return the property value OR null if none is found
     * @throws UnsupportedOperationException if the type cannot be converted to the requested type
     */
    public <T> T getProperty(String name, Class<T> type, T defaultValue);
    
    /**
     * Convenience method - get a configuration property (setting) from 
     * the system.
     * 
     * @param name the property name
     * @return the property value OR null if none is found
     */
    public String getProperty(String name);

    
    /**
     * Sets a property changed handler to deal with changed properties
     * 
     * @param name
     * @param handler
     * @return
     */
    public boolean addWatchHandler (ConfigurationPropertyChangeHandler handler, String... name);
    
    public boolean removeWatchHandler (ConfigurationPropertyChangeHandler handler);
    
    
    /**
     * Adding a property
     * 
     * @param name
     * @param value
     * @return
     */
    public boolean addProperty (String name, Object value);
    
    /**
     * Setting the property
     * 
     * @param name
     * @param value
     * @return
     */
    public boolean setProperty (String name, Object value);
}
