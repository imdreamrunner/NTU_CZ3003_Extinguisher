submitIncident = ->
  data = $('#incident-detail').serialize()
  $.ajax
    url: "/submit"
    data: data
    type: "post"
    dataType: "json"
    success: (ret) ->
      console.log ret
      alert ret['message']

$(document).ready ->
  $('#incident-detail').on('submit', ->
    submitIncident()
    return false
  )