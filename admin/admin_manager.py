import time
from datetime import datetime

from pymongo.collection import Collection
import bcrypt
import jwt


class AdminManager:
    def __init__(self, mongo: Collection, jwt_signing_key: str, vertex_endpoint: str):
        self.mongo = mongo
        self.jwt_signing_key = jwt_signing_key
        self.vertex_endpoint = vertex_endpoint

    def init(self, identifier: str, password: str):
        if not identifier.isalnum():
            raise Exception("Identifier must be alphanumeric")
        if len(identifier) < 3 or len(identifier) > 32:
            raise Exception("Identifier must be between 3 and 32 characters")
        if password.strip() == "":
            raise Exception("Password cannot be an empty string")
        if len(password) < 8 or len(password) > 128:
            raise Exception("Password must be between 8 and 128 characters")

        identifier = identifier.lower()

        if self.mongo.count_documents({}) != 0:
            raise Exception("You are not allowed to perform this action")

        admin_id = self.mongo.insert_one({
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            "created_at": datetime.now(),
            "updated_at": datetime.now(),
        }).inserted_id

        return {
            "id": str(admin_id),
            "identifier": identifier,
        }

    def get_token(self, identifier: str, password: str) -> dict:
        identifier = identifier.lower()
        admin = self.mongo.find_one({"identifier": identifier})
        if not admin:
            raise Exception(f"Admin {identifier} not found")

        if not bcrypt.checkpw(password.encode(), admin["password"].encode()):
            raise Exception("Invalid password")

        return {
            "token": jwt.encode({
                "sub": admin["identifier"],
                "type": "admin",
                "iat": int(time.time()),
                "iss": self.vertex_endpoint,
                "aud": self.vertex_endpoint
            }, algorithm="HS256", key=self.jwt_signing_key),
        }

    def fetch(self, page=0, size=50) -> list[dict]:
        admins = self.mongo.find({}).skip(page * size).limit(size)
        result = []
        for admin in admins:
            result.append(self.to_dict(admin))
        return result

    def get(self, identifier: str) -> dict:
        admin = self.mongo.find_one({"identifier": identifier})
        if not admin:
            raise Exception(f"Admin {identifier} not found")
        return self.to_dict(admin)

    @staticmethod
    def to_dict(self):
        return {
            "id": str(self["_id"]),
            "identifier": self["identifier"],
            "created_at": self["created_at"],
            "updated_at": self["updated_at"],
        }