// information about server communication. This sample webservice is provided by Wikitude and returns random dummy places near given location
var ServerInformation = {
	POIDATA_SERVER: "https://example.wikitude.com/GetSamplePois/",
	POIDATA_SERVER_ARG_LAT: "lat",
	POIDATA_SERVER_ARG_LON: "lon",
	POIDATA_SERVER_ARG_NR_POIS: "nrPois"
};

var World = {
	loaded: false,
	rotating: false,
  markerObject:null,
// you may request new data from server periodically, however: in this sample data is only requested once
	isRequestingData: false,

	// true once data was fetched
	initiallyLoadedData: false,

	// different POI-Marker assets
	markerDrawable_idle: null,
	markerDrawable_selected: null,
	markerDrawable_directionIndicator: null,

	// list of AR.GeoObjects that are currently shown in the scene / World
	markerList: [],

	// The last selected marker
	currentMarker: null,

	locationUpdateCounter: 0,
	updatePlacemarkDistancesEveryXLocationUpdates: 10,

	// called to inject new POI data
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {
	AR.context.destroyAll();

   World.markerDrawable_directionIndicator = new AR.ImageResource("assets/arrow.png");

		// empty list of visible markers
		World.markerList = [];
    // loop through POI-information and create an AR.GeoObject (=Marker) per POI
		for (var currentPlaceNr = 0; currentPlaceNr < poiData.length; currentPlaceNr++) {
			var singlePoi = {
				"id": poiData[currentPlaceNr].id,
				"latitude": parseFloat(poiData[currentPlaceNr].latitude),
				"longitude": parseFloat(poiData[currentPlaceNr].longitude),
				"altitude": parseFloat(poiData[currentPlaceNr].altitude),
				"title": poiData[currentPlaceNr].name,
				"description": poiData[currentPlaceNr].description
			};
			markerObject = new Marker(singlePoi);
			World.markerList.push(markerObject);
    }

		// updates distance information of all placemarks
		World.updateDistanceToUserValues();
		World.updateStatusMessage(currentPlaceNr + ' places loaded');
	},


	// sets/updates distances of all makers so they are available way faster than calling (time-consuming) distanceToUser() method all the time
    	updateDistanceToUserValues: function updateDistanceToUserValuesFn() {
      for (var i = 0; i < World.markerList.length; i++) {
          World.markerList[i].distanceToUser = World.markerList[i].obj.locations[0].distanceToUser();
    			//World.markerList[i].firstTimeDistance = 10;
    			//World.markerList[i].updateSize(World.markerList[i]);
    		}
    	},

    	// updates status message shon in small "i"-button aligned bottom center
    	updateStatusMessage: function updateStatusMessageFn(message, isWarning) {

    		var themeToUse = isWarning ? "e" : "c";
    		var iconToUse = isWarning ? "alert" : "info";

    		$("#status-message").html(message);
    		$("#popupInfoButton").buttonMarkup({
    			theme: themeToUse
    		});
    		$("#popupInfoButton").buttonMarkup({
    			icon: iconToUse
    		});
    	},

    	// location updates, fired every time you call architectView.setLocation() in native environment
    	locationChanged: function locationChangedFn(lat, lon, alt, acc) {

    		// request data if not already present
    		if (!World.initiallyLoadedData) {
    			//World.requestDataFromServer(lat, lon);
    			//World.requestDataFromLocal(lat, lon);
    			World.initiallyLoadedData = true;
    		//} else if (World.locationUpdateCounter === 0) {
    		} else {
    			// update placemark distance information frequently, you max also update distances only every 10m with some more effort
    			World.updateDistanceToUserValues();
    		}

    		// helper used to update placemark information every now and then (e.g. every 10 location upadtes fired)
    		World.locationUpdateCounter = (++World.locationUpdateCounter % World.updatePlacemarkDistancesEveryXLocationUpdates);
    	},

    	// fired when user pressed maker in cam
    	  onMarkerSelected: function onMarkerSelectedFn(marker) {
    		World.currentMarker = marker;
        // update panel values
    		$("#poi-detail-title").html(marker.poiData.title);
    		$("#poi-detail-description").html(marker.poiData.description);
        /* It's ok for AR.Location subclass objects to return a distance of `undefined`. In case such a distance was calculated when all distances were queried in `updateDistanceToUserValues`, we recalcualte this specific distance before we update the UI. */
    		if( undefined == marker.distanceToUser ) {
    			marker.distanceToUser = marker.markerObject.locations[0].distanceToUser();
    		}
    		var distanceToUserValue = (marker.distanceToUser > 999) ? ((marker.distanceToUser / 1000).toFixed(2) + " km") : (Math.round(marker.distanceToUser) + " m");

    		$("#poi-detail-distance").html(distanceToUserValue);
    		// show panel
    		$("#panel-poidetail").panel("open", 123);

    		$( ".ui-panel-dismiss" ).unbind("mousedown");

    		$("#panel-poidetail").on("panelbeforeclose", function(event, ui) {
    			World.currentMarker.setDeselected(World.currentMarker);
    		});
    	},

    	// screen was clicked but no geo-object was hit
    	onScreenClick: function onScreenClickFn() {
    		//$("#informationModalTap").style.display = "none";
    		$("#informationModalTap").hide();
    	},

    	// returns distance in meters of placemark with maxdistance * 1.1
    	getMaxDistance: function getMaxDistanceFn() {

    		// sort palces by distance so the first entry is the one with the maximum distance
    		World.markerList.sort(World.sortByDistanceSortingDescending);

    		// use distanceToUser to get max-distance
    		var maxDistanceMeters = World.markerList[0].distanceToUser;

    		// return maximum distance times some factor >1.0 so ther is some room left and small movements of user don't cause places far away to disappear
    		return maxDistanceMeters * 1.1;
    	},

    	// request POI data
    	requestDataFromServer: function requestDataFromServerFn(lat, lon) {

    		// set helper var to avoid requesting places while loading
    		World.isRequestingData = true;
    		World.updateStatusMessage('Requesting places from web-service');

    		// server-url to JSON content provider
    		var serverUrl = ServerInformation.POIDATA_SERVER + "?" + ServerInformation
    		.POIDATA_SERVER_ARG_LAT + "=" + lat + "&" + ServerInformation.POIDATA_SERVER_ARG_LON + "=" + lon + "&" + ServerInformation.POIDATA_SERVER_ARG_NR_POIS + "=5";

    		var jqxhr = $.getJSON(serverUrl, function(data) {
    			World.loadPoisFromJsonData(data);
    		})
    			.error(function(err) {
    				World.updateStatusMessage("Invalid web-service response.", true);
    				World.isRequestingData = false;
    			})
    			.complete(function() {
    				World.isRequestingData = false;
    			});
    	},

    	// request POI data
    	requestDataFromLocal: function requestDataFromLocalFn(lat, lon) {
    		World.loadPoisFromJsonData(myJsonData);
    	},

    	// helper to sort places by distance
    	sortByDistanceSorting: function(a, b) {
    		return a.distanceToUser - b.distanceToUser;
    	},

    	// helper to sort places by distance, descending
    	sortByDistanceSortingDescending: function(a, b) {
    		return b.distanceToUser - a.distanceToUser;
    	},


// you may request new data from server periodically, however: in this sample data is only requested once
	isRequestingData: false,

	// true once data was fetched
	initiallyLoadedData: false,

	// different POI-Marker assets
	markerDrawable_idle: null,
	markerDrawable_selected: null,
	markerDrawable_directionIndicator: null,

	// list of AR.GeoObjects that are currently shown in the scene / World
	markerList: [],

	// The last selected marker
	currentMarker: null,

	locationUpdateCounter: 0,
	updatePlacemarkDistancesEveryXLocationUpdates: 10,

	// called to inject new POI data
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {
	AR.context.destroyAll();

   World.markerDrawable_directionIndicator = new AR.ImageResource("assets/arrow.png");

		// empty list of visible markers
		World.markerList = [];
    // loop through POI-information and create an AR.GeoObject (=Marker) per POI
		for (var currentPlaceNr = 0; currentPlaceNr < poiData.length; currentPlaceNr++) {
			var singlePoi = {
				"id": poiData[currentPlaceNr].id,
				"latitude": parseFloat(poiData[currentPlaceNr].latitude),
				"longitude": parseFloat(poiData[currentPlaceNr].longitude),
				"altitude": parseFloat(poiData[currentPlaceNr].altitude),
				"title": poiData[currentPlaceNr].name,
				"description": poiData[currentPlaceNr].description
			};
			World.markerList.push(new Marker(singlePoi));
    }

		// updates distance information of all placemarks
		World.updateDistanceToUserValues();
		World.updateStatusMessage(currentPlaceNr + ' places loaded');
	},


	// sets/updates distances of all makers so they are available way faster than calling (time-consuming) distanceToUser() method all the time
    	updateDistanceToUserValues: function updateDistanceToUserValuesFn() {
    		for (var i = 0; i < World.markerList.length; i++) {
    			World.markerList[i].distanceToUser = World.markerList[i].obj.locations[0].distanceToUser();
    		}
    	},

    	// updates status message shon in small "i"-button aligned bottom center
    	updateStatusMessage: function updateStatusMessageFn(message, isWarning) {

    		var themeToUse = isWarning ? "e" : "c";
    		var iconToUse = isWarning ? "alert" : "info";

    		$("#status-message").html(message);
    		$("#popupInfoButton").buttonMarkup({
    			theme: themeToUse
    		});
    		$("#popupInfoButton").buttonMarkup({
    			icon: iconToUse
    		});
    	},

    	// location updates, fired every time you call architectView.setLocation() in native environment
    	locationChanged: function locationChangedFn(lat, lon, alt, acc) {

    		// request data if not already present
    		if (!World.initiallyLoadedData) {
    			//World.requestDataFromServer(lat, lon);
    			//World.requestDataFromLocal(lat, lon);
    			World.initiallyLoadedData = true;
    		//} else if (World.locationUpdateCounter === 0) {
    		} else {
    			// update placemark distance information frequently, you max also update distances only every 10m with some more effort
    			World.updateDistanceToUserValues();
    		}

    		// helper used to update placemark information every now and then (e.g. every 10 location upadtes fired)
    		World.locationUpdateCounter = (++World.locationUpdateCounter % World.updatePlacemarkDistancesEveryXLocationUpdates);
    	},

    	// fired when user pressed maker in cam
    	  onMarkerSelected: function onMarkerSelectedFn(marker) {
    		World.currentMarker = marker;
        // update panel values
    		$("#poi-detail-title").html(marker.poiData.title);
    		$("#poi-detail-description").html(marker.poiData.description);
        /* It's ok for AR.Location subclass objects to return a distance of `undefined`. In case such a distance was calculated when all distances were queried in `updateDistanceToUserValues`, we recalcualte this specific distance before we update the UI. */
    		if( undefined == marker.distanceToUser ) {
    			marker.distanceToUser = marker.markerObject.locations[0].distanceToUser();
    		}
    		var distanceToUserValue = (marker.distanceToUser > 999) ? ((marker.distanceToUser / 1000).toFixed(2) + " km") : (Math.round(marker.distanceToUser) + " m");

    		$("#poi-detail-distance").html(distanceToUserValue);
    		// show panel
    		$("#panel-poidetail").panel("open", 123);

    		$( ".ui-panel-dismiss" ).unbind("mousedown");

    		$("#panel-poidetail").on("panelbeforeclose", function(event, ui) {
    			World.currentMarker.setDeselected(World.currentMarker);
    		});
    	},

    	// screen was clicked but no geo-object was hit
    	onScreenClick: function onScreenClickFn() {
    		//$("#informationModalTap").style.display = "none";
    		$("#informationModalTap").hide();
    	},

    	// returns distance in meters of placemark with maxdistance * 1.1
    	getMaxDistance: function getMaxDistanceFn() {

    		// sort palces by distance so the first entry is the one with the maximum distance
    		World.markerList.sort(World.sortByDistanceSortingDescending);

    		// use distanceToUser to get max-distance
    		var maxDistanceMeters = World.markerList[0].distanceToUser;

    		// return maximum distance times some factor >1.0 so ther is some room left and small movements of user don't cause places far away to disappear
    		return maxDistanceMeters * 1.1;
    	},

    	// request POI data
    	requestDataFromServer: function requestDataFromServerFn(lat, lon) {

    		// set helper var to avoid requesting places while loading
    		World.isRequestingData = true;
    		World.updateStatusMessage('Requesting places from web-service');

    		// server-url to JSON content provider
    		var serverUrl = ServerInformation.POIDATA_SERVER + "?" + ServerInformation
    		.POIDATA_SERVER_ARG_LAT + "=" + lat + "&" + ServerInformation.POIDATA_SERVER_ARG_LON + "=" + lon + "&" + ServerInformation.POIDATA_SERVER_ARG_NR_POIS + "=5";

    		var jqxhr = $.getJSON(serverUrl, function(data) {
    			World.loadPoisFromJsonData(data);
    		})
    			.error(function(err) {
    				World.updateStatusMessage("Invalid web-service response.", true);
    				World.isRequestingData = false;
    			})
    			.complete(function() {
    				World.isRequestingData = false;
    			});
    	},

    	// request POI data
    	requestDataFromLocal: function requestDataFromLocalFn(lat, lon) {
    		World.loadPoisFromJsonData(myJsonData);
    	},

    	// helper to sort places by distance
    	sortByDistanceSorting: function(a, b) {
    		return a.distanceToUser - b.distanceToUser;
    	},

    	// helper to sort places by distance, descending
    	sortByDistanceSortingDescending: function(a, b) {
    		return b.distanceToUser - a.distanceToUser;
    	},


	init: function initFn() {

		//this.createModelAtLocation();
	},

	createModelAtLocation: function createModelAtLocationFn() {

		/*
			First a location where the model should be displayed will be defined. This location will be relativ to the user.	
		*/
		var location = new AR.RelativeLocation(null, 5, 0, -2. );

		/*
			Next the model object is loaded.
		*/
		var modelEarth = new AR.Model("assets/Sprytar_74BIN_50percentTrans.wt3", {
			onLoaded: this.worldLoaded,
			scale: {
                  				x: 0.1,
                  				y: 0.1,
                  				z: 0.1
                  			},
                  translate: {
                                 x: 0.0,
                                 y: 0.05,
                                 z: 0.0
                  },
                  rotate: {
                                 z: -25
                  }
		});

        var indicatorImage = new AR.ImageResource("assets/arrow.png");

        var indicatorDrawable = new AR.ImageDrawable(indicatorImage, 0.1, {
            verticalAnchor: AR.CONST.VERTICAL_ANCHOR.TOP
        });

		/*
			Putting it all together the location and 3D model is added to an AR.GeoObject.
		*/
		var obj = new AR.GeoObject(location, {
            drawables: {
               cam: [modelEarth],
               indicator: [indicatorDrawable]
            }
        });
	},

	worldLoaded: function worldLoadedFn() {
		World.loaded = true;
		var e = document.getElementById('loadingMessage');
		e.parentElement.removeChild(e);
	}
};



/* forward locationChanges to custom function */
AR.context.onLocationChanged = World.locationChanged;

/* forward clicks in empty area to World */
AR.context.onScreenClick = World.onScreenClick;
