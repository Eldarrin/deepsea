/* Credit: http://www.templatemo.com */

var menuDisabled = false;
var leftPix = '0px';

jQuery(function($) {
    
    $(window).load(function() { // makes sure the whole site is loaded
            $('#status').fadeOut(); // will first fade out the loading animation
            $('#preloader').delay(350).fadeOut('slow'); // will fade out the white DIV that covers the website.
            $('#main-wrapper').delay(350).css({'overflow':'visible'});
    });
    
    $(document).ready( function() {

        // backstretch for background image
        var defaultImgSrc = $('img.main-img').attr('src');
        $.backstretch(defaultImgSrc, {speed: 500});
	
	// for responsive-menu
	$("#m-btn").click(function(){
		$("#responsive").toggle();
	});
	
        // copy menu list to responsive menu
        var mainMenuList = $('#menu-list').html();
        $('#responsive').html(mainMenuList);
	
	//for image slide on menu item click(normal) and responsive
	$("#menu-list a, #responsive a").on('click',function(e){
            
			
			if(this.className == "external") {
                return;
            }
			
			e.preventDefault();

            if (menuDisabled == false) // check the menu has disabled?
            {
                menuDisabled = true; // disable to menu
                
                var name = $(this).attr('href');
                $('#menu-list li').removeClass('active');
                $('#responsive li').removeClass('active');

                //  set active to both menu
                var menuClass = $(this).parent('li').attr('class');
                $('.'+menuClass).addClass('active');

                // hide responsive menu
                $("#responsive").hide();
                
                // get image url and assign to backstretch for background
                var imgSrc = $("img"+name+"-img").attr('src');
                $.backstretch(imgSrc, {speed: 500}); //backstretch for background fade in/out
			    
                // content slide in/out
                $("section.active").animate({left:$("section.active").outerWidth()}, 400,function(){
                    $(this).removeClass("active");
                    $(this).hide();
                    $(name+"-text").show();
					if (name == "#deepsea") {
						leftPix = '250px';
					} else {
						leftPix = '0px';
					}
                    $(name+"-text").animate({left:leftPix},400,function(){
                        $(this).addClass("active");
                        
                        $.backstretch("resize"); // resize the background image
                        
                        menuDisabled = false; // enable the menu
                    });
                });
                
            }
            return;
	});
        
    });

});

