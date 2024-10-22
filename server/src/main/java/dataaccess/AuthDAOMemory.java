package dataaccess;

import model.AuthData;
import java.util.HashMap;

public class AuthDAOMemory implements AuthDAO {
    private final HashMap<String, AuthData> database = new HashMap<>();

    @Override
    public AuthData getAuth(String authToken){
        return database.get(authToken);
    }

    @Override
    public void addAuth(AuthData authData) {
        database.put(authData.authToken(), authData);
    }

    @Override
    public void deleteAuth(String authToken) {
        database.remove(authToken);
    }

    @Override
    public void clear() {
        database.clear();
    }
}
