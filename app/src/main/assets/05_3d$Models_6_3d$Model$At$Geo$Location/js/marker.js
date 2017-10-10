var kMarker_AnimationDuration_ChangeDrawable = 500;
var kMarker_AnimationDuration_Resize = 1000;

function Marker(poiData) {
    this.poiData = poiData;
    this.isSelected = false;
    /*
        With AR.PropertyAnimations you are able to animate almost any property of ARchitect objects. This sample will animate the opacity of both background drawables so that one will fade out while the other one fades in. The scaling is animated too. The marker size changes over time so the labels need to be animated too in order to keep them relative to the background drawable. AR.AnimationGroups are used to synchronize all animations in parallel or sequentially.
    */
    //this.x = 0.5;
    //this.y = 0.5;
    //this.z = 0.5;

    this.animationGroup_idle = null;
    this.animationGroup_selected = null;

    // create the AR.GeoLocation from the poi data
    this.markerLocation = new AR.GeoLocation(poiData.latitude, poiData.longitude, poiData.altitude);
    /**
      First a location where the model should be displayed will be defined. This location will be relativ to the user.
    */
    var location = new AR.RelativeLocation(this.markerLocation , 5, 0, -2.);
    /*
    	Next the model object is loaded.
    */
    this.modelEarth = new AR.Model("assets/FloatingSpryte.wt3", {
    	onLoaded: World.worldLoaded(),
    	scale: {
          		x: 0.07,
                y: 0.07,
          		z: 0.07
          },
      translate: {
                  x: 0.0,
                  y: 0.0,
                  z: 0.0
      },
      rotate: {
                 z: 25

      },onClick : null//Marker.prototype.getOnClickTrigger(this)
    }
    );
     /* var animation = new AR.ModelAnimation(modelEarth, "Base Stack");

              // start the animation
             animation.start();
*/
/* var animation = new AR.ModelAnimation(modelEarth, "Base Stack");

         // start the animation
        animation.start();*/

    var indicatorImage = new AR.ImageResource("assets/arrow.png");

    this.indicatorDrawable = new AR.ImageDrawable(indicatorImage, 0.1, {
        verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
    });

    /*	this.targetCollectionResource = new AR.TargetCollectionResource("assets/tracker.wtc", {
                onLoaded: function () {
                    World.resourcesLoaded = true;
                    this.loadingStep;
                }

      this.tracker = new AR.ImageTracker(this.targetCollectionResource, {
                 onTargetsLoaded: this.loadingStep
             });*/

    /*
            Create an AR.ImageDrawable using the AR.ImageResource for the direction indicator which was created in the World. Set options regarding the offset and anchor of the image so that it will be displayed correctly on the edge of the screen.
        */

    // create an AR.Label for the marker's title
    this.titleLabel = new AR.Label(poiData.title.trunc(10), 1, {
        zOrder: 1,
        translate: {
            y: 0.55
        },
        style: {
            textColor: '#FFFFFF',
            fontStyle: AR.CONST.FONT_STYLE.BOLD
        }
    });
    // create an AR.Label for the marker's description
    this.descriptionLabel = new AR.Label(poiData.description.trunc(15), 0.8, {
        zOrder: 1,
        translate: {
            y: -0.55
        },
        style: {
            textColor: '#FFFFFF'
        }
    });


    this.radarCircle = new AR.Circle(0.03, {
        horizontalAnchor: AR.CONST.HORIZONTAL_ANCHOR.CENTER,
        opacity: 0.8,
        style: {
            fillColor: "#ffffff"
        }
    });
    this.radarCircleSelected = new AR.Circle(0.05, {
        horizontalAnchor: AR.CONST.HORIZONTAL_ANCHOR.CENTER,
        opacity: 0.8,
        style: {
            fillColor: "#0066ff"
        }
    });

    this.radardrawables = [];
    this.radardrawables.push(this.radarCircle);

    this.radardrawablesSelected = [];
    this.radardrawablesSelected.push(this.radarCircleSelected);

    /*
      Putting it all together the location and 3D model is added to an AR.GeoObject.
    */
    this.obj = new AR.GeoObject(location, {
            drawables: {
               cam: [this.modelEarth],
               indicator: this.indicatorDrawable,
               radar: this.radardrawables
            }
    });

    return this;
}

Marker.prototype.getOnClickTrigger = function(marker) {
    /*
        The setSelected and setDeselected functions are prototype Marker functions.
        Both functions perform the same steps but` inverted.
    */
    return function() {
        console.log(marker.poiData);
        if (!Marker.prototype.isAnyAnimationRunning(marker)) {
            if (marker.isSelected) {
                Marker.prototype.setDeselected(marker);
            } else {
                Marker.prototype.setSelected(marker);
                //try {
                //    World.onMarkerSelected(marker);
                //} catch (err) {
                //    alert(err);
                //}
            }
        } else {
            AR.logger.debug('a animation is already running');
        }
        return true;
    };
};

/*
    Property Animations allow constant changes to a numeric value/property of an object, dependent on start-value, end-value and the duration of the animation. Animations can be seen as functions defining the progress of the change on the value. The Animation can be parametrized via easing curves.
*/

Marker.prototype.setSelected = function(marker) {

    marker.isSelected = true;
    //if (marker.animationGroup_selected === null) {
        var sx = new AR.PropertyAnimation(marker.modelEarth, "scale.x", null, 0.3, 1500, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_IN_QUAD
        });
        var sy = new AR.PropertyAnimation(marker.modelEarth, "scale.y", null, 0.3, 1500, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_IN_QUAD
        });
        var sz = new AR.PropertyAnimation(marker.modelEarth, "scale.z", null, 0.3, 1500, {
            type: AR.CONST.EASING_CURVE_TYPE.EASE_OUT_IN_QUAD
        });
        marker.animationGroup_selected = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [sx, sy, sz]);
        marker.animationGroup_selected.start();
    //}
};

Marker.prototype.setDeselected = function(marker) {
    marker.isSelected = false;
    //if (marker.animationGroup_idle === null) {
         var sx = new AR.PropertyAnimation(marker.modelEarth, "scale.x", null, 0.5, 1500, {
             type: AR.CONST.EASING_CURVE_TYPE.EASE_IN_OUT_QUAD
         });
         var sy = new AR.PropertyAnimation(marker.modelEarth, "scale.y", null, 0.5, 1500, {
             type: AR.CONST.EASING_CURVE_TYPE.EASE_IN_OUT_QUAD
         });
         var sz = new AR.PropertyAnimation(marker.modelEarth, "scale.z", null, 0.5, 1500, {
               type: AR.CONST.EASING_CURVE_TYPE.EASE_IN_OUT_QUAD
         });
        marker.animationGroup_selected = new AR.AnimationGroup(AR.CONST.ANIMATION_GROUP_TYPE.PARALLEL, [sx, sy, sz]);
        marker.animationGroup_selected.start();
    //}
};

Marker.prototype.isAnyAnimationRunning = function(marker) {
    if (marker.animationGroup_idle === null || marker.animationGroup_selected === null) {
        return false;
    } else {
        if ((marker.animationGroup_idle.isRunning() === true) || (marker.animationGroup_selected.isRunning() === true)) {
            return true;
        } else {
            return false;
        }
    }
};

// will truncate all strings longer than given max-length "n". e.g. "foobar".trunc(3) -> "foo..."
String.prototype.trunc = function(n) {
    return this.substr(0, n - 1) + (this.length > n ? '...' : '');
};