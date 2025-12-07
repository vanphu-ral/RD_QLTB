package io.rd.qltb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class GlobalConfig {
    public static String  IP_ADDRESS_FRONTEND = "http://localhost:4200";

    public String createNumberPrefix(Long number, int minLength) {
        String numberStr = String.valueOf(number);
        int numberLength = numberStr.length();

        if (numberLength >= minLength) {
            return numberStr; // nếu dài hơn hoặc bằng thì giữ nguyên
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < minLength - numberLength; i++) {
            sb.append('0');
        }
        sb.append(numberStr);
        return sb.toString();
    }
}
