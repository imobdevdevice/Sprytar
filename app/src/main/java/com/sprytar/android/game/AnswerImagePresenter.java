package com.sprytar.android.game;

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

import com.sprytar.android.BuildConfig;
import com.sprytar.android.data.SpSession;
import com.sprytar.android.data.model.Answer;
import com.sprytar.android.data.model.Question;
import com.sprytar.android.events.SendQuestionResultEvent;
import com.sprytar.android.network.SpResult;
import com.sprytar.android.network.SpService;
import com.sprytar.android.presentation.BasePresenter;

import org.greenrobot.eventbus.EventBus;

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

public class AnswerImagePresenter extends BasePresenter<AnswerImageView> {

    private final Context context;
    private final SpService spService;
    private final SpSession spSession;
    private final EventBus bus;

    private CompositeSubscription compositeSubscription = new CompositeSubscription();

    private Question question;

    private boolean questionAnswered;

    private boolean questionAnsweredCorrectly;

    private Answer correctAnswer;

    private Answer userAnswer;

    private File imageFile;
    private Uri currentImageUri;

    @Inject
    AnswerImagePresenter(Context context, SpService spService, SpSession spSession, EventBus bus) {
        this.context = context;
        this.spService = spService;
        this.spSession = spSession;
        this.bus = bus;
    }

    @Override
    public void attachView(AnswerImageView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        if (compositeSubscription != null) {
            compositeSubscription.clear();
        }
    }

    @Override
    public void onDestroyed() {
    }

    public void setQuestion(Question question){
        this.question = question;

        getMvpView().showQuestion(question, questionAnswered, questionAnsweredCorrectly);
    }

    public void onAnswerClick(Answer answer) {

        if (questionAnswered) {
            return;
        }

        questionAnswered = true;
        userAnswer = answer;
        if (userAnswer.equals(correctAnswer)) {
            questionAnsweredCorrectly = true;
        }

        getMvpView().showQuestion(question, questionAnswered, questionAnsweredCorrectly);
    }

    public void onNavigationButtonClick(){
//        if (questionAnswered) {
            getMvpView().nextQuestion();
//        } else {
//            getMvpView().close();
//        }
    }

    public void onShowHintClick() {
        String hint = question.getHint();
        if (hint != null) {
            getMvpView().showHint(hint);
        }
    }

    public void onCameraResult() {

        getMvpView().showLoadingIndicator();

        resizeCompressFile();
        final RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"),
                imageFile);

        MultipartBody.Part body = MultipartBody.Part.createFormData("image", "test.jpg",
                requestFile);

        double currentLat = question.getLatitude() == 0.0 ? Double.valueOf(spSession
                .getCurrentLocationLatitude()) : question.getLatitude();

        double currentLong = question.getLongitude() == 0.0 ? Double.valueOf(spSession
                .getCurrentLocationLongitude()) : question.getLongitude();

        compositeSubscription.add(spService
                .checkImage(body, question.getGlobalId(), currentLat,currentLong)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<SpResult<Boolean>>() {
                    @Override
                    public void call(SpResult<Boolean> spResult) {
                        getMvpView().showLoadingIndicator();
                        if (spResult.isSuccess()) {
                            boolean checkOk = spResult.getData();
                            sendResultToServer(checkOk);
                            if (checkOk) {
                                getMvpView().showCorrectAnswerActivity(currentImageUri, true);
                            } else {
                                getMvpView().showCorrectAnswerActivity(currentImageUri, false);
                            }
                        } else {
                           // getMvpView().showError("Cannot get response from the server");
                            getMvpView().showWaitingDialog();
                        }
                        getMvpView().hideLoadingIndicator();
                        deleteImageFile();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        getMvpView().hideLoadingIndicator();
                     //   getMvpView().showError(throwable.getLocalizedMessage());
                        deleteImageFile();
                        getMvpView().showWaitingDialog();
                    }
                }));

    }

    private void sendResultToServer(boolean questionStatus) {
        if (!spSession.isLoggedIn()) {
            return;
        }
        bus.post(new SendQuestionResultEvent(question.getGlobalId(),question.getId(),questionStatus));
    }

    private void deleteImageFile() {
        if (imageFile.exists()) {
            imageFile.delete();
        }
    }

    private void resizeCompressFile() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;

            Bitmap bitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            try{
                bitmap = rotateImageIfRequired(bitmap, imageFile.getAbsolutePath());
            } catch (Exception e){}

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

    public Uri getImageUri() throws IOException {
        createImageFile();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
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

    public Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                true);
    }

    public void setExplainerCompleted(boolean isCompleted){
        spSession.setQuizExplainerCompleted(isCompleted);
    }

    public boolean getExplainerStatus(){
        return spSession.isExplainerCompleted();
    }

}
