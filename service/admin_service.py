import datetime

import bcrypt
from montydb.collection import MontyCollection

from exception.errors import AuthorizationError
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
