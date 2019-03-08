package com.devmasterteam.photicker.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devmasterteam.photicker.R;
import com.devmasterteam.photicker.views.MainActivity;
import com.facebook.share.model.ShareContent;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareMediaContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SocialUtil {

    private static final String HASHTAG = "#photickerAPP";

    // Por Meio do SDK
    public static void shareImageOnFacebook(Activity activity, RelativeLayout mRelativePhotoContent, View view) {
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(ImageUtils.drawBitmap(mRelativePhotoContent))
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .setShareHashtag(new ShareHashtag.Builder().setHashtag(HASHTAG).build())
                .build();

        new ShareDialog(activity).show(content);
    }

    // Forma 1 de fazer
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
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/" + fileName));
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

    public static void shareImageOnInstagram(Activity activity, RelativeLayout mRelativePhotoContent, View view) {
        PackageManager pkManager = activity.getPackageManager();
        try {
            pkManager.getPackageInfo("com.instagram.android", 0);
            Bitmap image = ImageUtils.drawBitmap(mRelativePhotoContent);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp_file.jpeg");
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temp_file.jpeg"));
            sendIntent.setType("image/*");
            sendIntent.setPackage("com.instagram.android");

            view.getContext().startActivity(Intent.createChooser(sendIntent, activity.getString(R.string.share_image)));


        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(activity, R.string.instagram_not_installed, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(activity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(activity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }

    // Forma 2
    public static void shareImageOnTwitter(Activity activity, RelativeLayout mRelativePhotoContent, View view) {
        PackageManager pkManager = activity.getPackageManager();
        try {
            pkManager.getPackageInfo("com.twitter.android", 0);
            Intent sendIntent = new Intent(Intent.ACTION_SEND);

            Bitmap image = ImageUtils.drawBitmap(mRelativePhotoContent);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, bytes);

            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp_file.jpeg");
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());

            sendIntent.putExtra(Intent.EXTRA_TEXT, HASHTAG);
            sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/temp_file.jpeg"));
            sendIntent.setType("image/jpeg");

            PackageManager pm = activity.getPackageManager();
            List<ResolveInfo> resolve = pm.queryIntentActivities(sendIntent, PackageManager.MATCH_DEFAULT_ONLY);
            boolean resolved = false;

            for (ResolveInfo ri : resolve) {
                if (ri.activityInfo.name.contains("twitter")) {
                    sendIntent.setClassName(ri.activityInfo.packageName, ri.activityInfo.name);
                    resolved = true;
                    break;
                }
            }

            view.getContext().startActivity(resolved ? sendIntent : Intent.createChooser(sendIntent, activity.getString(R.string.share_image)));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(activity, R.string.twitter_not_installed, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Toast.makeText(activity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(activity, R.string.unexpected_error, Toast.LENGTH_SHORT).show();
        }
    }
}
