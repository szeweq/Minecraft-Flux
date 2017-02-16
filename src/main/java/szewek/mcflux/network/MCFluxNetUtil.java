package szewek.mcflux.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public enum MCFluxNetUtil {
	;

	public static JsonObject downloadGistJSON(String hash, String name) throws IOException {
		URL url = new URL("https", "gist.githubusercontent.com", 443, "/Szewek/" + hash + "/raw/" + name, null);
		HttpURLConnection huc = (HttpURLConnection) url.openConnection();
		InputStreamReader isr = new InputStreamReader(huc.getInputStream());
		return new JsonParser().parse(isr).getAsJsonObject();
	}

}
