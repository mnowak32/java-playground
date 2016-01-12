package pl.cdbr.transactionalCollections;

import java.util.Map;

import pl.cdbr.transactionalCollections.transaction.MapTransaction;
import pl.cdbr.transactionalCollections.transaction.Transaction;

public interface TransMap<K, V> {

	MapTransaction<K, V> begin();
	Map<K, V> snapshot();
	boolean commit(Transaction<Map<K, V>> t);
}
