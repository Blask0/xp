var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

// BEGIN
// Move content by path. New parent path, keeps same name.
var content2 = nodeLib.move({
    source: '/my-name',
    target: '/content/my-site/folder/'
});

log.info('New path: ' + content2._path); // '/content/my-site/folder/my-name'
// END

assert.assertEquals('/content/my-site/folder/my-name', content2._path);
