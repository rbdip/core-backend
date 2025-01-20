package ru.stepagin.backend.exception;

public class ProjectAlreadyExistsException extends EntityAlreadyExistsException {
    public ProjectAlreadyExistsException(String author, String projectName) {
        super("Project already exists: " + projectName + "/" + author);
    }
}
