package com.example.lab2java;

public class LFSR {

    public static String GenerateKey(String plain, int lenM) {
        int seed = Integer.parseInt(plain, 2);
        StringBuilder key = new StringBuilder(lenM);
        int mask = 1 << (plain.length() - 1);
        int n = 23;
        int seedMask = (1 << n) - 1;
        int tapMask = 0b00000000000000000010000;
        for (int i = 0; i < lenM; i++) {
            int keyBit = (seed & mask) >> (plain.length() - 1);
            key.append(keyBit);
            int newBit = keyBit ^ ((seed & tapMask) >> 4);
            seed = (seed << 1) & seedMask;
            seed ^= newBit;
        }

        return key.toString();
    }
}
