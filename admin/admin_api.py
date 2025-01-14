from flask import Flask

from util.api import required_param
from .admin_manager import AdminManager


class AdminAPI:
    def __init__(self, app: Flask, admin_manager: AdminManager):
        self.app = app
        self.admin_manager = admin_manager

    def register(self):

        @self.app.post("/api/v1/admins/init")
        def init_admin():
            return self.admin_manager.init(
                required_param("identifier"),
                required_param("password"),
            )

        @self.app.post("/api/v1/admins/token")
        def get_admin_token():
            return self.admin_manager.get_token(
                required_param("identifier"),
                required_param("password")
            )
