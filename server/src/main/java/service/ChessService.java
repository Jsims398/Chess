package service;

import dataaccess.DataAccess;
import java.util.Collection;

public class ChessService {
    private final DataAccess dataAccess;

    public ChessService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }
}