package vn.dk.BackendFoodApp.exception;

public class InvalidDataException extends RuntimeException {
  public InvalidDataException(String message) {
    super(message);
  }
}
