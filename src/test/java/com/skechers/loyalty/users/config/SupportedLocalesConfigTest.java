package com.skechers.loyalty.users.config;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class SupportedLocalesConfigTest {
	
	@InjectMocks
	SupportedLocalesConfig supportedLocalesConfig;
	
    String supportedLocales= "{ \"usa\":[en-US], \"deu\":[\"de-DE\",\"en-DE\"], \"can\":[\"en-CA\", \"fr-CA\"],  \"gbr\":[\"en-GB\"], \"esp\":[\"es-ES\",\"en-ES\"] }";
    
    
    @Test
    void supportedLocalesTest(){
    	supportedLocalesConfig.setSupportedLocales(supportedLocales);
    	Map<String, List<String>> localeMap =  supportedLocalesConfig.loyaltySupportLocales();
    	assertEquals("en-US", localeMap.get("usa").get(0));
    	
    }

}
