import flask
from flask import render_template


class Web:
    def __init__(self, app: flask.Flask):
        self.app = app

    def register(self):

        @self.app.get("/")
        def index_page():
            return render_template("index.html")

        @self.app.get("/logout")
        def logout_page():
            return render_template("logout.html")
