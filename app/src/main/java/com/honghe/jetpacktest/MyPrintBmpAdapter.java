package com.honghe.jetpacktest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MyPrintBmpAdapter extends PrintDocumentAdapter {
    private Bitmap bitmap;
    private Context context;
    private PrintAttributes mAttributes;

    public MyPrintBmpAdapter(Context context, Bitmap bitmap) {
        this.bitmap = bitmap;
        this.context = context;
    }


    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal,
                         LayoutResultCallback callback, Bundle extras) {
        this.mAttributes = newAttributes;
        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }
        PrintDocumentInfo info = new PrintDocumentInfo.Builder("name")
                .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                .build();
        callback.onLayoutFinished(info, true);
    }

    @Override
    public void onWrite(PageRange[] pages, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal,
                        WriteResultCallback callback) {
        OutputStream output = null;

        try {
            output = new FileOutputStream(destination.getFileDescriptor());
            PrintedPdfDocument document = new PrintedPdfDocument(context, mAttributes);//1, 建立PdfDocument
            PdfDocument.Page page = document.startPage(1);
            RectF contentRect = new RectF(page.getInfo().getContentRect());
            Matrix matrix = getMatrix(bitmap.getWidth(), bitmap.getHeight(), contentRect);
            page.getCanvas().drawBitmap(bitmap, matrix, null);
            document.finishPage(page);//4
            document.writeTo(output);
            callback.onWriteFinished(new PageRange[]{PageRange.ALL_PAGES});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static Matrix getMatrix(int imageWidth, int imageHeight, RectF content) {
        Matrix matrix = new Matrix();
        float scale = content.width() / (float) imageWidth;
        scale = Math.min(scale, content.height() / (float) imageHeight);

        matrix.postScale(scale, scale);
        float translateX = (content.width() - (float) imageWidth * scale) / 2.0F;
        float translateY = (content.height() - (float) imageHeight * scale) / 2.0F;
        matrix.postTranslate(translateX, translateY);
        return matrix;
    }
}
