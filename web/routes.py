from flask import Flask, render_template


class WebRoutes:
    def __init__(self, app: Flask):
        self.app = app

    def register(self):

        @self.app.get("/")
        def index_page():
            return render_template("index.html")
