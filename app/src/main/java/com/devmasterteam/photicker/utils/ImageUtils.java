package com.devmasterteam.photicker.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.devmasterteam.photicker.R;
import com.devmasterteam.photicker.views.MainActivity;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ImageUtils {

    public static List<Integer> getImageList() {
        List<Integer> images = new ArrayList<>();
        Field[] drawables = R.drawable.class.getFields();

        for (Field f : drawables) {
            try {
                if (f.getName().startsWith("st")) {
                    images.add(f.getInt(f.getName()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return images;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmatpFromResource(Resources res, int resId, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static void handleZoonIn(ImageView mImageSelected) {
        if (mImageSelected.getWidth() > 800) {
            return;
        }

        ViewGroup.LayoutParams params = mImageSelected.getLayoutParams();
        params.width = (int) (mImageSelected.getWidth() + (mImageSelected.getWidth() * 0.1));
        params.height = (int) (mImageSelected.getHeight() + (mImageSelected.getHeight() * 0.1));
        mImageSelected.setLayoutParams(params);
    }

    public static void handleZoonOut(ImageView mImageSelected) {

        if (mImageSelected.getWidth() < 50) {
            return;
        }

        ViewGroup.LayoutParams params = mImageSelected.getLayoutParams();
        params.width = (int) (mImageSelected.getWidth() - (mImageSelected.getWidth() * 0.1));
        params.height = (int) (mImageSelected.getHeight() - (mImageSelected.getHeight() * 0.1));
        mImageSelected.setLayoutParams(params);
    }

    public static void handleRotateLeft(ImageView mImageSelected) {
        mImageSelected.setRotation(mImageSelected.getRotation() - 5);
    }

    public static void handleRotateRight(ImageView mImageSelected) {
        mImageSelected.setRotation(mImageSelected.getRotation() + 5);
    }

    public static File createImageFile(Context context) throws IOException {
        String imageFileName = "photicker";
        File storeDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(imageFileName, ".jpg", storeDir);
        return image;
    }

    public static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) {
        ExifInterface exifInterface;

        try {
            exifInterface = new ExifInterface(selectedImage.getPath());
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return rotateImage(img, 90);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return rotateImage(img, 90);
                default:
                    return img;
            }
        } catch (IOException e) {
            return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, false);
        img.recycle();
        return rotatedImg;
    }

    public static Bitmap drawBitmap(RelativeLayout mRelativePhotoContent) {
        mRelativePhotoContent.setDrawingCacheEnabled(true);
        return mRelativePhotoContent.getDrawingCache();
    }
}
