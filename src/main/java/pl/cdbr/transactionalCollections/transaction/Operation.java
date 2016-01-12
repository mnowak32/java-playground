package pl.cdbr.transactionalCollections.transaction;

@FunctionalInterface
public interface Operation<C> {
	public void apply(C collection);
}
