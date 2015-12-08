/**
 * Internationalization functions.
 *
 * @example
 * var i18nLib = require('/lib/xp/i18n');
 *
 * @module lib/xp/i18n
 */

/**
 * This function localizes a phrase.
 *
 * @example-ref examples/localize.js
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key The property key.
 * @param {string} [params.locale] A string-representation of a locale. If the locale is not set, the site language is used.
 * @param {string[]} [params.values] Optional placeholder values.
 *
 * @returns {string} The localized string.
 */
exports.localize = function (params) {
    var bean = __.newBean('com.enonic.xp.lib.i18n.LocaleScriptBean');
    return bean.localize(params.key, __.nullOrValue(params.locale), __.toScriptValue(params.values));
};
