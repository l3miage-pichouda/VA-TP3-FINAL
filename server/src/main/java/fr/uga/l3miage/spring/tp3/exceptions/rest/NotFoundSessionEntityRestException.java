package fr.uga.l3miage.spring.tp3.exceptions.rest;

public class NotFoundSessionEntityRestException extends RuntimeException{
    public NotFoundSessionEntityRestException(String message) {
        super(message);
    }
}
