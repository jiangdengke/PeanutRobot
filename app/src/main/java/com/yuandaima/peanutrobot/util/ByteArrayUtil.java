package com.yuandaima.peanutrobot.util;

import okio.ByteString;

public class ByteArrayUtil {
    public static ByteString byteString(byte[] data) {
        return ByteString.of(data);
    }

}
