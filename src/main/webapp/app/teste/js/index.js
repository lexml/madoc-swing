$(document).bind("mobileinit", function(){
	$.extend(  $.mobile , {
		defaultDialogTransition = 'pop';
		defaultPageTransition = 'fade';
		
		//To keep all previously-visited pages in the DOM, set the domCache option on the page plugin to true, like this:
		//page.prototype.options.domCache = true;

	  });
});
