import json
import os
import time
from datetime import date, datetime
import mysql.connector
import requests
from flask import Flask, g, request, send_from_directory


class ComplexEncoder(json.JSONEncoder):
    """
    TypeError: Object of type datetime is not JSON serializable
    meet this error in json.dumps, the reason is related to the datetime
    find the solution on CSDN
    """

    def default(self, obj):
        if isinstance(obj, datetime):
            return obj.strftime('%Y-%m-%d %H:%M:%S')
        elif isinstance(obj, date):
            return obj.strftime('%Y-%m-%d')
        else:
            return json.JSONEncoder.default(self, obj)


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


'''
AttributeError: 'Request' object has no attribute 'is_xhr'
if we use jsonify in here, we would meet this error
'''

app = Flask(__name__)

UPLOAD_FOLDER = os.getcwd() + '/uploads'
app.config['UPLOAD_FOLDER'] = UPLOAD_FOLDER

socketio_url = "http://192.168.0.104:8001/api/broadcast"


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


########################################################################################################################
@app.route('/api/login', methods=['POST'])
def login():
    password = request.values.get("password")
    name = request.values.get("name")
    result = is_name_exist_and_password_correct_in_login(name, password)
    if result == 0:
        return json.dumps({"status": "error", "message": "no such name"})
    if result == 1:
        return json.dumps({"status": "error", "message": "wrong password"})
    return json.dumps({"status": "ok"})


def is_name_exist_and_password_correct_in_login(name, password):
    query = '''SELECT * FROM login WHERE name = %s'''
    params = (name,)
    g.mydb.cursor.execute(query, params)
    result = g.mydb.cursor.fetchall()
    if len(result) == 0:
        return 0
    if password == result[0]["password"]:
        return 2
    return 1


########################################################################################################################
@app.route('/api/register', methods=['POST'])
def register():
    password = request.values.get("password")
    name = request.values.get("name")
    if is_name_exist_in_register(name) is True:
        return json.dumps({"status": "error", "message": "name already exists"})
    write_in_login(name, password)
    return json.dumps({"status": "ok"})


def is_name_exist_in_register(name):
    query = '''SELECT * FROM login WHERE name = %s'''
    params = (name,)
    g.mydb.cursor.execute(query, params)
    if len(g.mydb.cursor.fetchall()) == 0:
        return False
    return True


def write_in_login(name, password):
    query = '''INSERT INTO login (name, password) VALUES (%s, %s)'''
    params = (name, password)
    g.mydb.cursor.execute(query, params)
    g.mydb.conn.commit()
    return


########################################################################################################################
@app.route('/api/get_fri_list', methods=['GET'])
def get_fri_list():
    query = '''SELECT name FROM login ORDER BY name asc'''
    g.mydb.cursor.execute(query)
    result = g.mydb.cursor.fetchall()
    return json.dumps({"result": result})


@app.route('/api/get_chatroom_list', methods=['GET'])
def get_chatroom_list():
    name = request.values.get("name")
    query = '''SELECT chatroom FROM chatrooms WHERE name = %s ORDER BY chatroom asc'''
    params = (name,)
    g.mydb.cursor.execute(query, params)
    result = g.mydb.cursor.fetchall()
    return json.dumps({"result": result})


########################################################################################################################
@app.route('/api/get_chatroom_messages', methods=['POST'])
def get_chatroom_messages():
    chatroom = request.values.get("chatroom")
    messageTimeLine = request.values.get("messageTimeLine")
    if messageTimeLine == '0':
        query = '''SELECT * FROM chatroom_messages WHERE chatroom = %s ORDER BY id DESC'''
        params = (chatroom,)
    else:
        query = '''SELECT * FROM chatroom_messages WHERE chatroom = %s and message_time < %s ORDER BY id DESC'''
        params = (chatroom, messageTimeLine)
    g.mydb.cursor.execute(query, params)
    result = g.mydb.cursor.fetchall()
    return cut_messages(result)


def cut_messages(result):
    if len(result) >= 15:
        result = result[:15]
    return json.dumps({"result": result}, cls=ComplexEncoder)


@app.route('/api/get_private_chat_messages', methods=['POST'])
def get_private_chat_messages():
    name1 = request.values.get("name1")
    name2 = request.values.get("name2")
    messageTimeLine = request.values.get("messageTimeLine")
    if messageTimeLine == '0':
        query = '''SELECT * FROM private_chat_messages 
            WHERE (sendname = %s and receivename = %s) or (sendname = %s and receivename = %s) ORDER BY id DESC'''
        params = (name1, name2, name2, name1)
    else:
        query = '''SELECT * FROM private_chat_messages 
                    WHERE ((sendname = %s and receivename = %s) or (sendname = %s and receivename = %s)) 
                    and message_time < %s ORDER BY id DESC'''
        params = (name1, name2, name2, name1, messageTimeLine)
    g.mydb.cursor.execute(query, params)
    result = g.mydb.cursor.fetchall()
    return cut_messages(result)


########################################################################################################################
@app.route('/api/post_private_chat_message', methods=['POST'])
def post_private_chat_message():
    sendname = request.values.get("sendname")
    receivename = request.values.get("receivename")
    message = request.values.get("message")
    return post_message(receivename, sendname, message, 0)


def post_message(chatroom_or_receivename, sendname, message, is_chatroom):
    if is_chatroom == 1:  # chatroom
        query = '''INSERT INTO chatroom_messages (chatroom, name, message, message_time) 
                    VALUES (%s, %s, %s, default);'''
        query2 = '''SELECT message_time FROM chatroom_messages where id = %s'''
    else:  # private chat
        query = '''INSERT INTO private_chat_messages (receivename, sendname, message, message_time) 
                    VALUES (%s, %s, %s, default);'''
        query2 = '''SELECT message_time FROM private_chat_messages where id = %s'''
    params = (chatroom_or_receivename, sendname, message)
    g.mydb.cursor.execute(query, params)
    query = "SELECT @@IDENTITY;"
    g.mydb.cursor.execute(query)
    result = g.mydb.cursor.fetchall()
    g.mydb.conn.commit()
    id = str(result[0]["@@IDENTITY"])
    params = (id,)
    g.mydb.cursor.execute(query2, params)
    result = g.mydb.cursor.fetchall()
    message_time = result[0]['message_time']
    push = requests.post(socketio_url,
                         data={"is_chatroom": str(is_chatroom), "chatroom_or_receivename": chatroom_or_receivename,
                               "message": message, "message_time": message_time, "sendname": sendname})
    return json.dumps({"status": "ok", "message_time": message_time}, cls=ComplexEncoder)


@app.route('/api/post_chatroom_message', methods=['POST'])
def post_chatroom_message():
    chatroom = request.values.get("chatroom")
    sendname = request.values.get("sendname")
    message = request.values.get("message")
    return post_message(chatroom, sendname, message, 1)


########################################################################################################################
@app.route('/api/create_chatroom', methods=['POST'])
def create_chatroom():
    chatroom = request.values.get("chatroomName")
    if is_chatroom_exist(chatroom):
        return json.dumps({"status": "error", "message": "chatroom already exists"})
    users = request.values.get("chosenUsers")
    users = users[2:]
    users = users[:-2]
    users = users.split('","')
    query = '''INSERT INTO chatrooms (chatroom, name) VALUES (%s, %s);'''
    for i in users:
        params = (chatroom, i)
        g.mydb.cursor.execute(query, params)
        g.mydb.conn.commit()
    return json.dumps({"status": "ok"})


def is_chatroom_exist(chatroom):
    query = '''SELECT * FROM chatrooms WHERE chatroom = %s'''
    params = (chatroom,)
    g.mydb.cursor.execute(query, params)
    if len(g.mydb.cursor.fetchall()) == 0:
        return False
    return True


########################################################################################################################
@app.route('/api/post_moment', methods=['POST'])
def post_moment():
    name = request.values.get("name")
    content = request.values.get("content")
    moment_type = request.values.get("type")
    filename = ''
    if moment_type == 'image':
        file = request.files['file']
        filename = str(int(time.time())) + name + ".jpg"
        file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
    if moment_type == 'video':
        file = request.files['file']
        filename = str(int(time.time())) + name + ".mp4"
        file.save(os.path.join(app.config['UPLOAD_FOLDER'], filename))
    query = '''INSERT INTO moments (name, content, type, file_name, moment_time) 
                        VALUES (%s, %s, %s, %s, default);'''
    params = (name, content, moment_type, filename)
    g.mydb.cursor.execute(query, params)
    g.mydb.conn.commit()
    return json.dumps({"status": "ok"})


########################################################################################################################
@app.route('/api/get_moments', methods=['POST'])
def get_moments():
    momentTimeLine = request.values.get("momentTimeLine")
    if momentTimeLine == '0':
        query = '''SELECT * FROM moments ORDER BY id DESC'''
        g.mydb.cursor.execute(query)
    else:
        query = '''SELECT * FROM moments WHERE moment_time < %s ORDER BY id DESC'''
        params = (momentTimeLine,)
        g.mydb.cursor.execute(query, params)
    result = g.mydb.cursor.fetchall()
    if len(result) >= 15:
        result = result[:15]
    return json.dumps({"result": result}, cls=ComplexEncoder)


########################################################################################################################
@app.route('/api/download/<filename>')
def uploaded_file(filename):
    return send_from_directory(app.config['UPLOAD_FOLDER'], filename, as_attachment=True)


########################################################################################################################
if __name__ == '__main__':
    app.run(host="0.0.0.0", debug=True)
