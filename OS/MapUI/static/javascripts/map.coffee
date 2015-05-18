_this = this
map = null
baseIcon = null

display = {
  fire: true
  haze: true
  dengue: true
  weather: true
  shelter: true
  illness: true
  rescue: true
  gas: true
}

clickOnMap = (overlay, latlng) ->
  console.log(latlng.lat() + " * " + latlng.lng())

loadMap = ->
  initialized = ->
    console.log 'on load'
    if not GBrowserIsCompatible()
      alert "browser not supported"
      return
    map = new GMap2(document.getElementById('map'))
    map.setCenter(new GLatLng(1.362083, 103.819836), 12)
    # map.addControl(new GSmallMapControl()) # on top left
    map.addControl(new GScaleControl()) # on bottom left

    # Icons
    baseIcon = new GIcon()
    baseIcon.image = "/static/images/icon-fire.png"
    # baseIcon.shadow = "/static/images/icon-shadow.png"
    baseIcon.iconSize = new GSize(32, 32)
    baseIcon.shadowSize = new GSize(37, 34)
    baseIcon.iconAnchor = new GPoint(16, 16)
    baseIcon.infoWindowAnchor = new GPoint(-5, -5)

    GEvent.addListener(map, "click", clickOnMap)

    loadIncidents()
    setInterval(loadIncidents, 5000)

  gothere.setOnLoadCallback(initialized)
  gothere.load('maps')


if not _this.hasOwnProperty('gothere')
  alert 'Map is not loaded'
else
  gothere = _this['gothere']
  loadMap()
@incidents = []

loadIncidents = ->
  self = @
  $.ajax
    url: "/incident"
    data:
      from: getFromDate()
      to: getToDate()
    dataType: "json"
    success: (ret) ->
      console.log ret
      if ret.hasOwnProperty("data")
        self.incidents = ret["data"]
        displayIncidents()

displayIncidents = ->
  displayShelters()
  displayWeather()
  if polygon != null
    map.removeOverlay(polygon)
  for marker in markerList
    map.removeOverlay(marker)
  for incident in @incidents
    if not display[incident['type']]
      continue
    locations = incident['location']
    gpsLocation = null
    regionLocation = null
    stringLocation = ""
    for location in locations
      if location["type"] == "gps"
        gpsLocation = location
      if location["type"] == "string"
        stringLocation = location["location"]
      if location['type'] == "region"
        regionLocation = location["regions"][0]

    if gpsLocation != null
      displayString = "Location: "
      displayString += stringLocation
      displayString += "<p>"
      displayString += "Remark: "
      displayString += incident['remark']
      createMarker(gpsLocation["lat"], gpsLocation["lng"], displayString, incident['type'])
    if regionLocation != null
      createPolyon(geo[regionLocation].polygon)


closeFloatBox =  ->
  $('#logo').removeClass('active')
  $('.menu-item').removeClass('active')
  $('.float-box').hide()

openFloatBox = (target) ->
  closeFloatBox()
  $('.float-box#' + target + '-box').show()
  if target == 'logo'
    $('.float-box#about-box').show()
    $('#logo').addClass('active')
  else
    $('.menu-item#' + target).addClass('active')


markerList = []
createMarker = (lat, lng, location, type) ->
  console.log "create marker"
  customIcon = new GIcon(baseIcon)
  customIcon.image = "/static/images/icon-" + type + ".png"
  marker = new GMarker(
    new GLatLng(lat, lng),
    icon: customIcon
  )
  map.addOverlay(marker)
  marker.bindInfoWindow("<h3>"+type+"</h3>" + location)
  markerList.push(marker)

polygon = null
createPolyon = (pointList) ->
  console.log pointList

  if polygon != null
    map.removeOverlay polygon

  pointsToDraw = []
  for point in pointList
    pointsToDraw.push new GLatLng(point[0], point[1])
  # Create a simple polyline.

  # polygon = new GPolygon(pointsToDraw, "#ff0000", 5, 0.6, "#0000ff", 0.4)
  polygon = new GPolygon(pointsToDraw, "#0000ff", 0, 0, "#0000ff", 0.4)

  # Add the polyline to the map.
  map.addOverlay polygon


getFromDate = ->
  $slider = $('#slider')
  value = $slider.slider('getValue')[0]
  if value == 365
    return new Date().getTime();
  else
    return getDateBefore(value).getTime()

getToDate = ->
  $slider = $('#slider')
  value = $slider.slider('getValue')[1]
  if value == 365
    return null;
  else
    return getDateBefore(value).getTime()

getCurrentDate = ->
  date = new Date()
  return new Date(date.getFullYear(), date.getMonth(), date.getDate())

getDateBefore = (num) ->
  date = getCurrentDate()
  date.setDate(date.getDate() - 365 + num)
  return date

getDateBeforeDisplay = (num)->
  date = getDateBefore(num)

  ret = date.getDate() + '/'
  ret += (date.getMonth() + 1) + '/'
  ret += date.getFullYear()

  return ret

updateSlider = ->
  $slider = $('#slider')
  values = $slider.slider('getValue')
  console.log values
  from_value = values[0]
  to_value = values[1]
  text_from = "now"
  text_to = "future"
  if from_value < 365
    text_from = getDateBeforeDisplay(from_value)
  if to_value < 365
    text_to = getDateBeforeDisplay(to_value)

  $('#slider-from').html(text_from)
  $('#slider-to').html(text_to)


shelters = [
  [1.3269021065901792, 103.8332255874018]
  [1.3845641808145428, 103.78104052881012]
]

shelterIcons = []

removeShelters = ->
  for sIcon in shelterIcons
    map.removeOverlay(sIcon)


displayShelters = ->
  removeShelters()
  if not display['shelter']
    return
  for shelter in shelters
    console.log "create marker"
    customIcon = new GIcon(baseIcon)
    customIcon.image = "/static/images/icon-shelter.png"
    marker = new GMarker(
      new GLatLng(shelter[0], shelter[1]),
      icon: customIcon
    )
    marker.bindInfoWindow("<h3>Civil Defence shelters</h3>")
    map.addOverlay(marker)
    shelterIcons.push(marker)


weatherList = []

getWeather = ->
  $.ajax
    url: '/weather'
    dataType: "json"
    success: (ret)->
      weatherList = ret


wIcons = []

displayWeather = ->
  for wi in wIcons
    map.removeOverlay(wi)
  if not display['weather']
    return
  for w in weatherList
    console.log "create marker"
    customIcon = new GIcon(baseIcon)
    customIcon.image = "/static/images/icon-temp.png"
    marker = new GMarker(
      new GLatLng(w['lat'], w['lng']),
      icon: customIcon
    )
    marker.bindInfoWindow("<h3>" + w['weather'] + "</h3>" + w['temperature'] + "°C")
    map.addOverlay(marker)
    shelterIcons.push(marker)



$(document).ready ->
  getWeather()

  # CREATE GEO LIST
  # FOR TESING ONLY

  $geoList = $("#geo-list")
  for id, community of geo
    console.log id
    $option = $("<option />").val(id).text(community.name)
    $geoList.append($option)

  $("#load-geo").click ->
    id = parseInt $geoList.val()
    createPolyon(geo[id].polygon)

  $('input').iCheck
    checkboxClass: 'icheckbox_square-blue',
    radioClass: 'iradio_square-blue',
    increaseArea: '20%' # optional

  $('input').on('ifChanged', ->
    display['fire'] = $('#incident-fire').is(":checked")
    display['haze'] = $('#incident-haze').is(":checked")
    display['dengue'] = $('#incident-dengue').is(":checked")
    display['illness'] = $('#incident-illness').is(":checked")
    display['rescue'] = $('#incident-rescue').is(":checked")
    display['gas'] = $('#incident-gas').is(":checked")
    display['weather'] = $('#icon-weather').is(":checked")
    display['shelter'] = $('#icon-shelter').is(":checked")
    displayIncidents()
  )

  openMenu = ->
    $('#extended-menu').show()
    $('#extend-menu-open').hide()

  closeMenu = ->
    $('#extended-menu').hide()
    $('#extend-menu-open').show()
    closeFloatBox()

  $('#extend-menu-open').on('click', openMenu)
  $('#extend-menu-close').on('click', closeMenu)

  $('#logo').on('click', ->
    openFloatBox('logo')
  )

  $('#incident').on('click', ->
    openFloatBox('incident')
  )

  $('#timeline').on('click', ->
    openFloatBox('timeline')
  )

  $('#subscribe').on('click', ->
    openFloatBox('subscribe')
  )

  $('#about-box').on('mouseleave', closeFloatBox)

  $slider = $('#slider')

  $slider.slider({
    min: 0
    max: 365
    step: 1
    value: [365,365]
    range: true,
    tooltip: 'hide'
  }).on('slide', ->
    updateSlider()
  ).on('slideStop', ->
    updateSlider()
    loadIncidents()
  )