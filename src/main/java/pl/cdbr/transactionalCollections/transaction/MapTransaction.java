package pl.cdbr.transactionalCollections.transaction;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import pl.cdbr.transactionalCollections.TransMap;

public class MapTransaction<K, V> implements Map<K, V>, Transaction<Map<K, V>> {
	private final TransMap<K, V> parent;
	private final Map<K, V> snapshot;
	private LinkedList<Operation<Map<K, V>>> log = new LinkedList<>();
	
	private final KeySetWrapper keySet; 
	
	public MapTransaction(TransMap<K, V> parent) {
		this.parent = parent;
		this.snapshot = parent.snapshot();
		this.keySet = new KeySetWrapper(snapshot.keySet());
	}

	@Override
	public int size() {
		return snapshot.size();
	}

	@Override
	public boolean isEmpty() {
		return snapshot.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return snapshot.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return snapshot.containsValue(value);
	}

	@Override
	public V get(Object key) {
		return snapshot.get(key);
	}

	@Override
	public V put(final K key, final V value) {
		log.add(m -> m.put(key, value));
		return snapshot.put(key, value);
	}

	@Override
	public V remove(Object key) {
		log.add(m -> m.remove(key));
		return snapshot.remove(key);
	}

	@Override
	public void putAll(final Map<? extends K, ? extends V> map) {
		log.add(m -> m.putAll(map));
		snapshot.putAll(map);
	}

	@Override
	public void clear() {
		log.add(m -> m.clear());
		snapshot.clear();
	}

	@Override
	public Set<K> keySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<V> values() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean commit() {
		return parent.commit(this);
	}

	@Override
	public boolean rollback() {
		//just discard the transaction
		return true;
	}

	@Override
	public Collection<Operation<Map<K, V>>> getLog() {
		return log;
	}
	
	public class KeySetWrapper implements Set<K> {

		private final Set<K> keySet;

		public KeySetWrapper(Set<K> keySet) {
			this.keySet = keySet;
		}

		@Override
		public int size() {
			return keySet.size();
		}

		@Override
		public boolean isEmpty() {
			return keySet.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return keySet.contains(o);
		}

		@Override
		public Iterator<K> iterator() {
			// TODO czy trzeba robić wrapper?
			return keySet.iterator();
		}

		@Override
		public Object[] toArray() {
			return keySet.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return keySet.toArray(a);
		}

		@Override
		public boolean add(K e) {
			log.add(m -> m.keySet().add(e));
			return keySet.add(e);
		}

		@Override
		public boolean remove(Object o) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean addAll(Collection<? extends K> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void clear() {
			// TODO Auto-generated method stub
			
		}
		
	}

}
