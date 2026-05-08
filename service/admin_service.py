import bcrypt
from pymongo.collection import Collection

from service.config_service import ConfigService
from util.auth import generate_admin_token
from util.secret import generate_secret
from util.time import current_datetime


class AdminService:
    def __init__(self, mongo: Collection, config_service: ConfigService):
        self.mongo = mongo
        self.config_service = config_service

    def is_initialized(self) -> dict:
        return {
            "initialized": self.mongo.count_documents({}) > 0
        }

    def init(self, username: str, password: str, vertex_endpoint: str, vertex_display_name: str, vertex_description: str):
        if self.mongo.count_documents({}) > 0:
            raise Exception("You are not allowed to perform this action")

        self.mongo.insert_one({
            "username": username,
            "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
            "creator": username,
            "created_at": current_datetime(),
            "updated_at": current_datetime(),
        })

        jwt_signing_key = generate_secret(128)
        self.config_service.init(
            vertex_endpoint,
            vertex_display_name,
            vertex_description,
            jwt_signing_key,
            "http"
        )

        return {
            "username": username,
        }

    def get_token(self, username: str, password: str) -> dict:
        admin = self.mongo.find_one({
            "username": username,
        })

        if not admin or not bcrypt.checkpw(password.encode("utf-8"), admin["password"].encode("utf-8")):
            raise Exception("Invalid username and password combination")

        token = generate_admin_token(admin["username"])
        return {
            "token": token,
        }

    def get(self, username: str) -> dict:
        admin = self.mongo.find_one({
            "username": username,
        })

        if not admin:
            raise Exception(f"Admin {username} not found")

        return self.to_dict(admin)

    def change_password(self, username: str, password: str) -> dict:
        result = self.mongo.update_one({
            "username": username,
        }, {
            "$set": {
                "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
                "updated_at": current_datetime(),
            }
        })

        if result.matched_count == 0:
            raise Exception(f"Admin {username} not found")

        return {
            "username": username,
        }

    def add(self, username: str, creator: str) -> dict:
        if self.mongo.count_documents({
            "username": username,
        }) > 0:
            raise Exception(f"Username {username} is already taken")

        password = generate_secret(16)

        self.mongo.insert_one({
            "username": username,
            "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime(),
        })

        return {
            "username": username,
            "password": password,
        }

    def fetch(self, page: int = 0, size: int = 50) -> list[dict]:
        admins = self.mongo.find({}).skip(page * size).limit(size)
        return [self.to_dict(admin) for admin in admins]

    def delete(self, username: str) -> dict:
        result = self.mongo.delete_one({
            "username": username,
        })

        if result.deleted_count:
            raise Exception(f"Username {username} not found")

        return {
            "username": username,
        }

    def reset_password(self, username: str) -> dict:
        password = generate_secret(16)

        result = self.mongo.update_one({
            "username": username,
        }, {
            "$set": {
                "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
                "updated_at": current_datetime(),
            }
        })

        if result.matched_count == 0:
            raise Exception(f"Username {username} not found")

        return {
            "username": username,
            "password": password,
        }

    @staticmethod
    def to_dict(self) -> dict:
        return {
            "username": self["username"],
            "creator": self["creator"],
            "created_at": self["created_at"],
            "updated_at": self["updated_at"],
        }