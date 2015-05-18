@incidents = []

initialIdMap = {}

loadIncidents = ->
  self = @
  $.ajax
    url: "/incident"
    data:
      rd: window['rd']
    dataType: "json"
    success: (ret) ->
      console.log ret
      if ret.hasOwnProperty("data")
        self.incidents = ret["data"]
        incidents = ret["data"]
        for incident in incidents
          initialIdMap[incident['initialId']] = incident
        displayIncidents()


incidentListTemplate = _.template($("#incident-list-template").html())
displayIncidents = ->
  incidentDisplayList = []
  for incident in incidents
    incidentDisplay = {}
    if incident['initialId'] == null
      incidentDisplay['id'] = incident['_id']
    else
      incidentDisplay['id'] = incident['initialId']
    locations = incident['location']
    strLocation = ""
    for location in locations
      if location['type'] == 'string'
        strLocation = location['location']
      if location['type'] == 'region'
        strLocation = "Region " + location['regions'][0]
    strLocation += " " + incident['remark']
    incidentDisplay['strLocation'] = strLocation
    incidentDisplayList.push incidentDisplay
  html = incidentListTemplate
    incidents: incidentDisplayList
  $("#incident-list").html html


getIncidentByInitialId = (initialId, callback) ->
  $.ajax
    url: "/incidentByInitialId"
    dataType: "json"
    type: "post"
    data:
      initialId: initialId
    success: (ret)->
      console.log ret
      callback(ret["data"][0])


@completeIncident = (initialId) ->
  $.ajax
    url: "/completeIncident"
    data:
      initialId: initialId
      completeTime: Date.now()
    type: "post"
    success: (ret) ->
      console.log ret
      alert ret


incidentTemplate = _.template($("#incident-display-template").html())
@updateIncident = (initialId) ->
  incident = initialIdMap[initialId]
  for location in incident['location']
    if location['type'] == 'gps'
      incident['gps-location'] = location
    else if location['type'] == 'string'
      incident['string-location'] = location
  html = incidentTemplate
    incident: incident
  pop = simPop("Update incident", html, 500, 500)
  pop.find(".btn-update").on('click', ->
    data = pop.find('form').serialize()
    $.ajax
      url: "/updateIncident"
      data: data
      type: "post"
      success: (ret) ->
        alert ret
  )

$(document).ready ->
  loadIncidents()