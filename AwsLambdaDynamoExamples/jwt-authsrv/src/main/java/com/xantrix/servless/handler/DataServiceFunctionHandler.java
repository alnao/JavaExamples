package com.xantrix.servless.handler;
import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

import com.xantrix.servless.function.AuthData;
import com.xantrix.servless.function.JwtTokenResponse;
 
 
public class DataServiceFunctionHandler extends SpringBootRequestHandler<AuthData, JwtTokenResponse>
{

}
