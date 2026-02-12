from flask import Flask

from service.admin_service import AdminService
from util.api import required_param


class AdminAPI:
    def __init__(self, app: Flask, admin_service: AdminService):
        self.app = app
        self.admin_service = admin_service

    def register(self):

        @self.app.post("/api/v1/admins/init")
        def init_admin():
            return self.admin_service.init(
                required_param("username"),
                required_param("password"),
                required_param("vertex_endpoint"),
                required_param("vertex_display_name"),
                required_param("vertex_description"),
            )

        @self.app.post("/api/v1/admins/token")
        def get_admin_token():
            return self.admin_service.get_token(
                required_param("username"),
                required_param("password"),
            )