package com.yuandaima.peanutrobot.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

public class FastYuvConverter {
    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Allocation inputAllocation;
    private Allocation outputAllocation;

    public FastYuvConverter(Context context) {
        rs = RenderScript.create(context);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    }

    public Bitmap convertYuvToBitmap(byte[] nv21Data, int width, int height) {
        if (inputAllocation == null) {
            Type.Builder yuvType = new Type.Builder(rs, Element.U8(rs))
                    .setX(nv21Data.length);
            inputAllocation = Allocation.createTyped(rs, yuvType.create(),
                    Allocation.USAGE_SCRIPT);

            Type.Builder rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs))
                    .setX(width).setY(height);
            outputAllocation = Allocation.createTyped(rs, rgbaType.create(),
                    Allocation.USAGE_SCRIPT);
        }

        inputAllocation.copyFrom(nv21Data);

        yuvToRgbIntrinsic.setInput(inputAllocation);
        yuvToRgbIntrinsic.forEach(outputAllocation);

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        outputAllocation.copyTo(bitmap);

        return bitmap;
    }

    public void release() {
        if (inputAllocation != null) {
            inputAllocation.destroy();
            inputAllocation = null;
        }
        if (outputAllocation != null) {
            outputAllocation.destroy();
            outputAllocation = null;
        }
        if (yuvToRgbIntrinsic != null) {
            yuvToRgbIntrinsic.destroy();
            yuvToRgbIntrinsic = null;
        }
        if (rs != null) {
            rs.destroy();
            rs = null;
        }
    }
}