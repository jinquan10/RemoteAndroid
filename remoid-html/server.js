var http = require("http"), url = require("url"), path = require("path"), fs = require("fs"), static = require('node-static'), httpProxy = require('http-proxy'),
port = process.argv[2] || 80;

var proxyKeyWord = 'nwm-coauthor-webapp';
var proxyHost = 'http://localhost:8080';

var file = new static.Server('./public'/*, { cache: 3600 }*/);
var proxy = httpProxy.createProxyServer();

http.createServer(function(request, response) {
    var uri = url.parse(request.url).pathname;

    if (uri.indexOf(proxyKeyWord) > -1) {
        var targetUri = proxyHost + uri;
        proxy.web(request, response, { target: targetUri });
    } else {
        file.serve(request, response);
    }
}).listen(parseInt(port, 10));

process.on('uncaughtException', function(err) {
    console.log(err);
});
