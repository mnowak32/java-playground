package pl.codebrewery.lab;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class ObjectiveIf<T> {

	public static <T> ConditionalExpression<T> iff(boolean condition, Supplier<? extends T> t) {
		return new ConditionalExpression<T>(() -> condition, t);
	}
	
	public static <T> ConditionalExpression<T> iff(boolean condition, Supplier<? extends T> t, Supplier<? extends T> o) {
		return new ConditionalExpression<T>(() -> condition, t, o);
	}
	
	public static <T> ConditionalExpression<T> iff(Supplier<Boolean> condition, Supplier<? extends T> t) {
		return new ConditionalExpression<T>(condition, t);
	}
	
	public static <T> ConditionalExpression<T> iff(Supplier<Boolean> condition, Supplier<? extends T> t, Supplier<? extends T> o) {
		return new ConditionalExpression<T>(condition, t, o);
	}
	
	public static class ConditionalExpression<T> {
		private Supplier<Boolean> condition;
		private Optional<Supplier<? extends T>> then = Optional.empty(), otherwise = Optional.empty();

		public ConditionalExpression(final Supplier<Boolean> c) {
			this.condition = c;
		}
		
		public ConditionalExpression(final Supplier<Boolean> c, Supplier<? extends T> t) {
			this.condition = c;
			this.then = Optional.ofNullable(t);
		}
		
		public ConditionalExpression(final Supplier<Boolean> c, Supplier<? extends T> t, Supplier<? extends T> o) {
			this.condition = c;
			this.then = Optional.ofNullable(t);
			this.otherwise = Optional.ofNullable(o);
		}
		
		public ConditionalExpression<T> then(Supplier<? extends T> t) {
			this.then = Optional.ofNullable(t);
			return this;
		}
		
		public ConditionalExpression<T> otherwise(Supplier<? extends T> o) {
			this.otherwise = Optional.ofNullable(o);
			return this;
		}
		
		public Optional<T> get() {
			return condition.get() ? then.map(Supplier::get) : otherwise.map(Supplier::get);
		}
		
		public void consume(Consumer<? super T> cons) {
			get().ifPresent(cons);
		}
		
		public <U> Optional<U> apply(Function<? super T, ? extends U> func) {
			return get().map(func);
		}
	}
	
	public static void main(String[] args) {
		int a = 13;
		iff(a == 13,
			() -> "abc",
			() -> "def")
			.apply(String::toUpperCase)
			.ifPresent(System.out::println);
	}
}
