package it.itsincom.webdev2024.service.exception;

import java.sql.SQLException;

public class SessionCreationException extends Exception{
    public SessionCreationException(SQLException e) {
        super(e);
    }
}
