package com.devmasterteam.photicker.views;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devmasterteam.photicker.R;
import com.devmasterteam.photicker.utils.LongEventType;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.devmasterteam.photicker.utils.ImageUtils;
import com.devmasterteam.photicker.utils.PermissionUtil;
import com.devmasterteam.photicker.utils.SocialUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener, View.OnLongClickListener {

    private static final int Request_TAKE_PHOTO = 2;
    private final ViewHolder mViewHolder = new ViewHolder();
    private ImageView mImageSelected;
    private boolean mAutoIncrement;
    private LongEventType mLongEventType;
    private Handler mRepeatUpdateHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        List<Integer> mListImages = ImageUtils.getImageList();
        final LinearLayout content = (LinearLayout) findViewById(R.id.linear_horizontal_scrool_content);
        this.mViewHolder.mRelativePhotoContent = (RelativeLayout) this.findViewById(R.id.relative_photo_content_draw);


        for (Integer imageId : mListImages) {
            ImageView image = new ImageView(this);
            image.setImageBitmap(ImageUtils.decodeSampledBitmatpFromResource(getResources(), imageId, 70, 70));
            image.setPadding(20, 10, 20, 10);

            BitmapFactory.Options dimensions = new BitmapFactory.Options();
            dimensions.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), imageId, dimensions);

            final int width = dimensions.outWidth;
            final int height = dimensions.outHeight;

            image.setOnClickListener(onClickImageOption(this.mViewHolder.mRelativePhotoContent, imageId, width, height));

            content.addView(image);
        }

        this.mViewHolder.mLinearControlPanel = (LinearLayout) this.findViewById(R.id.linear_control_panel);
        this.mViewHolder.mLinearSharePanel = (LinearLayout) this.findViewById(R.id.linear_share_panel);
        this.mViewHolder.mButtonZoomIn = (ImageView) this.findViewById(R.id.image_zoom_in);
        this.mViewHolder.mButtonZoomOut = (ImageView) this.findViewById(R.id.image_zoom_out);
        this.mViewHolder.mButtonRotateLeft = (ImageView) this.findViewById(R.id.image_rotate_left);
        this.mViewHolder.mButtonRoteteRight = (ImageView) this.findViewById(R.id.image_rotate_right);
        this.mViewHolder.mButtonFinish = (ImageView) this.findViewById(R.id.image_finish);
        this.mViewHolder.mButtonRemove = (ImageView) this.findViewById(R.id.image_remove);
        this.mViewHolder.mImagePhoto = (ImageView) this.findViewById(R.id.image_photo);

        this.mViewHolder.mImageInstagram  = (ImageView) this.findViewById(R.id.image_instagram);
        this.mViewHolder.mImageFacebook  = (ImageView) this.findViewById(R.id.image_facebook);
        this.mViewHolder.mImageWhatszap  = (ImageView) this.findViewById(R.id.image_whatssap);
        this.mViewHolder.mImageTwitter  = (ImageView) this.findViewById(R.id.image_twitter);

        this.setListeners();
    }

    private void setListeners() {
        this.findViewById(R.id.image_take_photo).setOnClickListener(this);
        this.findViewById(R.id.image_zoom_in).setOnClickListener(this);
        this.findViewById(R.id.image_zoom_out).setOnClickListener(this);
        this.findViewById(R.id.image_rotate_right).setOnClickListener(this);
        this.findViewById(R.id.image_rotate_left).setOnClickListener(this);
        this.findViewById(R.id.image_finish).setOnClickListener(this);
        this.findViewById(R.id.image_remove).setOnClickListener(this);

        this.findViewById(R.id.image_zoom_in).setOnLongClickListener(this);
        this.findViewById(R.id.image_zoom_out).setOnLongClickListener(this);
        this.findViewById(R.id.image_rotate_right).setOnLongClickListener(this);
        this.findViewById(R.id.image_rotate_left).setOnLongClickListener(this);
        this.findViewById(R.id.image_finish).setOnLongClickListener(this);
        this.findViewById(R.id.image_remove).setOnLongClickListener(this);

        this.mViewHolder.mImageInstagram.setOnClickListener(this);
        this.mViewHolder.mImageTwitter.setOnClickListener(this);
        this.mViewHolder.mImageFacebook.setOnClickListener(this);
        this.mViewHolder.mImageWhatszap.setOnClickListener(this);

        this.findViewById(R.id.image_zoom_in).setOnTouchListener(this);
        this.findViewById(R.id.image_zoom_out).setOnTouchListener(this);
        this.findViewById(R.id.image_rotate_right).setOnTouchListener(this);
        this.findViewById(R.id.image_rotate_left).setOnTouchListener(this);
        this.findViewById(R.id.image_finish).setOnTouchListener(this);
        this.findViewById(R.id.image_remove).setOnTouchListener(this);
    }

    private View.OnClickListener onClickImageOption(final RelativeLayout relativeLayout, final Integer imageId, int width, int height) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ImageView image = new ImageView(MainActivity.this);
                image.setBackgroundResource(imageId);
                relativeLayout.addView(image);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) image.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);

                mImageSelected = image;

                toggleControlPanel(true);

                image.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View v, MotionEvent motionEvent) {
                        float x, y;

                        switch (motionEvent.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                mImageSelected = image;
                                toggleControlPanel(true);
                                break;
                            case MotionEvent.ACTION_MOVE:
                                int cords[] = {0, 0};
                                relativeLayout.getLocationOnScreen(cords);
                                x = (motionEvent.getRawX() - (image.getWidth() / 2));
                                y = motionEvent.getRawY() - ((cords[1] + 100) + (image.getHeight() / 2));
                                image.setX(x);
                                image.setY(y);
                                break;
                            case MotionEvent.ACTION_UP:
                                break;

                        }
                        return true;
                    }
                });
            }

        };
    }

    private void toggleControlPanel(boolean showControls) {
        if (showControls) {
            this.mViewHolder.mLinearSharePanel.setVisibility(View.GONE);
            this.mViewHolder.mLinearControlPanel.setVisibility(View.VISIBLE);
        } else {
            this.mViewHolder.mLinearSharePanel.setVisibility(View.VISIBLE);
            this.mViewHolder.mLinearControlPanel.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.image_take_photo:
                if (!PermissionUtil.hasCameraPermission(this)) {
                    PermissionUtil.asksCameraPermission(this);
                } else {
                    dispatchTakePictureIntent();
                }
                break;
            case R.id.image_zoom_in:
                ImageUtils.handleZoonIn(this.mImageSelected);
                break;
            case R.id.image_zoom_out:
                ImageUtils.handleZoonOut(this.mImageSelected);
                break;
            case R.id.image_rotate_left:
                ImageUtils.handleRotateLeft(this.mImageSelected);
                break;
            case R.id.image_rotate_right:
                ImageUtils.handleRotateRight(this.mImageSelected);
                break;
            case R.id.image_finish:
                this.toggleControlPanel(false);
                break;
            case R.id.image_remove:
                this.mViewHolder.mRelativePhotoContent.removeView(this.mImageSelected);
                break;
            case R.id.image_facebook:
                SocialUtil.shareImageOnFacebook(this, mViewHolder.mRelativePhotoContent, view);
                break;
            case R.id.image_whatssap:
                SocialUtil.shareImageOnWhatszap(this, mViewHolder.mRelativePhotoContent, view);
                break;
            case R.id.image_instagram:
                SocialUtil.shareImageOnInstagram(this, mViewHolder.mRelativePhotoContent, view);
                break;
            case R.id.image_twitter:
                SocialUtil.shareImageOnTwitter(this, mViewHolder.mRelativePhotoContent, view);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Request_TAKE_PHOTO && resultCode == RESULT_OK) {
            this.setPhotoAsBackground();
        }
    }

    private void setPhotoAsBackground() {
        int targetW = this.mViewHolder.mImagePhoto.getWidth();
        int targetH = this.mViewHolder.mImagePhoto.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(this.mViewHolder.mUriPhotoPath.getPath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoh = bmOptions.outHeight;

        int scaleFactor = Math.min(photoh / targetH, photoh / targetH);

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;

        Bitmap bitmap = BitmapFactory.decodeFile(this.mViewHolder.mUriPhotoPath.getPath(), bmOptions);

        Bitmap bitmapRotated = ImageUtils.rotateImageIfRequired(bitmap, this.mViewHolder.mUriPhotoPath);

        this.mViewHolder.mImagePhoto.setImageBitmap(bitmapRotated);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtil.CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                new AlertDialog.Builder(this).setMessage(getString(R.string.without_permission_camera_explanation))
                        .setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        }
    }

    private void dispatchTakePictureIntent() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = ImageUtils.createImageFile(this);
                this.mViewHolder.mUriPhotoPath = Uri.fromFile(photoFile);
            } catch (IOException e) {
                Toast.makeText(this, "Não foi possivel iniciar a camera", Toast.LENGTH_SHORT).show();
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, Request_TAKE_PHOTO);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.image_zoom_in) {
            this.mLongEventType = LongEventType.ZoomIn;
        }
        if (view.getId() == R.id.image_zoom_out) {
            this.mLongEventType = LongEventType.ZoomOut;
        }
        if (view.getId() == R.id.image_rotate_left) {
            this.mLongEventType = LongEventType.rotateLeft;
        }
        if (view.getId() == R.id.image_rotate_right) {
            this.mLongEventType = LongEventType.rotateRight;
        }
        mAutoIncrement = true;

        new RptUpdate().run();

        return false;
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int id = view.getId();

        if (id == R.id.image_zoom_in || id == R.id.image_zoom_out || id == R.id.image_rotate_right ||
                id == R.id.image_rotate_left && mAutoIncrement) {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                mAutoIncrement = false;
                this.mLongEventType = null;
            }
        }

        return false;
    }

    private static class ViewHolder {

        Uri mUriPhotoPath;
        ImageView mButtonZoomIn;
        ImageView mButtonZoomOut;
        ImageView mButtonRotateLeft;
        ImageView mButtonRoteteRight;
        ImageView mButtonFinish;
        ImageView mButtonRemove;
        ImageView mImagePhoto;

        ImageView mImageInstagram;
        ImageView mImageFacebook;
        ImageView mImageTwitter;
        ImageView mImageWhatszap;

        LinearLayout mLinearSharePanel;
        LinearLayout mLinearControlPanel;

        RelativeLayout mRelativePhotoContent;
    }

    private class RptUpdate implements Runnable {

        @Override
        public void run() {
            if (mAutoIncrement) {
                mRepeatUpdateHandler.postDelayed(new RptUpdate(), 50);
            }

            if (mLongEventType != null) {
                switch (mLongEventType) {
                    case ZoomIn:
                        ImageUtils.handleZoonIn(mImageSelected);
                        break;
                    case ZoomOut:
                        ImageUtils.handleZoonOut(mImageSelected);
                        break;
                    case rotateLeft:
                        ImageUtils.handleRotateLeft(mImageSelected);
                        break;
                    case rotateRight:
                        ImageUtils.handleRotateRight(mImageSelected);
                        break;
                }
            }
        }
    }
}
