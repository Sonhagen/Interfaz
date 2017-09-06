package JSONParser;

import org.json.JSONObject;

public interface AsyncResponse {
    void processFinish(JSONObject json);
}
