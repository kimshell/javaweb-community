(function($){
    window.setInterval(function(){
        $.ajax({
            url:'/notify',
            method:'GET',
            dataType:'JSON',
            success:function (response) {
            	if(response.success && response.data > 0){
            		$('.badge').text(response.data);
            	}else{
            		$('.badge').text('');
            	}
            }
        });
    },5000 * 2);
})(jQuery);