package com.skechers.loyalty.users.config;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

@Configuration
public class SupportedLocalesConfig {
      
      @Value("${supported_locales}")
      private String supportedLocales;
      
      @Bean
      public Map<String,List<String>> loyaltySupportLocales(){
            Gson gson = new GsonBuilder().create();
            Type localesType = new TypeToken<Map<String,List<String>>>() {}.getType();
            return gson.fromJson(supportedLocales, localesType);
      }

	public void setSupportedLocales(String supportedLocales) {
		this.supportedLocales = supportedLocales;
	}
      
      

}
