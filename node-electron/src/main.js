const { app, session, Menu, BrowserWindow } = require('electron');

let win;
let windowConfig = {
    width: 1024,
    height: 768,
};
let socketClient;

function createWindow() {
    // const execSync = require('child_process').execSync;
    // const s = execSync('D:\\code.txt');
    // console.log(s);

    // 打开客户端窗口
    win = new BrowserWindow(windowConfig);
    // win.loadURL(`file://${__dirname}/../dist/index.html?_web_socket_rui=ws://localhost:8082/node/webSocket`);
    win.loadURL(`http://192.168.9.76:3000/?_web_socket_uri=ws://localhost:8082/node/webSocket`);
    //开启调试工具
    win.webContents.openDevTools();
    win.on('close', () => {
        //回收BrowserWindow对象
        win = null;
    });
    win.on('resize', () => {
        win.reload();
    });
    Menu.setApplicationMenu(null);
}

app.on('ready', createWindow);

app.on('window-all-closed', () => {
    app.quit();
});

app.on('activate', () => {
    if (win == null) {
        createWindow();
    }
});