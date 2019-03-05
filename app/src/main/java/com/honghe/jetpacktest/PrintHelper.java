package com.honghe.jetpacktest;

import android.content.Context;
import android.graphics.Bitmap;
import android.print.PrintManager;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;

public class PrintHelper {
    private static final String TAG = PrintHelper.class.getName();

    public static void printBmp(Context context, Bitmap bitmap) {
        if (android.support.v4.print.PrintHelper.systemSupportsPrint()) {
            android.support.v4.print.PrintHelper printHelper = new android.support.v4.print.PrintHelper(context);
            printHelper.setScaleMode(android.support.v4.print.PrintHelper.SCALE_MODE_FIT);
            printHelper.printBitmap("printBmp", bitmap);
            Log.d(TAG, "doBmpPrint ");
        } else {
            Log.d(TAG, "printBmp: 不支持图片打印");
        }
    }

    public static void printBmp2(Context context, Bitmap bitmap) {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        MyPrintBmpAdapter myPrintBmpAdapter = new MyPrintBmpAdapter(context, bitmap);
        printManager.print("printBmp2", myPrintBmpAdapter, null);
    }

    public static void printWebView(Context context, WebView webView) {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        printManager.print("printWebView", webView.createPrintDocumentAdapter(), null);
    }

    public static void printAssetPDF(Context context, String filePath) {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        MyPrintPdfAdapter myPrintAdapter = new MyPrintPdfAdapter(context, filePath, true);
        printManager.print("printAssetPDF", myPrintAdapter, null);
    }

    public static void printPDF(Context context, String filePath) {
        PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
        MyPrintPdfAdapter myPrintAdapter = new MyPrintPdfAdapter(filePath);
        printManager.print("printPDF", myPrintAdapter, null);
    }

    public static void printView(Context context, View view) {
        printBmp2(context, getCacheBitmapFromView(view));
    }

    public static Bitmap getCacheBitmapFromView(View view) {
        return getCacheBitmapFromView(view, false);
    }

    /**
     * 获取一个 View 的缓存视图
     * (前提是这个View已经渲染完成显示在页面上)
     *
     * @param view
     * @return
     */
    public static Bitmap getCacheBitmapFromView(View view, boolean needFitA4Scale) {
        final boolean drawingCacheEnabled = true;
        view.setDrawingCacheEnabled(drawingCacheEnabled);
        view.buildDrawingCache(drawingCacheEnabled);
        final Bitmap drawingCache = view.getDrawingCache();
        Bitmap bitmap;
        if (drawingCache != null) {
            if (needFitA4Scale) {
                float width = drawingCache.getWidth();//设置A4纸比例
                float height = drawingCache.getHeight();
                if ((int) (width * 1.414) > (int) height) {
                    bitmap = Bitmap.createBitmap(drawingCache, 0, 0, (int) width, (int) height);
                } else {
                    bitmap = Bitmap.createBitmap(drawingCache, 0, 0, (int) width, (int) (width * 1.414));
                }
            } else {
                bitmap = Bitmap.createBitmap(drawingCache);
            }
            view.setDrawingCacheEnabled(false);
        } else {
            bitmap = null;
        }
        return bitmap;
    }
}
