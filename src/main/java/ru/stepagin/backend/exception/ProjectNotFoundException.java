package ru.stepagin.backend.exception;

public class ProjectNotFoundException extends EntityNotFoundException {
    public ProjectNotFoundException(String username, String projectName) {
        super("Project not found: " + username + "/" + projectName);
    }

    public ProjectNotFoundException(String username, String projectName, String projectVersion) {
        super("Project not found: " + username + "/" + projectName + " on version '" + projectVersion + "'");
    }
}
