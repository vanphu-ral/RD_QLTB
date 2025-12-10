package io.rd.qltb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class GlobalConfig {
    public static String  IP_ADDRESS_FRONTEND = "http://192.168.18.127:4200";
    public static final int COMPLETED = 5;
    public static final int IN_PROGRESS = 4;
    public static final int DRAFF = 1;
    public static final int  WAIT_APPROVE =2 ;
    public static final int APPROVED = 3;
    public static final int REJECTED = 6;

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
