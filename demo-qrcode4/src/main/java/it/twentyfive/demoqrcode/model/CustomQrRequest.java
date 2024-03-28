package it.twentyfive.demoqrcode.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomQrRequest {
    private int width;
    private int height;
    private String requestUrl;
    private CustomBorder customBorder;
    private CustomColor customColor;;
    private String logoCenterUrl;
    private String logoBorderUrl;
}

