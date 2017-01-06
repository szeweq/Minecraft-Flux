package szewek.mcflux.api.assistant;

import java.util.List;

public interface IAssist {
	List<QueryElement<?>> getPossibleQueries();
}
