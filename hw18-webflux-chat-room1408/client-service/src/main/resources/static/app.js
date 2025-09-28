const ROOM_1408 = '1408';
let stompClient = null;

const chatLineElementId = "chatLine";
const roomIdElementId = "roomId";
const messageElementId = "message";

let getRoomId = () => {
    return document.getElementById(roomIdElementId).value.trim();
}

const setConnected = (connected) => {
    const connectBtn = document.getElementById("connect");
    const disconnectBtn = document.getElementById("disconnect");
    const sendBtn = document.getElementById("send");
    const roomId = getRoomId();

    connectBtn.disabled = connected;
    disconnectBtn.disabled = !connected;
    //Для того чтобы кнопка send была активна только при подключении и кроме комнаты 1408
    sendBtn.disabled = !connected || (roomId === ROOM_1408);
    const chatLine = document.getElementById(chatLineElementId);
    chatLine.hidden = !connected;
}

const connect = () => {
    stompClient = Stomp.over(new SockJS('/gs-guide-websocket'));
    stompClient.connect({}, (frame) => {
        setConnected(true);
        const userName = frame.headers["user-name"];
        const roomId = getRoomId();
        console.log(`Connected to roomId: ${roomId} frame:${frame}`);
        const topicName = `/topic/response.${roomId}`;
        const topicNameUser = `/user/${userName}${topicName}`;
        stompClient.subscribe(topicName, (message) => showMessage(JSON.parse(message.body).messageStr));
        stompClient.subscribe(topicNameUser, (message) => showMessage(JSON.parse(message.body).messageStr));
    });
}

const disconnect = () => {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

const sendMsg = () => {
    const roomId = getRoomId();
    //На случай, если кнопку send кто-то включит через devTool
    if (roomId === ROOM_1408) {
       alert("Вы в комнате " + roomId + ", здесь нельзя отправлять сообщения O_o");
       return true;
    }
    const message = document.getElementById(messageElementId).value;
    stompClient.send(`/app/message.${roomId}`, {}, JSON.stringify({'messageStr': message}))
}

const showMessage = (message) => {
    const chatLine = document.getElementById(chatLineElementId);
    let newRow = chatLine.insertRow(-1);
    let newCell = newRow.insertCell(0);
    let newText = document.createTextNode(message);
    newCell.appendChild(newText);
}
