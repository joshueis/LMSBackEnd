package com.gcit.lms;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class MyExceptionMapper implements ExceptionMapper<NotFoundException> {

	@Override
	public Response toResponse(NotFoundException ex) {
		return Response.status(Response.Status.NOT_FOUND)
				.entity("Resource Not Found").type("text/plain").build();
	}
	

}
