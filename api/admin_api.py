from flask import Flask

from service.admin_service import AdminService
from util.api import required_param, pagination_page, pagination_size
from util.auth import authenticate_admin


class AdminAPI:
    def __init__(self, app: Flask, admin_service: AdminService):
        self.app = app
        self.admin_service = admin_service

    def register(self):

        @self.app.get("/api/v1/admins/init")
        def is_admin_initialized():
            return self.admin_service.is_initialized()

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

        @self.app.get("/api/v1/admins/current")
        @authenticate_admin
        def get_current_admin(admin):
            return self.admin_service.get(
                admin["username"],
            )

        @self.app.put("/api/v1/admins/current/password")
        @authenticate_admin
        def change_current_admin_password(admin):
            return self.admin_service.change_password(
                admin["username"],
                required_param("password"),
            )

        @self.app.post("/api/v1/admins")
        @authenticate_admin
        def add_admin(admin):
            return self.admin_service.add(
                required_param("username"),
                admin["username"],
            )

        @self.app.get("/api/v1/admins")
        @authenticate_admin
        def fetch_admins(admin):
            return self.admin_service.fetch(
                pagination_page(),
                pagination_size()
            )

        @self.app.get("/api/v1/admins/<username>")
        @authenticate_admin
        def get_admin(admin, username):
            return self.admin_service.get(
                username
            )

        @self.app.put("/api/v1/admins/<username>/password")
        @authenticate_admin
        def reset_admin_password(admin, username):
            return self.admin_service.reset_password(
                username
            )

        @self.app.delete("/api/v1/admins/<username>")
        @authenticate_admin
        def delete_admin(admin, username):
            return self.admin_service.delete(
                username
            )
