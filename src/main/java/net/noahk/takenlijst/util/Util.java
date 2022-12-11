package net.noahk.takenlijst.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public class Util {

    public static ResponseEntity<String> getBindingResultResponse(BindingResult bindingResult) {
        String response = "";
        for (var error : bindingResult.getFieldErrors()) {
            response += error.getField() + ": " + error.getDefaultMessage() + "\n";
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
