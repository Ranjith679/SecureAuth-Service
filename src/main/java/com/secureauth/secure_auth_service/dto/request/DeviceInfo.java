package com.secureauth.secure_auth_service.dto.request;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfo {

    /*
     * Browser / Android / iPhone.
     */
    private String deviceName;

    /*
     * Client IP.
     */
    private String ipAddress;

}