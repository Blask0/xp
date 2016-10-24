var nodeLib = require('/lib/xp/node');
var assert = require('/lib/xp/assert');

// BEGIN
// Rename content by id. Keeps same parent.
var result = nodeLib.push({
    keys: ['a'],
    target: 'otherBranch',
    resolve: {
        includeChildren: true
    }
});
// END

// BEGIN
// Node created.
var expected = {
    "success": [
        "a",
        "b",
        "c"
    ],
    "failed": [
        {
            "id": "d",
            "reason": "ACCESS_DENIED"
        }
    ],
    "deleted": []
};
// END

assert.assertJsonEquals(expected, result);


