template = $("#simPop-template").html()

class SimPop
  constructor: (title, content, width, height) ->
    $box = $("<div class='simPop'></div>")
    $box.html(template)
    $box.find(".title").html(title)
    $box.find(".content").html(content)
    $box.css(
      height: height
      width: width
      top: Math.max(($(window).height() - height)/2, 10)
      left: Math.max(($(window).width() - width)/2, 10)
    )
    $box.find(".close-button").click @remove.bind(@)
    $box.find(".header").on("mousedown", @mousedown.bind(@))
    $("body").on("mousemove", @mousemove.bind(@))
    $box.find(".header").on("mouseup", @mouseup.bind(@))
    $("body").append($box)
    @$box = $box

  remove: ->
    @$box.remove()

  mousedown: (e) ->
    e.preventDefault()
    @initDrag =
      x: e.clientX - @$box.offset().left
      y: e.clientY - @$box.offset().top
    # console.log @initDrag
    @isDragging = true

  mousemove: (e) ->
    if @isDragging
      @$box.css(
        left: e.clientX - @initDrag.x
        top: e.clientY - @initDrag.y
      )

  find: (el) ->
    @$box.find(el)


  mouseleave: ->
    @isDragging = false

  mouseup: ->
    @isDragging = false


@simPop = (title, content, width, height) ->
  return new SimPop(title, content, width, height)