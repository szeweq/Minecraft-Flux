package szewek.mcflux.api.assistant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AnswerElement<T> {
	private final QueryElement<T> query;
	private final List<T> answerList = new ArrayList<>();
	private final List<T> answersRO = Collections.unmodifiableList(answerList);

	AnswerElement(QueryElement<T> qe) {
		this.query = qe;
	}

	public void add(T o) {
		if (query.type.isInstance(o))
			answerList.add(o);
	}

	public List<T> getAll() {
		return answersRO;
	}
}
