/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.orm.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;


import org.dspace.orm.dao.api.ICollectionDao;
import org.dspace.orm.dao.api.ICommunityDao;
import org.dspace.orm.dao.api.IEpersonDao;
import org.dspace.orm.dao.api.IEpersonGroupDao;
import org.dspace.orm.dao.api.IItemDao;
import org.dspace.orm.dao.content.DSpaceObjectType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Table(name = "handle")
@SequenceGenerator(name="handle_gen", sequenceName="handle_seq")
@Configurable
public class Handle extends DSpaceObject {
    private String handle;
    private int resourceType;
    private int resourceId;

    @Id
    @Column(name = "handle_id")
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="handle_gen")
    public int getID() {
        return id;
    }

    @Column(name = "handle", unique = true)
    public String getHandleString() {
        return handle;
    }

    @Column(name = "resource_type_id")
    public int getResourceType() {
        return resourceType;
    }

    @Column(name = "resource_id")
    public int getResourceId() {
        return resourceId;
    }

    public void setHandleString(String handle) {
        this.handle = handle;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    @Transient
	@Override
	public DSpaceObjectType getType() {
		return DSpaceObjectType.HANDLE;
	}
	
	@Autowired ICommunityDao commDao;
	@Autowired ICollectionDao collDao;
	@Autowired IEpersonDao personDao;
	@Autowired IEpersonGroupDao groupDao;
	@Autowired IItemDao itemDao;

	/**
	 * Returns a DSpace Object from the Handle.
	 * Other options added:
	 * 
	 * - EPERSON
	 * - EPERSONGROUP
	 * 
	 * So now they could have an handle
	 * 
	 * @return DSpace Object
	 */
	public DSpaceObject toDSpaceObject() {
		switch (DSpaceObjectType.getById(this.getResourceType())) {
			case COLLECTION:
				return collDao.selectById(getResourceId());
			case COMMUNITY:
				return commDao.selectById(getResourceId());
			case EPERSON:
				return personDao.selectById(getResourceId());
			case GROUP:
				return groupDao.selectById(getResourceId());
			default:
				return itemDao.selectById(getResourceId());
		}
	}
	
	@Transient
	public String toString () {
		return this.getHandleString();
	}
}
