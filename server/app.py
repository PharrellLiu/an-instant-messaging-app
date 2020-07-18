import json
import mysql.connector
from flask import Flask
from flask import g
from flask import request


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
    else:
        return True


def is_name_exist_in_login(name, password):
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


@app.route('/api/login', methods=['POST'])
def login():
    password = request.values.get("password")
    name = request.values.get("name")
    result = is_name_exist_in_login(name, password)
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
    query = "SELECT name FROM login ORDER BY "
    g.mydb.cursor.execute(query)
    result = g.mydb.cursor.fetchall()
    return json.dumps({"result": result})


if __name__ == '__main__':
    app.run(host="0.0.0.0", debug=True)
