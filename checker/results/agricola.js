function setTitle(e) {
  var an = $(e).css('animation-name');
  if (an != "none") {
    $(e).attr("title",an.replace(/_/g," "))
  }
};
$(function(){
  $('*').each(function(){setTitle(this)})
});