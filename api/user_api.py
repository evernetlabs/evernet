from flask import Flask

from service.user_service import UserService
from util.api import required_param, optional_param, pagination_page, pagination_size
from util.auth import authenticate_user, authenticate_admin


class UserAPI:
    def __init__(self, app: Flask, user_service: UserService):
        self.app = app
        self.user_service = user_service

    def register(self):

        @self.app.post("/api/v1/nodes/<node_identifier>/users/signup")
        def user_sign_up(node_identifier: str):
            return self.user_service.sign_up(
                node_identifier,
                required_param("username"),
                required_param("password"),
                required_param("display_name"),
            )

        @self.app.post("/api/v1/nodes/<node_identifier>/users/token")
        def get_user_token(node_identifier: str):
            return self.user_service.get_token(
                node_identifier,
                required_param("username"),
                required_param("password"),
            )

        @self.app.get("/api/v1/users/current")
        @authenticate_user(must_be_local=True)
        def get_current_user(user):
            return self.user_service.get(
                user["source_node_identifier"],
                user["username"],
            )

        @self.app.put("/api/v1/users/current")
        @authenticate_user(must_be_local=True)
        def update_current_user(user):
            return self.user_service.update(
                user["source_node_identifier"],
                user["username"],
                optional_param("display_name"),
            )

        @self.app.put("/api/v1/users/current/password")
        @authenticate_user(must_be_local=True)
        def change_current_user_password(user):
            return self.user_service.change_password(
                user["source_node_identifier"],
                user["username"],
                required_param("password"),
            )

        @self.app.delete("/api/v1/users/current")
        @authenticate_user(must_be_local=True)
        def delete_current_user(user):
            return self.user_service.delete(
                user["source_node_identifier"],
                user["username"],
            )

        @self.app.post("/api/v1/nodes/<node_identifier>/users")
        @authenticate_admin
        def add_user(admin, node_identifier):
            return self.user_service.add(
                node_identifier,
                required_param("username"),
                required_param("display_name"),
                admin["username"],
            )

        @self.app.get("/api/v1/nodes/<node_identifier>/users")
        @authenticate_admin
        def fetch_users(admin, node_identifier):
            return self.user_service.fetch(
                node_identifier,
                pagination_page(),
                pagination_size()
            )

        @self.app.put("/api/v1/nodes/<node_identifier>/users/<username>/password")
        @authenticate_admin
        def reset_user_password(admin, node_identifier, username):
            return self.user_service.reset_password(
                node_identifier,
                username,
            )

        @self.app.delete("/api/v1/nodes/<node_identifier>/users/<username>")
        @authenticate_admin
        def delete_user(admin, node_identifier, username):
            return self.user_service.delete(
                node_identifier,
                username,
            )
