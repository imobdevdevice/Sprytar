package com.novp.sprytar.poi;

import com.novp.sprytar.data.model.Amenity;
import com.novp.sprytar.data.model.PoiFile;
import com.novp.sprytar.data.model.PointOfInterest;
import com.novp.sprytar.presentation.BasePresenter;

import javax.inject.Inject;

public class PoiPresenter extends BasePresenter<PoiView> {

    private final PoiInteractor interactor;

    private PointOfInterest poi;

    @Inject
    PoiPresenter(PoiInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void attachView(PoiView mvpView) {
        super.attachView(mvpView);
    }

    @Override
    public void detachView() {
        super.detachView();
        interactor.unsubscribe();
    }

    @Override
    public void onDestroyed() {
        interactor.unsubscribe();
    }

    public void setPoi(PointOfInterest poi) {
        this.poi = poi;

        if (poi.getPoiFiles() != null) {
            if (poi.getPoiFiles().size() > 0) {
                PoiFile file = poi.getPoiFiles().get(0);
                if (file.getFileType() != null) {
                    if (file.getFileType().equals(PoiFile.AUDIO)) {
                        getMvpView().showAudioPoi(poi);
                    } else {
                        getMvpView().showPoi(poi);
                    }
                }
            }
        }
    }

    public void setAmenity(Amenity amenity) {
        getMvpView().showAmenity(amenity);
    }
}
