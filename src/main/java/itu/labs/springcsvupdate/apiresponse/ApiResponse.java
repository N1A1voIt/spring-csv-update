package itu.labs.springcsvupdate.apiresponse;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ApiResponse {
    int status;
    Object data;
    List<Exception> exceptions;
}
