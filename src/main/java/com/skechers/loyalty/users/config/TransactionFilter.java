package com.skechers.loyalty.users.config;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skechers.loyalty.users.data.CountriesConfig;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("DEPRECATION")
@Component
@Order(1)
@Slf4j
public class TransactionFilter implements Filter {
	

	@Value("${spring.profiles.active}")
	private String activeProfile;
	
	@Autowired
	ObjectMapper objectmapper;
	
	@Autowired
	CountriesConfig countriesConfig;
	
	@Resource(name = "loyaltySupportLocales")
	private Map<String,List<String>> loyaltySupportedLocales;

	private static final String COUNTRY = "country";
	private static final String USER = "userId";
	private static final String LOCALE = "locale";

	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		try {

			MDC.put("aProfile", activeProfile);
			String countryCode;
			String locale;
			// if(StringUtils.isEmpty(req.getHeader(LOCALE))) {
			//	locale = "en-US";
			//} else {
			//	locale = req.getHeader(LOCALE);
			//}
			locale = "en-US";

			if (req.getHeader(COUNTRY) == null || "".equals(req.getHeader(COUNTRY))) {
				countryCode = "usa";
			} else {
				countryCode = req.getHeader(COUNTRY).toLowerCase();
			}
			List<String> locales = loyaltySupportedLocales.get(countryCode.toLowerCase());
			if (locales == null) {
				res.sendError(HttpStatus.BAD_REQUEST.value(), "Country not available!");
				return;
			} else if (!locales.contains(locale)) {
				res.sendError(HttpStatus.BAD_REQUEST.value(), "Country & locale does not match");
				return;
			}
			log.info("countryCode {}", countryCode);
			MDC.put("countryCode", countryCode);
			log.info("locale {}", locale);
			MDC.put(LOCALE, locale);
			
			if (req.getHeader(USER) != null) {
				MDC.put(USER, req.getHeader(USER));
			}
			chain.doFilter(request, response);

		} finally {
			MDC.clear();
		}
	}
	
}