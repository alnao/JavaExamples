package com.xantrix.servless.handler;

import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

import com.amazonaws.services.lambda.runtime.events.S3Event;

 
public class DataServiceFunctionHandler extends SpringBootRequestHandler<S3Event, String>
{

}
