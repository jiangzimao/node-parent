let webSocketUri = null;
let socketClientId = null;
let socketClient = null;
let onMessageFun = null;
let onCloseFun = null;
let onErrorFun = null;

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

const sendMessage = (message) => {
    if (socketClient !== null && socketClient !== undefined) {
        const {messageType, clientId} = message;
        if (clientId === null || clientId === undefined) {
            Object.assign(message, {clientId: socketClientId});
        }
        if (messageType === null || messageType === undefined) {
            console.error('messageType is null.');
        } else {
            socketClient.send(JSON.stringify(message));
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
        const req = {messageType: 'REGISTER', data: 'register'};
        socketClient.send(JSON.stringify(req));
        console.info('The connection is successful and the registration message is sent.');
    };
    socketClient.onmessage = function (msg) {
        const data = msg.data;
        if (data !== null && data !== undefined) {
            const message = JSON.parse(data);
            const {messageType} = message;
            if (messageType !== null && messageType !== undefined) {
                switch (messageType) {
                    case 'HEARTBEAT':
                        console.info(`heartbeat successful. ${data}`);
                        return;
                    case 'REGISTER':
                        const {sourceId, status} = message.data;
                        if (status) {
                            socketClientId = sourceId;
                            console.info(`Client registration is successful, clientId is ${socketClientId}. response[${data}]`);
                        } else {
                            console.error('Client registration failure.');
                        }
                        return;
                    default:
                        break;
                }
            }
            if (onMessageFun !== null && onMessageFun !== undefined) {
                onMessageFun(message);
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
        this.timeoutObj = setInterval(() => {
            if(socketClient.readyState === 1) {
                const req = {
                    messageType: 'HEARTBEAT',
                    targetId: socketClientId,
                    sourceId: socketClientId,
                    data: getNowFormatDate()
                };
                socketClient.send(JSON.stringify(req));
            } else {
                connection();
            }
        }, this.timeout);
    }
};

export {start, sendMessage, onMessage, getClientId, onClose, onError};