import json
import mysql.connector
from flask import Flask
from flask import g
from flask import request
from datetime import date, datetime


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


def is_name_exist_in_register(name):
    query = "SELECT * FROM login WHERE name = %s"
    params = (name,)
    g.mydb.cursor.execute(query, params)
    if len(g.mydb.cursor.fetchall()) == 0:
        return False
    return True


def is_name_exist_in_login_and_password_correct(name, password):
    query = "SELECT * FROM login WHERE name = %s"
    params = (name,)
    g.mydb.cursor.execute(query, params)
    result = g.mydb.cursor.fetchall()
    if len(result) == 0:
        return 0
    if password == result[0]["password"]:
        return 2
    return 1


def write_in_login(name, password):
    query = "INSERT INTO login (name, password) VALUES (%s, %s)"
    params = (name, password)
    g.mydb.cursor.execute(query, params)
    g.mydb.conn.commit()
    return


def cut_messages_in_page(result, page):
    total_page = int(len(result) / 15) + 1
    if total_page < page:
        return json.dumps({"status": "error"})
    if page == total_page:
        result = result[((page - 1) * 15):]
    else:
        result = result[((page - 1) * 15):(page * 15)]
    return json.dumps({"status": "ok", "result": result}, cls=ComplexEncoder)


@app.route('/api/login', methods=['POST'])
def login():
    password = request.values.get("password")
    name = request.values.get("name")
    result = is_name_exist_in_login_and_password_correct(name, password)
    if result == 0:
        return json.dumps({"status": "error", "message": "no such name"})
    if result == 1:
        return json.dumps({"status": "error", "message": "wrong password"})
    return json.dumps({"status": "ok"})


@app.route('/api/register', methods=['POST'])
def register():
    password = request.values.get("password")
    name = request.values.get("name")
    if is_name_exist_in_register(name) is True:
        return json.dumps({"status": "error", "message": "name already exists"})
    write_in_login(name, password)
    return json.dumps({"status": "ok"})


@app.route('/api/get_fri_list', methods=['GET'])
def get_fri_list():
    query = "SELECT name FROM login ORDER BY name asc"
    g.mydb.cursor.execute(query)
    result = g.mydb.cursor.fetchall()
    return json.dumps({"result": result})


@app.route('/api/get_chatroom_list', methods=['GET'])
def get_chatroom_list():
    name = request.values.get("name")
    query = "SELECT chatroom FROM chatrooms WHERE name = %s ORDER BY chatroom asc"
    params = (name,)
    g.mydb.cursor.execute(query, params)
    result = g.mydb.cursor.fetchall()
    return json.dumps({"result": result})


@app.route('/api/get_chatroom_messages', methods=['GET'])
def get_chatroom_messages():
    chatroom = request.values.get("chatroom")
    page = int(request.values.get("page"))
    query = '''SELECT * FROM chatroom_messages WHERE chatroom = %s ORDER BY id DESC'''
    params = (chatroom,)
    g.mydb.cursor.execute(query, params)
    result = g.mydb.cursor.fetchall()
    return cut_messages_in_page(result, page)


@app.route('/api/get_private_chat_messages', methods=['GET'])
def get_private_chat_messages():
    name1 = request.values.get("name1")
    name2 = request.values.get("name2")
    page = int(request.values.get("page"))
    query = '''SELECT * FROM private_chat_messages 
            WHERE (sendname = %s and receivename = %s) or (sendname = %s and receivename = %s) ORDER BY id DESC'''
    params = (name1, name2, name2, name1)
    g.mydb.cursor.execute(query, params)
    result = g.mydb.cursor.fetchall()
    return cut_messages_in_page(result, page)


if __name__ == '__main__':
    app.run(host="0.0.0.0", debug=True)
