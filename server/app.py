from flask import Flask
from flask import jsonify
from flask import request
from flask import g
import mysql.connector


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
        return jsonify({"status": "error", "message": "no such name"})
    if result == 1:
        return jsonify({"status": "error", "message": "wrong password"})
    return jsonify({"status": "ok"})


@app.route('/api/register', methods=['POST'])
def register():
    password = request.values.get("password")
    name = request.values.get("name")
    if is_name_exist_in_register(name) is True:
        return jsonify({"status": "error", "message": "name already exists"})
    write_in_login(name, password)
    return jsonify({"status": "ok"})


if __name__ == '__main__':
    app.run()
