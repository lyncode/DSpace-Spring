/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.services.api.configuration;

import org.dspace.services.api.configuration.event.ChangeHandler;
import org.dspace.services.api.configuration.reference.PropertyReference;


/**
 * 
 * @author João Melo
 */
public interface ConfigurationService {

    
    /**
     * Convenience method - get a configuration property (setting) from 
     * the system.
     * 
     * @param name the property name
     * @return the property value OR null if none is found
     */
    public String getProperty(PropertyReference reference);
    
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
    public <T> T getProperty(PropertyReference reference, Class<T> type);

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
    public <T> T getProperty(PropertyReference reference, Class<T> type, T defaultValue);

    
    /**
     * Sets a property changed handler to deal with changed properties
     * 
     * @param name
     * @param handler
     * @return
     */
    public boolean addWatchHandler (ChangeHandler handler, PropertyReference... references);
    
    /**
     * Removes an handler
     * 
     * @param handler
     * @return
     */
    public boolean removeWatchHandler (ChangeHandler handler);
    
    /**
     * Removes an handler
     * 
     * @param handler
     * @return
     */
    public boolean removeWatchHandler (ChangeHandler handler, PropertyReference... references);
    
    
    /**
     * Adding a property
     * 
     * @param name
     * @param value
     * @return
     */
    public boolean addProperty (PropertyReference reference, Object value);
    
    /**
     * Setting the property
     * 
     * @param name
     * @param value
     * @return
     */
    public boolean setProperty (PropertyReference name, Object value);
    
    /**
     * Checks if the DSpace is installed
     * 
     * @return
     */
    public boolean isInstalled ();
}
