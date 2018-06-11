import React, {Component} from 'react';
import logo from './logo.svg';
import './App.css';

import { start, sendMessage, onMessage, getClientId } from './SocketClient';

const send = () => {
    const targetId = document.getElementById("targetServerId");
    const message = document.getElementById("message");
    const innerHTML = document.getElementById("show_div").innerHTML;
    document.getElementById("show_div").innerHTML = innerHTML + '<br />发送:<span style="color: red">' + message.value + '</span>';
    const req = { messageType: 'CHAT', targetId: targetId.value, sourceId: getClientId(), data: message.value};
    message.value = '';
    sendMessage(req);
};

const receive = (replay) => {
    const innerHTML = document.getElementById("show_div").innerHTML;
    document.getElementById("show_div").innerHTML = innerHTML + '<br />收到:' + replay;
};

const segClientId = () => {
    document.getElementById("client_div").innerHTML = getClientId();
};

class App extends Component {

    componentWillMount() {
        onMessage(function (data) {
            console.log('这是回调方法，返回值：' + JSON.stringify(data));
            const { data : replay } = data;
            receive(replay);
        });
        start();
    }

    render() {
        return (
            <div className="App">
                <header className="App-header">
                    <img src={logo} className="App-logo" alt="logo"/>
                    <h1 className="App-title">Welcome to React</h1>
                </header>
                <p className="App-intro">
                    To get started, edit <code>src/App.js</code> and save to reload.
                </p>
                <div id="client_div" />
                <button onClick={ segClientId }>获取clientId</button><br />
                <input id="targetServerId" /><br />
                <input id="message" /><br />
                <button onClick={ send }>发送消息</button>
                <div id="show_div" />
            </div>
        );
    }
}

export default App;
