// Generated by CoffeeScript 1.6.3
(function() {
  var clickOnMap, geocoder, gothere, loadMap, map, map_ready, marker, searchMap, showLocation, showLocationInfo, _this;

  _this = this;

  map_ready = false;

  map = null;

  geocoder = null;

  loadMap = function() {
    var initialized;
    initialized = function() {
      console.log('on load');
      if (GBrowserIsCompatible()) {
        map = new GMap2(document.getElementById('map'));
        map.setCenter(new GLatLng(1.362083, 103.819836), 11);
        map.addControl(new GSmallMapControl());
        map.addControl(new GScaleControl());
        geocoder = new GClientGeocoder();
        GEvent.addListener(map, "click", clickOnMap);
        return map_ready = true;
      } else {
        return alert("Map cannot be loaded");
      }
    };
    gothere.setOnLoadCallback(initialized);
    return gothere.load('maps');
  };

  clickOnMap = function(overlay, latlng) {
    if (latlng === null) {
      return;
    }
    return showLocation(latlng);
  };

  searchMap = function() {
    var keyword;
    keyword = $("#search-map").val();
    console.log(keyword);
    return geocoder.getLatLng(keyword, function(latlng) {
      if (latlng === null) {
        return;
      }
      map.setCenter(latlng, 14);
      return showLocation(latlng);
    });
  };

  marker = null;

  showLocation = function(latlng) {
    if (marker !== null) {
      map.removeOverlay(marker);
    }
    marker = new GMarker(latlng);
    marker.bindInfoWindow("Some info");
    map.addOverlay(marker);
    $("#lat").val(latlng.lat());
    $("#lng").val(latlng.lng());
    $("#location").val("");
    return geocoder.getLocations(latlng, showLocationInfo);
  };

  showLocationInfo = function(data) {
    var place, placemark;
    if (!data.hasOwnProperty("Placemark")) {
      return;
    }
    placemark = data['Placemark'];
    if (placemark.length === 0) {
      return;
    }
    console.log(placemark);
    place = placemark[0];
    return $("#location").val(place['address']);
  };

  if (!_this.hasOwnProperty('gothere')) {
    alert('Map is not loaded');
  } else {
    gothere = _this['gothere'];
    loadMap();
  }

  $(document).ready(function() {
    return $("#search-map").keypress(function(e) {
      if (e.keyCode === 13) {
        searchMap();
        return false;
      }
    });
  });

}).call(this);

/*
//@ sourceMappingURL=map.map
*/
