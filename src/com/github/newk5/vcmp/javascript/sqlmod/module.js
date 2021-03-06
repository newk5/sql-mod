"use strict";
module.exports = {

    newConnection: function (url, username, password) {
        let connection = SQLWrapper.newConnection(url, username, password);
        let c = {
            instance: connection,
            query: function (query, async, callback) {
                if (query && async && typeof (async) === "boolean") {
                    return SQLWrapper.query(query, c.instance, async, callback, true);
                }
                if (query && callback == null) {
                    let isFunc = async && {}.toString.call(async) === '[object Function]';
                    if (isFunc) {
                        callback = async;
                        return SQLWrapper.query(query, c.instance, true, callback, true);
                    }
                }
                if (query && async == null && callback == null) {
                    return SQLWrapper.query(query, c.instance, false, null, true);
                }
            },
            findFirst: function (query, async, callback) {
                if (query && async && typeof (async) === "boolean") {
                    return SQLWrapper.query(query, c.instance, async, callback, false);
                }
                if (query && callback == null) {
                    let isFunc = async && {}.toString.call(async) === '[object Function]';
                    if (isFunc) {
                        callback = async;
                        return SQLWrapper.query(query, c.instance, true, callback, false);
                    }
                }
                if (query && async == null && callback == null) {
                    return SQLWrapper.query(query, c.instance, false, null, false);
                }

            }

        };
        return c;
    },

    connect: function (db, ip, port, username, password) {
        var url = "jdbc:mysql://" + ip + ":" + port + "/" + db;
        return this.newConnection(url, username, password);
    }

};