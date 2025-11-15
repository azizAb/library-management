package com.aziz.library.infrastructure.security;

import org.springframework.stereotype.Component;

import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class DeviceInfoExtractor {

    public String getBrowser(HttpServletRequest request) {
        String userAgentString = request.getHeader("User-Agent");
        if (userAgentString == null) return "Unknown";
        
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        return userAgent.getBrowser().getName() + " " + userAgent.getBrowserVersion();
    }
    
    public String getDevice(HttpServletRequest request) {
        String userAgentString = request.getHeader("User-Agent");
        if (userAgentString == null) return "Unknown";
        
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        return userAgent.getOperatingSystem().getDeviceType().getName();
    }
    
    public String getOperatingSystem(HttpServletRequest request) {
        String userAgentString = request.getHeader("User-Agent");
        if (userAgentString == null) return "Unknown";
        
        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);
        return userAgent.getOperatingSystem().getName();
    }

}
