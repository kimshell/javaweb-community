/**
 * 
 * 感谢前端大佬 @huang 
 * 
 * 
 */


//聊天 chat-container
var chatContainer = document.getElementById('chat-container')
var setScrollY;
//消息渲染
function ChatHtml(direction, obj) {
	//创建div
    var html = document.createElement('div')
    //消息位置
    html.className = 'chat-user clearfix ' + direction
    html.innerHTML = '<div class="img">' +
        '    <img src="' + obj.imgUrl + '" height="50" width="50"/>' +			//头像地址
        '</div>' +
        '<div class="chat-content">' +
        '    <div class="chat-page">' +
        '        <div class="user-name"><a href=\'http://www.javaweb.io/user/'+obj.userId+'\' target=\'_blank\'>' + obj.name + '<a/></div>' +					//用户名称
        '        <div class="chat-information clearfix">' +
        '            <div class="information-page">' + obj.content + '</div>' +		//消息正文
        '        </div>' +
        '    </div>' +
        '</div>';
    chatContainer.appendChild(html);
    scrollY();
}

//通知渲染
function TipHtml(text) {
    var html = document.createElement('div')
    html.className = 'chat-tip'
    html.innerHTML = '  <span class="tip">' + text + ' </span>'
    chatContainer.appendChild(html)
    scrollY()
}
function scrollY() {
    clearInterval(setScrollY)
    var chat = document.getElementById('chat')
    var chatHeight = chat.scrollHeight - chat.clientHeight
    setScrollY = setInterval(function () {
        chat.scrollTop +=2
        if(chatHeight <= chat.scrollTop){
            clearInterval(setScrollY)
            count = 0
        }
    },1)
}