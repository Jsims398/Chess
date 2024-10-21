package dataaccess;

import model.AuthData;

import java.util.HashSet;

public class AuthDAOMemory implements AuthDAO {

    HashSet<AuthData> database;
//    public MemoryAuthDAO(){
//        database = HashSet.newHashSet(20);
//    }

    //    createAuth
    @Override
    public AuthData getAuth(String authentucation) throws DataAccessException {
        for (AuthData code : database) {
            if (code.authToken().equals(authentucation)) {
                return code;
            }
        }
        throw new DataAccessException("Auth Token does not exist: " + authentucation);
    }
    //    getAuth
    @Override
    public void addAuth(AuthData authData) {
        database.add(authData);
    }
    //    deleteAuth
    @Override
    public void deleteAuth(String authentication) {
        for (AuthData data : database) {
            if (data.authToken().equals(authentication)) {
                database.remove(data);
                break;
            }
        }
    }

    @Override
    public void clear() {
        database = HashSet.newHashSet(20);
    }
}



