/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.orm.dao.database;


import org.dspace.orm.dao.api.IFileExtensionDao;
import org.dspace.orm.entity.FileExtension;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Miguel Pinto <mpinto@lyncode.com>
 * @version $Revision$
 */

@Transactional
@Repository("org.dspace.orm.dao.api.IFileExtensionDao")
public class FileExtensionDao extends DSpaceDao<FileExtension> implements IFileExtensionDao {
    
	public FileExtensionDao() {
		super(FileExtension.class);
	}
}
