package com.devmasterteam.photicker.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devmasterteam.photicker.R;
import com.devmasterteam.photicker.views.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SocialUtil {

    private static final String HASHTAG = "#photickerAPP";

    public static void shareImageOnFacebook(MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View view) {
    }

    public static void shareImageOnWhatszap(Activity activity, RelativeLayout mRelativePhotoContent, View view) {
        PackageManager pkManager = activity.getPackageManager();

        try {
            pkManager.getPackageInfo("com.whatsapp", 0);
            String fileName = "temp_file" + System.currentTimeMillis() + ".jpg";

            mRelativePhotoContent.setDrawingCacheEnabled(true);
            mRelativePhotoContent.buildDrawingCache();

            File imageFile = new File(Environment.getExternalStorageDirectory(), fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            mRelativePhotoContent.getDrawingCache(true).compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            mRelativePhotoContent.setDrawingCacheEnabled(false);
            mRelativePhotoContent.destroyDrawingCache();

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, HASHTAG);
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/"+ fileName));
            sendIntent.setType("image/jpg");
            sendIntent.setPackage("com.whatsapp");

            view.getContext().startActivity(Intent.createChooser(sendIntent, activity.getString(R.string.share_image)));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(activity, R.string.whatsapp_not_installed, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(activity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(activity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }

    public static void shareImageOnInstagram(MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View view) {
    }

    public static void shareImageOnTwitter(MainActivity mainActivity, RelativeLayout mRelativePhotoContent, View view) {
    }
}
