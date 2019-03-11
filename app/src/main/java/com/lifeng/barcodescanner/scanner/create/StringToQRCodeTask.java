package com.lifeng.barcodescanner.scanner.create;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

import java.util.EnumMap;
import java.util.Map;

/**
 * author : lifeng
 * e-mail : android_lifeng@sina.com
 * date   : 2019/3/111:57 PM
 * desc   : 一步任务生成图片
 * version: 1.0
 */
public class StringToQRCodeTask extends AsyncTask<Void, Void, Bitmap> {
    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private String content;//生成图片的内容
    private int dimension;//生成图片的大小
    private StringToQRCodeListener listener;

    public StringToQRCodeTask(String content, int dimension, StringToQRCodeListener listener) {
        this.content = content;
        this.listener = listener;
        this.dimension = dimension;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
        Bitmap bitmap = null;
        try {
            String contentsToEncode = content;
            if (contentsToEncode == null) {
                return null;
            }
            Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            BitMatrix result;
            result = new MultiFormatWriter().encode(
                    contentsToEncode, BarcodeFormat.QR_CODE, dimension, dimension, hints);
            int width = result.getWidth();
            int height = result.getHeight();
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
                }
            }
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        } catch (Exception ignore) {
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        if (listener != null) {
            listener.onResult(result);
        }
    }
}
