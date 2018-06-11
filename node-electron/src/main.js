const { app, session, BrowserWindow } = require('electron');

let win;
let windowConfig = {
    width: 800,
    height: 600,
};
let socketClient;

function createWindow() {
    // const execSync = require('child_process').execSync;
    // const s = execSync('D:\\code.txt');
    // console.log(s);

    // 打开客户端窗口
    win = new BrowserWindow(windowConfig);
    win.loadURL(`file://${__dirname}/../dist/index.html`);
    //开启调试工具
    win.webContents.openDevTools();
    win.on('close', () => {
        //回收BrowserWindow对象
        win = null;
    });
    win.on('resize', () => {
        win.reload();
    });
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