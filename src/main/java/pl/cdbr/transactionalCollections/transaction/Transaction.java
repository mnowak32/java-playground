package pl.cdbr.transactionalCollections.transaction;

import java.util.Collection;

public interface Transaction<C> {

	boolean commit();
	boolean rollback();
	Collection<Operation<C>> getLog();
}
