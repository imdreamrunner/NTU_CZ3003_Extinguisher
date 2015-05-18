submitIncident = ->
  data = $('#incident-detail').serialize()
  $.ajax
    url: "/submit"
    data: data
    type: "post"
    dataType: "json"
    success: (ret) ->
      console.log ret
      alert "success"
      location.reload()

$(document).ready ->
  $('#incident-detail').on('submit', ->
    submitIncident()
    return false
  )