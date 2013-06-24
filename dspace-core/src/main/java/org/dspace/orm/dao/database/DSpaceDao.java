/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.orm.dao.database;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dspace.orm.dao.api.IDSpaceDao;
import org.dspace.orm.entity.DSpaceObject;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Miguel Pinto <mpinto@lyncode.com>
 * @version $Revision$
 */

public abstract class DSpaceDao<T extends DSpaceObject> implements IDSpaceDao<T> {
	private static Logger log = LogManager.getLogger(DSpaceDao.class);
	
	private Class<T> clazz;
	@Autowired(required=false) SessionFactory sessionFactory;
	
	/**
	 * If the configuration isn't correctly set it will return NULL.
	 * 
	 * @return Session (Hibernate)
	 */
	protected Session getSession() {
		
        Session sess = sessionFactory.getCurrentSession();
        if (sess == null)
        	sess = sessionFactory.openSession();
        
        if (!sess.getTransaction().isActive())
        	sess.beginTransaction();
        
        return sess;
    }
	
	public DSpaceDao (Class<T> clazz) {
		this.clazz = clazz;
	}
	

    @SuppressWarnings("unchecked")
	@Override
    public T selectById(int id) {
    	if (this.getSession() == null) return null;
        return (T) getSession().get(clazz, id);
    }
	
	@Override
    public Integer save(T c) {
        Session session = getSession();
        Integer id = null;
        if (session == null) return id;
        try {
            id = (Integer) session.save(c);
            c.setID(id);
            log.debug(c.getClass().getSimpleName() + " saved");
        } catch (Exception e) {
        	log.error(e.getMessage(), e);
        }
        return id;
    }
	
	public boolean update (T c) {
		try {
			if (this.getSession() == null) return false;
			getSession().update(c);
			return true;
		} catch (Exception e) {
        	log.error(e.getMessage(), e);
			return false;
		}
		
	}
	
	@Override
    public boolean delete(T c) {
        boolean result = false;
        Session session = getSession();
        if (session == null) return false;
        try {
            session.delete(c);
            log.debug(c.getClass().getSimpleName() + " deleted");
            result = true;
        } catch (HibernateException e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }
	
    @SuppressWarnings("unchecked")
    @Override
    public List<T> selectAll() {
    	if (getSession() == null) return null;
        return (List<T>) getSession().createCriteria(
                clazz).list();
    }
}
