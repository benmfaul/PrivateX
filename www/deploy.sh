#!/bin/sh
scp -i ~ben/certs/rtb4free_key.pem js/privatex.js ubuntu@rtb4free.com:/usr/share/nginx/www/js/privatex.js
scp -i ~ben/certs/rtb4free_key.pem video-sample.html ubuntu@rtb4free.com:/usr/share/nginx/www/video-sample.html
scp -i ~ben/certs/rtb4free_key.pem banner-sample.html ubuntu@rtb4free.com:/usr/share/nginx/www/banner-sample.html
