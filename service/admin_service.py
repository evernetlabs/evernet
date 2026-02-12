import datetime
import time
import uuid

import bcrypt
import jwt
from montydb.collection import MontyCollection

from exception.errors import AuthorizationError, NotFoundError
from service.config_service import ConfigService
from util.secret import generate_secret


class AdminService:
    def __init__(self, collection: MontyCollection, config_service: ConfigService):
        self.collection = collection
        self.config_service = config_service

    def init(
            self, username: str, password: str,
            vertex_endpoint: str, vertex_display_name: str, vertex_description: str
    ):
        if self.collection.count_documents({}) > 0:
            raise AuthorizationError("You are not allowed to perform this action")

        self.collection.insert_one({
            "username": username,
            "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
            "creator": username,
            "created_at": datetime.datetime.now(tz=datetime.timezone.utc),
            "updated_at": datetime.datetime.now(tz=datetime.timezone.utc)
        })

        self.config_service.init(vertex_endpoint, vertex_display_name, vertex_description, generate_secret(128), "http")
        return {
            "username": username
        }

    def get_token(self, username: str, password: str) -> dict:
        admin = self.collection.find_one({
            "username": username
        })

        if not admin or not bcrypt.checkpw(password.encode("utf-8"), admin.get("password").encode("utf-8")):
            raise AuthorizationError("Invalid username or password")

        vertex_endpoint = self.config_service.get_vertex_endpoint()
        jwt_signing_key = self.config_service.get_jwt_signing_key()

        token = jwt.encode({
            "sub": username,
            "type": "admin",
            "iat": int(time.time()),
            "exp": int(time.time()) + 3600,
            "iss": vertex_endpoint,
            "aud": vertex_endpoint,
            "jti": str(uuid.uuid4()),
        }, jwt_signing_key, algorithm="HS256")

        return {
            "token": token
        }

    def get(self, username: str) -> dict:
        admin = self.collection.find_one({
            "username": username
        })

        if not admin:
            raise NotFoundError(f"Admin {username} not found")

        return self.to_dict(admin)

    @staticmethod
    def to_dict(admin: dict) -> dict:
        return {
            "username": admin.get("username"),
            "creator": admin.get("creator"),
            "created_at": admin.get("created_at"),
            "updated_at": admin.get("updated_at")
        }