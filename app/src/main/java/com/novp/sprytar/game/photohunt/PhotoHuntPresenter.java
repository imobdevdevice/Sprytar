package com.novp.sprytar.game.photohunt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;

import com.novp.sprytar.BuildConfig;
import com.novp.sprytar.data.SpSession;
import com.novp.sprytar.data.model.Question;
import com.novp.sprytar.data.model.VenueActivity;
import com.novp.sprytar.network.SpResult;
import com.novp.sprytar.network.SpService;
import com.novp.sprytar.presentation.BasePresenter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.inject.Inject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage;

public class PhotoHuntPresenter extends BasePresenter<PhotoHuntView> {

    private final Context context;
    private final SpService spService;
    private final SpSession spSession;
    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private VenueActivity venueActivity;
    private int venueId;
    private Question question;

    private File imageFile;
    private Uri currentImageUri;

    @Inject
    PhotoHuntPresenter(Context context, SpService spService, SpSession spSession) {
        this.context = context;
        this.spService = spService;
        this.spSession = spSession;
    }

    @Override
    public void onDestroyed() {
    }

    public void setVenueActivity(VenueActivity venueActivity, int venueId, Question question) {
        this.venueActivity = venueActivity;
        this.venueId = venueId;
        this.question = question;

        getMvpView().setQuestion(this.question);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    public Uri getImageUri() throws IOException {
        createImageFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            currentImageUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + "" +
                    ".provider", imageFile);
        } else {
            currentImageUri = Uri.fromFile(imageFile);
        }
        return currentImageUri;
    }

    private void createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        imageFile = File.createTempFile(imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */);
    }

    void showHint() {
        getMvpView().showHint(question.getHint());
    }

    public void checkAnswer(android.location.Location location) {
        /*
        android.location.Location questionlocation = new android.location.Location("questionLocation");
        questionlocation.setLatitude(question.getLatitude());
        questionlocation.setLongitude(question.getLongitude());

        if (location.distanceTo(questionlocation) <= 10) {
            sendResultToServer(true);
            getMvpView().hideLoadingIndicator();
            getMvpView().showResultActivity(currentImageUri.toString(), true, question);
        } else {
            sendResultToServer(false);
            getMvpView().hideLoadingIndicator();
            getMvpView().showResultActivity(currentImageUri.toString(), false, question);
        }
        */
    }

    public void onCameraResult() {
        getMvpView().showLoadingIndicator();

        final RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"),
                imageFile);

        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "test.jpg",
                requestFile);
        compositeSubscription.add(spService
                .checkImage(body, question.getGlobalId(), question.getLatitude(),question.getLongitude())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SpResult<Boolean>>() {
                    @Override
                    public void call(SpResult<Boolean> spResult) {
                        getMvpView().hideLoadingIndicator();
                        if (spResult.isSuccess()) {
                            boolean checkOk = spResult.getData();
                            sendResultToServer(checkOk);
                            if (checkOk) {
                                getMvpView().showResultActivity(currentImageUri.toString(), true, question);
                            } else {
                                getMvpView().showResultActivity(currentImageUri.toString(), false, question);
                            }
                        } else {
                            getMvpView().showResultActivity(currentImageUri.toString(), false, question);
                           // getMvpView().showError("Cannot get response from the server");
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getMvpView().hideLoadingIndicator();
                        sendResultToServer(false);
                        getMvpView().showResultActivity(currentImageUri.toString(), false, question);
                       // getMvpView().showError(throwable.getLocalizedMessage());
                    }
                }));

     //   getMvpView().checkPhotoLocation();
    }

    private void sendResultToServer(boolean questionStatus) {
        compositeSubscription.add(spService
                .sendAnsweredQuestion(
                        spSession.getUserId(),
                        venueId,
                        venueActivity.getId(),
                        venueActivity.getGameTypeId(),
                        question.getGlobalId(),
                        questionStatus ? 1 : 0,
                        question.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SpResult>() {
                    @Override
                    public void call(SpResult response) {
                        if (!response.isSuccess()) {
                            // getMvpView().showError("Error: " + response.getMessage());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                      //  getMvpView().showError(throwable.getLocalizedMessage());
                    }
                }));
    }

    private void resizeCompressFile() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            try {
                bitmap = rotateImageIfRequired(bitmap, imageFile.getAbsolutePath());
            } catch (Exception e) {
            }

            int reqWidth = 1000;
            int reqHeight = 750;

            if (bitmap.getWidth() <= reqWidth || bitmap.getHeight() <= reqHeight) {
                return;
            }

            Matrix m = new Matrix();
            m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()), new RectF(0,
                    0, reqWidth, reqHeight), Matrix.ScaleToFit.CENTER);
            Bitmap bitmapResult = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap
                    .getHeight(), m, true);

            FileOutputStream outputStream = new FileOutputStream(imageFile);

            bitmapResult.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap rotateImageIfRequired(Bitmap img, String path) throws IOException {
        ExifInterface ei = new ExifInterface(path);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }
}
