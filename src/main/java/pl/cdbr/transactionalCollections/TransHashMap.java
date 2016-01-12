package pl.cdbr.transactionalCollections;

import java.util.HashMap;
import java.util.Map;

import pl.cdbr.transactionalCollections.transaction.MapTransaction;
import pl.cdbr.transactionalCollections.transaction.Transaction;

public class TransHashMap<K, V> implements TransMap<K, V> {
	
	private final HashMap<K, V> store = new HashMap<>();

	@Override
	public MapTransaction<K, V> begin() {
		return null;
	}

	@Override
	public Map<K, V> snapshot() {
		return new HashMap<>(store);
	}

	@Override
	public boolean commit(Transaction<Map<K, V>> t) {
		t.getLog().stream()
			.forEach(op -> op.apply(store));
		return true;
	}

}
