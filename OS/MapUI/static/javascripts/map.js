// Generated by CoffeeScript 1.6.3
(function() {
  var baseIcon, clickOnMap, closeFloatBox, createMarker, createPolyon, display, displayIncidents, displayShelters, displayWeather, getCurrentDate, getDateBefore, getDateBeforeDisplay, getFromDate, getToDate, getWeather, gothere, loadIncidents, loadMap, map, markerList, openFloatBox, polygon, removeShelters, shelterIcons, shelters, updateSlider, wIcons, weatherList, _this;

  _this = this;

  map = null;

  baseIcon = null;

  display = {
    fire: true,
    haze: true,
    dengue: true,
    weather: true,
    shelter: true,
    illness: true,
    rescue: true,
    gas: true
  };

  clickOnMap = function(overlay, latlng) {
    return console.log(latlng.lat() + " * " + latlng.lng());
  };

  loadMap = function() {
    var initialized;
    initialized = function() {
      console.log('on load');
      if (!GBrowserIsCompatible()) {
        alert("browser not supported");
        return;
      }
      map = new GMap2(document.getElementById('map'));
      map.setCenter(new GLatLng(1.362083, 103.819836), 12);
      map.addControl(new GScaleControl());
      baseIcon = new GIcon();
      baseIcon.image = "/static/images/icon-fire.png";
      baseIcon.iconSize = new GSize(32, 32);
      baseIcon.shadowSize = new GSize(37, 34);
      baseIcon.iconAnchor = new GPoint(16, 16);
      baseIcon.infoWindowAnchor = new GPoint(-5, -5);
      GEvent.addListener(map, "click", clickOnMap);
      loadIncidents();
      return setInterval(loadIncidents, 5000);
    };
    gothere.setOnLoadCallback(initialized);
    return gothere.load('maps');
  };

  if (!_this.hasOwnProperty('gothere')) {
    alert('Map is not loaded');
  } else {
    gothere = _this['gothere'];
    loadMap();
  }

  this.incidents = [];

  loadIncidents = function() {
    var self;
    self = this;
    return $.ajax({
      url: "/incident",
      data: {
        from: getFromDate(),
        to: getToDate()
      },
      dataType: "json",
      success: function(ret) {
        console.log(ret);
        if (ret.hasOwnProperty("data")) {
          self.incidents = ret["data"];
          return displayIncidents();
        }
      }
    });
  };

  displayIncidents = function() {
    var displayString, gpsLocation, incident, location, locations, marker, regionLocation, stringLocation, _i, _j, _k, _len, _len1, _len2, _ref, _results;
    displayShelters();
    displayWeather();
    if (polygon !== null) {
      map.removeOverlay(polygon);
    }
    for (_i = 0, _len = markerList.length; _i < _len; _i++) {
      marker = markerList[_i];
      map.removeOverlay(marker);
    }
    _ref = this.incidents;
    _results = [];
    for (_j = 0, _len1 = _ref.length; _j < _len1; _j++) {
      incident = _ref[_j];
      if (!display[incident['type']]) {
        continue;
      }
      locations = incident['location'];
      gpsLocation = null;
      regionLocation = null;
      stringLocation = "";
      for (_k = 0, _len2 = locations.length; _k < _len2; _k++) {
        location = locations[_k];
        if (location["type"] === "gps") {
          gpsLocation = location;
        }
        if (location["type"] === "string") {
          stringLocation = location["location"];
        }
        if (location['type'] === "region") {
          regionLocation = location["regions"][0];
        }
      }
      if (gpsLocation !== null) {
        displayString = "Location: ";
        displayString += stringLocation;
        displayString += "<p>";
        displayString += "Remark: ";
        displayString += incident['remark'];
        createMarker(gpsLocation["lat"], gpsLocation["lng"], displayString, incident['type']);
      }
      if (regionLocation !== null) {
        _results.push(createPolyon(geo[regionLocation].polygon));
      } else {
        _results.push(void 0);
      }
    }
    return _results;
  };

  closeFloatBox = function() {
    $('#logo').removeClass('active');
    $('.menu-item').removeClass('active');
    return $('.float-box').hide();
  };

  openFloatBox = function(target) {
    closeFloatBox();
    $('.float-box#' + target + '-box').show();
    if (target === 'logo') {
      $('.float-box#about-box').show();
      return $('#logo').addClass('active');
    } else {
      return $('.menu-item#' + target).addClass('active');
    }
  };

  markerList = [];

  createMarker = function(lat, lng, location, type) {
    var customIcon, marker;
    console.log("create marker");
    customIcon = new GIcon(baseIcon);
    customIcon.image = "/static/images/icon-" + type + ".png";
    marker = new GMarker(new GLatLng(lat, lng), {
      icon: customIcon
    });
    map.addOverlay(marker);
    marker.bindInfoWindow("<h3>" + type + "</h3>" + location);
    return markerList.push(marker);
  };

  polygon = null;

  createPolyon = function(pointList) {
    var point, pointsToDraw, _i, _len;
    console.log(pointList);
    if (polygon !== null) {
      map.removeOverlay(polygon);
    }
    pointsToDraw = [];
    for (_i = 0, _len = pointList.length; _i < _len; _i++) {
      point = pointList[_i];
      pointsToDraw.push(new GLatLng(point[0], point[1]));
    }
    polygon = new GPolygon(pointsToDraw, "#0000ff", 0, 0, "#0000ff", 0.4);
    return map.addOverlay(polygon);
  };

  getFromDate = function() {
    var $slider, value;
    $slider = $('#slider');
    value = $slider.slider('getValue')[0];
    if (value === 365) {
      return new Date().getTime();
    } else {
      return getDateBefore(value).getTime();
    }
  };

  getToDate = function() {
    var $slider, value;
    $slider = $('#slider');
    value = $slider.slider('getValue')[1];
    if (value === 365) {
      return null;
    } else {
      return getDateBefore(value).getTime();
    }
  };

  getCurrentDate = function() {
    var date;
    date = new Date();
    return new Date(date.getFullYear(), date.getMonth(), date.getDate());
  };

  getDateBefore = function(num) {
    var date;
    date = getCurrentDate();
    date.setDate(date.getDate() - 365 + num);
    return date;
  };

  getDateBeforeDisplay = function(num) {
    var date, ret;
    date = getDateBefore(num);
    ret = date.getDate() + '/';
    ret += (date.getMonth() + 1) + '/';
    ret += date.getFullYear();
    return ret;
  };

  updateSlider = function() {
    var $slider, from_value, text_from, text_to, to_value, values;
    $slider = $('#slider');
    values = $slider.slider('getValue');
    console.log(values);
    from_value = values[0];
    to_value = values[1];
    text_from = "now";
    text_to = "future";
    if (from_value < 365) {
      text_from = getDateBeforeDisplay(from_value);
    }
    if (to_value < 365) {
      text_to = getDateBeforeDisplay(to_value);
    }
    $('#slider-from').html(text_from);
    return $('#slider-to').html(text_to);
  };

  shelters = [[1.3269021065901792, 103.8332255874018], [1.3845641808145428, 103.78104052881012]];

  shelterIcons = [];

  removeShelters = function() {
    var sIcon, _i, _len, _results;
    _results = [];
    for (_i = 0, _len = shelterIcons.length; _i < _len; _i++) {
      sIcon = shelterIcons[_i];
      _results.push(map.removeOverlay(sIcon));
    }
    return _results;
  };

  displayShelters = function() {
    var customIcon, marker, shelter, _i, _len, _results;
    removeShelters();
    if (!display['shelter']) {
      return;
    }
    _results = [];
    for (_i = 0, _len = shelters.length; _i < _len; _i++) {
      shelter = shelters[_i];
      console.log("create marker");
      customIcon = new GIcon(baseIcon);
      customIcon.image = "/static/images/icon-shelter.png";
      marker = new GMarker(new GLatLng(shelter[0], shelter[1]), {
        icon: customIcon
      });
      marker.bindInfoWindow("<h3>Civil Defence shelters</h3>");
      map.addOverlay(marker);
      _results.push(shelterIcons.push(marker));
    }
    return _results;
  };

  weatherList = [];

  getWeather = function() {
    return $.ajax({
      url: '/weather',
      dataType: "json",
      success: function(ret) {
        return weatherList = ret;
      }
    });
  };

  wIcons = [];

  displayWeather = function() {
    var customIcon, marker, w, wi, _i, _j, _len, _len1, _results;
    for (_i = 0, _len = wIcons.length; _i < _len; _i++) {
      wi = wIcons[_i];
      map.removeOverlay(wi);
    }
    if (!display['weather']) {
      return;
    }
    _results = [];
    for (_j = 0, _len1 = weatherList.length; _j < _len1; _j++) {
      w = weatherList[_j];
      console.log("create marker");
      customIcon = new GIcon(baseIcon);
      customIcon.image = "/static/images/icon-temp.png";
      marker = new GMarker(new GLatLng(w['lat'], w['lng']), {
        icon: customIcon
      });
      marker.bindInfoWindow("<h3>" + w['weather'] + "</h3>" + w['temperature'] + "°C");
      map.addOverlay(marker);
      _results.push(shelterIcons.push(marker));
    }
    return _results;
  };

  $(document).ready(function() {
    var $geoList, $option, $slider, closeMenu, community, id, openMenu;
    getWeather();
    $geoList = $("#geo-list");
    for (id in geo) {
      community = geo[id];
      console.log(id);
      $option = $("<option />").val(id).text(community.name);
      $geoList.append($option);
    }
    $("#load-geo").click(function() {
      id = parseInt($geoList.val());
      return createPolyon(geo[id].polygon);
    });
    $('input').iCheck({
      checkboxClass: 'icheckbox_square-blue',
      radioClass: 'iradio_square-blue',
      increaseArea: '20%'
    });
    $('input').on('ifChanged', function() {
      display['fire'] = $('#incident-fire').is(":checked");
      display['haze'] = $('#incident-haze').is(":checked");
      display['dengue'] = $('#incident-dengue').is(":checked");
      display['illness'] = $('#incident-illness').is(":checked");
      display['rescue'] = $('#incident-rescue').is(":checked");
      display['gas'] = $('#incident-gas').is(":checked");
      display['weather'] = $('#icon-weather').is(":checked");
      display['shelter'] = $('#icon-shelter').is(":checked");
      return displayIncidents();
    });
    openMenu = function() {
      $('#extended-menu').show();
      return $('#extend-menu-open').hide();
    };
    closeMenu = function() {
      $('#extended-menu').hide();
      $('#extend-menu-open').show();
      return closeFloatBox();
    };
    $('#extend-menu-open').on('click', openMenu);
    $('#extend-menu-close').on('click', closeMenu);
    $('#logo').on('click', function() {
      return openFloatBox('logo');
    });
    $('#incident').on('click', function() {
      return openFloatBox('incident');
    });
    $('#timeline').on('click', function() {
      return openFloatBox('timeline');
    });
    $('#subscribe').on('click', function() {
      return openFloatBox('subscribe');
    });
    $('#about-box').on('mouseleave', closeFloatBox);
    $slider = $('#slider');
    return $slider.slider({
      min: 0,
      max: 365,
      step: 1,
      value: [365, 365],
      range: true,
      tooltip: 'hide'
    }).on('slide', function() {
      return updateSlider();
    }).on('slideStop', function() {
      updateSlider();
      return loadIncidents();
    });
  });

}).call(this);

/*
//@ sourceMappingURL=map.map
*/
