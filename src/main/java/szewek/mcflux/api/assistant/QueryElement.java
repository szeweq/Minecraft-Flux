package szewek.mcflux.api.assistant;

public class QueryElement<T> {
	public final String name;
	public final Class<T> type;

	public QueryElement(String name, Class<T> type) {
		this.name = name;
		this.type = type;
	}

	AnswerElement<T> createAnswer() {
		return new AnswerElement<T>(this);
	}

	@SuppressWarnings("unchecked")
	public <E> QueryElement<E> getCasted() {
		return (QueryElement<E>) this;
	}
}
