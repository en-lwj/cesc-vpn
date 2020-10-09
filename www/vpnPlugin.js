var exec = require('cordova/exec');

exports.conn = function (arg0, success, error) {
    exec(success, error, 'vpnPlugin', 'conn', [arg0]);
};
