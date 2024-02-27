const socket = new WebSocket('ws://localhost:8080/ws/chat');

socket.onopen = function (event) {
    console.log('WebSocket 연결이 열렸습니다.');

    // 연결이 열리면 채팅 메시지를 서버에 전송할 수 있습니다.
    const message = {
        messageType: 'ENTER',  // 예시에 따라 메시지 타입을 지정합니다.
        chatRoomId: 123,        // 채팅방 ID 또는 필요한 정보
        // 추가 필드들...
    };

    socket.send(JSON.stringify(message));
};

socket.onmessage = function (event) {
    console.log('서버로부터 메시지를 수신했습니다.', event.data);
    // 수신한 메시지를 처리하는 로직을 추가합니다.
};

socket.onclose = function (event) {
    console.log('WebSocket 연결이 닫혔습니다.');
};

// 필요한 이벤트 핸들러 등록...
