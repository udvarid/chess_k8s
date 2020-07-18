package com.donat.donchess.exceptions;

import java.time.LocalDateTime;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.springframework.http.HttpStatus;

@Provider
public class MyExceptionMapper implements ExceptionMapper<Throwable> {

	@Override
	public Response toResponse(Throwable exception) {
		MyExceptionMessage exceptionDetails = new MyExceptionMessage();
		exceptionDetails.setStatus(HttpStatus.NOT_FOUND);
		exceptionDetails.setMessage(exception.getMessage());
		exceptionDetails.setTimeStamp(LocalDateTime.now());
		return Response.status(Status.NOT_FOUND).entity(exceptionDetails).type("application/json").build();
	}

}

