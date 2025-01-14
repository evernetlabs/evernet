from flask import Flask

from util.api import required_param, authenticate_admin, size, page
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

        @self.app.get("/api/v1/admins")
        @authenticate_admin
        def fetch_admins(_):
            return self.admin_manager.fetch(page(), size())

        @self.app.get("/api/v1/admins/<identifier>")
        @authenticate_admin
        def get_admin_details(_, identifier):
            return self.admin_manager.get(identifier)

        @self.app.get("/api/v1/admins/current")
        @authenticate_admin
        def get_current_admin_details(admin):
            return self.admin_manager.get(admin["identifier"])

        @self.app.put("/api/v1/admins/current/password")
        @authenticate_admin
        def change_admin_password(admin):
            return self.admin_manager.change_password(
                admin["identifier"],
                required_param("password")
            )

        @self.app.post("/api/v1/admins")
        @authenticate_admin
        def add_admin(admin):
            return self.admin_manager.add(
                required_param("identifier"),
                admin["identifier"],
            )

        @self.app.delete("/api/v1/admins/<identifier>")
        @authenticate_admin
        def delete_admin(_, identifier):
            return self.admin_manager.delete(
                identifier,
            )
