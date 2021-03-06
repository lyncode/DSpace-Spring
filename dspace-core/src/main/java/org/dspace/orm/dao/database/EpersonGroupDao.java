/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.orm.dao.database;


import org.dspace.orm.dao.api.IEpersonGroupDao;
import org.dspace.orm.dao.content.PredefinedGroup;
import org.dspace.orm.entity.EpersonGroup;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author João Melo <jmelo@lyncode.com>
 * @author Miguel Pinto <mpinto@lyncode.com>
 */
@Transactional
@Repository("org.dspace.orm.dao.api.IEpersonGroupDao")
public class EpersonGroupDao extends DSpaceDao<EpersonGroup> implements IEpersonGroupDao {
    
	public EpersonGroupDao() {
		super(EpersonGroup.class);
	}

	@Override
	public EpersonGroup selectById(PredefinedGroup anonymous) {
		return super.selectById(anonymous.getId());
	}
}
