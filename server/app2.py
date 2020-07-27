import json
from flask import Flask, request
from flask_socketio import SocketIO, join_room, leave_room, emit

app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app)


@app.route("/api/broadcast", methods=["POST"])
def broadcast():
    is_chatroom = request.values.get("is_chatroom")
    chatroom_or_receivename = request.values.get("chatroom_or_receivename")
    message_time = request.values.get("message_time")
    message = request.values.get("message")
    broadcast_message = {"chatroom_or_receivename": chatroom_or_receivename,
                         "message_time": message_time, "message": message}
    if is_chatroom == '1':
        socketio.emit('push_chatroom', broadcast_message)
    else:
        socketio.emit('push_private_chat', broadcast_message)
    return json.dumps({"status": "ok"})


@socketio.on('push_chatroom')
def push_chatroom(data):
    chatroom = data["chatroom_or_receivename"]
    emit('broadcast', data, room=chatroom)


@socketio.on('push_private_chat')
def push_private_chat(data):
    receivename = data["chatroom_or_receivename"]
    emit('broadcast', data, room=receivename)


@socketio.on('join')
def on_join(data):
    join_room(data)


@socketio.on('leave')
def on_leave(data):
    leave_room(data)


@socketio.on('connect')
def connect_handler():
    pass


@socketio.on('disconnect')
def disconnect_handler():
    pass


if __name__ == '__main__':
    socketio.run(app, host='0.0.0.0', port=8001)
