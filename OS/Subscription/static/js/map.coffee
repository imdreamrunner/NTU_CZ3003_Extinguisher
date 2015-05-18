_this = this
map_ready = false
map = null
geocoder = null

loadMap = ->
  initialized = ->
    console.log 'on load'
    if GBrowserIsCompatible()
      map = new GMap2(document.getElementById('map'))
      map.setCenter(new GLatLng(1.362083, 103.819836), 11)
      map.addControl(new GSmallMapControl()) # on top left
      map.addControl(new GScaleControl()) # on bottom left
      geocoder = new GClientGeocoder()
      GEvent.addListener(map, "click", clickOnMap)
      map_ready = true
    else
      alert "Map cannot be loaded"
  gothere.setOnLoadCallback(initialized)
  gothere.load('maps')

clickOnMap = (overlay, latlng) ->
  if latlng == null
    return
  showLocation(latlng)

searchMap = ->
  keyword = $("#search-map").val()
  console.log keyword
  geocoder.getLatLng(keyword, (latlng) ->
    if latlng == null
      return
    map.setCenter(latlng, 14)
    showLocation(latlng)
  )

marker = null

showLocation = (latlng) ->
  if marker != null
    map.removeOverlay(marker)
  marker = new GMarker(latlng)
  marker.bindInfoWindow("Some info")
  map.addOverlay(marker)
  $("#lat").val(latlng.lat())
  $("#lng").val(latlng.lng())
  $("#location").val ""
  geocoder.getLocations(latlng, showLocationInfo)

showLocationInfo = (data) ->
  if !data.hasOwnProperty("Placemark")
    return
  placemark = data['Placemark']
  if placemark.length == 0
    return
  console.log placemark
  place = placemark[0]
  console.log place['address']
  $("#location").val place['address']



if not _this.hasOwnProperty('gothere')
  alert 'Map is not loaded'
else
  gothere = _this['gothere']
  loadMap()

$(document).ready ->
  $("#search-map").keypress (e)->
    if e.keyCode == 13
      searchMap()
      return false