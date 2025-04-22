package com.example.util;

public class HexToAscii {
    public static String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder("");
        
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        
        return output.toString();
    }
}
