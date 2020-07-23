from flask import Flask, render_template
from flask_socketio import SocketIO, join_room, leave_room

app = Flask(__name__)
app.config['SECRET_KEY'] = 'secret!'
socketio = SocketIO(app)

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
