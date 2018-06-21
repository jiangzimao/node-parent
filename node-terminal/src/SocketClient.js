let webSocketUri = null;
let socketClientId = null;
let socketClient = null;
let onMessageFun = null;
let onCloseFun = null;
let onErrorFun = null;
const replyContext = new Map();

const getClientId = () => {
    return socketClientId;
};

const onMessage = (fun) => {
    onMessageFun = fun;
};

const onClose = (fun) => {
    onCloseFun = fun;
};

const onError = (fun) => {
    onErrorFun = fun;
};

const sendMessage = async (message) => {
    if (socketClient !== null && socketClient !== undefined) {
        const {messageType, clientId, sync} = message;
        if (clientId === null || clientId === undefined) {
            Object.assign(message, {clientId: socketClientId});
        }
        if (messageType === null || messageType === undefined) {
            console.error('messageType is null.');
        } else {
            if (sync) {
                const requestId = new Date().getTime();
                const promise = new Promise((resolve, reject) => {
                    replyContext.set(requestId, {resolve, reject});
                    Object.assign(message, {requestId});
                    socketClient.send(JSON.stringify(message));
                });
                return await promise.then(function (rs) {
                    return rs;
                }).catch(function (err) {
                    return err;
                });
            } else {
                socketClient.send(JSON.stringify(message));
            }
        }

    } else {
        console.error('WebSocketClient is null.');
    }
};

const start = () => {
    webSocketUri = getParamByKey('_web_socket_uri');
    console.info(`Connecting to WebSocket at ${webSocketUri}`);
    if (webSocketUri === null || webSocketUri === undefined) {
        console.error('_web_socket_uri is null.');
        return;
    }
    connection();
    // 启动心跳
    heartbeat.reset().start();
};

const connection = () => {
    try {
        socketClient = new WebSocket(webSocketUri);
    } catch (e) {
        console.error(e);
        return;
    }
    socketClient.onopen = function () {
        (
            async () => {
                const req = {
                    sync: true,
                    requestId: new Date().getTime(),
                    messageType: 'REGISTER',
                    data: 'client register'
                };
                console.log('start register');
                const rs = await sendMessage(req);
                const {data : {sourceId, status}} = rs;
                if (status) {
                    socketClientId = sourceId;
                    console.info(`Client registration is successful, clientId is ${socketClientId}. response[${rs}]`);
                } else {
                    console.error('Client registration failure.');
                }
            }
        )();
    };
    socketClient.onmessage = function (msg) {
        const data = msg.data;
        if (data !== null && data !== undefined) {
            const message = JSON.parse(data);
            if (message === null || message === undefined) {
                console.log(`data[${data}] message is null.`);
                return;
            }
            const {messageType} = message;
            if (messageType !== null && messageType !== undefined) {
                switch (messageType) {
                    case 'REPLY':
                        const {requestId: rid} = message;
                        if (rid && rid > 0 && replyContext.has(rid)) {
                            const {resolve} = replyContext.get(rid);
                            if (resolve !== null && resolve !== undefined) {
                                resolve(message);
                            }
                            replyContext.delete(rid);
                        }
                        return;
                    default:
                        break;
                }
            }
            if (onMessageFun !== null && onMessageFun !== undefined) {
                const res = onMessageFun(message);
                const {sync, requestId, targetId, sourceId} = message;
                if (sync && requestId && requestId > 0) {
                    // 响应
                    const resData = res === undefined || res === null ? true : res;
                    const response = {sync: false, requestId, messageType: 'REPLY', targetId: sourceId, sourceId: targetId, data: resData};
                    sendMessage(response);
                }
            }
        }
    };
    socketClient.onclose = function () {
        if (onCloseFun !== null && onCloseFun !== undefined) {
            onCloseFun();
        }
    };
    socketClient.onerror = function (err) {
        if (onErrorFun !== null && onErrorFun !== undefined) {
            onErrorFun(err);
        } else {
            console.error(err);
        }
    };
};

const getParamByKey = (key) => {
    let qs = window.location.search.substr(1), // 获取url// 中"?"符后的字串
        args = {}, // 保存参数数据的对象
        items = qs.length ? qs.split("&") : [], // 取得每一个参数项,
        item = null,
        len = items.length;

    for (let i = 0; i < len; i++) {
        item = items[i].split("=");
        let name = decodeURIComponent(item[0]),
            value = decodeURIComponent(item[1]);
        if (name) {
            args[name] = value;
        }
    }
    return args[key];
};

const getNowFormatDate = () => {
    const date = new Date();
    let month = date.getMonth() + 1;
    let strDate = date.getDate();
    return date.getFullYear() + '-' + lpad(month, '0', 2) + '-' + lpad(strDate, '0', 2)
        + " " + lpad(date.getHours(), '0', 2) + ':' + lpad(date.getMinutes(), '0', 2)
        + ':' + lpad(date.getSeconds(), '0', 2) + ':' + lpad(date.getMilliseconds(), '0', 3);
};

const lpad = (str, padText, length) => {
    let newStr = str + '';
    while (newStr.length < length) {
        newStr = padText + newStr;
    }
    return newStr;
};

const heartbeat = {
    timeout: 5000,//5 秒
    timeoutObj: null,
    reset() {
        clearInterval(this.timeoutObj);
        this.timeoutObj = null;
        return this;
    },
    start() {
        this.timeoutObj = setInterval(async() => {
            if (socketClient.readyState === 1) {
                const req = {
                    sync: true,
                    messageType: 'HEARTBEAT',
                    targetId: socketClientId,
                    sourceId: socketClientId,
                    data: getNowFormatDate()
                };
                await sendMessage(req);
            } else {
                connection();
            }
        }, this.timeout);
    }
};

export {start, sendMessage, onMessage, getClientId, onClose, onError};