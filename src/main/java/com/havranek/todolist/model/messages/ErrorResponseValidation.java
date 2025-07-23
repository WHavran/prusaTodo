package com.havranek.todolist.model.messages;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponseValidation(LocalDateTime timestamp,
                                      int status,
                                      String error,
                                      List<ErrorValidationField> listOfErrors) {
}
