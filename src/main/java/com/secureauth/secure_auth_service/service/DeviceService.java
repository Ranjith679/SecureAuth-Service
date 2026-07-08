package com.secureauth.secure_auth_service.service;

import com.secureauth.secure_auth_service.dto.request.DeviceInfo;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

/*
 * Reads client device information.
 */
@Service
public class DeviceService {

    public DeviceInfo getDevice(
            HttpServletRequest request){

        /*
         * User-Agent sent by browser.
         */
        String agent =
                request.getHeader("User-Agent");

        /*
         * Client IP.
         */
        String ip =
                request.getRemoteAddr();

        return DeviceInfo.builder()

                .deviceName(agent)

                .ipAddress(ip)

                .build();

    }

}