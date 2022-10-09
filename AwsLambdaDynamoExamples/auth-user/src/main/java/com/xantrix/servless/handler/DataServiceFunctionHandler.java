package com.xantrix.servless.handler;

import org.springframework.cloud.function.adapter.aws.SpringBootRequestHandler;

import com.xantrix.servless.io.AuthPolicy;
import com.xantrix.servless.io.TokenAuthorizerContext;
 
public class DataServiceFunctionHandler extends SpringBootRequestHandler<TokenAuthorizerContext,AuthPolicy>
{

}
