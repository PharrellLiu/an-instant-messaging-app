import json
import mysql.connector
from flask import Flask, request, g
from flask_socketio import SocketIO, join_room, leave_room, emit


class MyDatabase:
    conn = None
    cursor = None

    def __init__(self):
        self.connect()
        return

    def connect(self):
        self.conn = mysql.connector.connect(
            host="localhost",
            port=3306,
            user="dbuser",
            password="password",
            database="iems5722",
        )
        self.cursor = self.conn.cursor(dictionary=True)
        return


app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app)


@app.before_request
def before_request():
    g.mydb = MyDatabase()
    return


@app.teardown_request
def teardown_request(exception):
    mydb = getattr(g, "mydb", None)
    if mydb is not None:
        mydb.conn.close()
    return


@app.route("/api/broadcast", methods=["POST"])
def broadcast():
    is_chatroom = request.values.get("is_chatroom")
    chatroom_or_receivename = request.values.get("chatroom_or_receivename")
    message_time = request.values.get("message_time")
    message = request.values.get("message")
    sendname = request.values.get("sendname")
    broadcast_message = {"is_chatroom": str(is_chatroom), "chatroom_or_receivename": chatroom_or_receivename,
                         "message": message, "message_time": message_time, "sendname": sendname,
                         "receiver": chatroom_or_receivename}
    if is_chatroom == '1':
        query = '''SELECT name FROM chatrooms WHERE chatroom = %s '''
        params = (chatroom_or_receivename,)
        g.mydb.cursor.execute(query, params)
        result = g.mydb.cursor.fetchall()
        for i in result:
            broadcast_message["receiver"] = i["name"]
            socketio.emit('push', broadcast_message)
    else:
        socketio.emit('push', broadcast_message)
    return json.dumps({"status": "ok"})


@socketio.on('push')
def push(data):
    receiver = data["receiver"]
    del data["receiver"]
    emit('broadcast', data, room=receiver)


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
