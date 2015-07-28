
function PrivateRTB(div,exchangeid,campaign,url) {
	this.div = div;
    this.exchangeid = exchangeid;
    this.campaignid = campaign;
    this.url = url;
    this.ua = navigator.userAgent;
    this.lat = 0;
    this.lon = 0;
    this.platform = navigator.platform;
}

PrivateRTB.prototype.perform = function(video) {
	var div = this.div;
	var cmd = {};
	cmd.ua = this.ua;
	cmd.location = this.location;
	cmd.accountNumber  = this.exchangeid;
	cmd.campaign = this.campaignid;
	cmd.platform = this.platform;;
	if (typeof navigator.connection != 'undefined') {
		cmd.connectionType = navigator.connection.type;
	 	cmd.maxDownLink = navigator.connection.downlinkMax;;
	}
	else {
		cmd.connectionType = 'unk';
		cmd.maxDownLink = -1.0;
	}
	var url = this.url;
	var self = this;
    if (typeof navigator.geolocation != 'undefined') {
        navigator.geolocation.getCurrentPosition(function(position) {
        	cmd.lat = position.coords.latitude;
        	cmd.lon = position.coords.longitude;
        	self.doAjax(url,cmd, div, video);
        });
    } else	
		self.doAjax(url,cmd,div, video);
}

PrivateRTB.prototype.doAjax = function(url,cmd,div, video) {
    $.ajax({
         type: 'POST',
         url: url,
         data: JSON.stringify(cmd),
         success: function(data, textStatus, request){
           if (request.status == 204) {
           	alert("No bid returned, so nothing changes");
           	return;
           } else {
           	text = request.responseText;
           	console.log("TEXT: " + text);
           	if (typeof video === 'undefined')
          		div.innerHTML = text;
          	else
          		video(text);
          }
         },
         error: function (request, textStatus, errorThrown) {
           alert("Error: " + request.responseText);
      }});
}



