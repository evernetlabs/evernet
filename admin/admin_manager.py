import time
from datetime import datetime
from password_generator import PasswordGenerator
from pymongo.collection import Collection
import bcrypt
import jwt


class AdminManager:
    def __init__(self, mongo: Collection, jwt_signing_key: str, vertex_endpoint: str):
        self.mongo = mongo
        self.jwt_signing_key = jwt_signing_key
        self.vertex_endpoint = vertex_endpoint
        self.password_generator = PasswordGenerator()
        self.password_generator.minlen = 16
        self.password_generator.maxlen = 16

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
            "creator": None,
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

    def change_password(self, identifier: str, password: str) -> dict:
        if password.strip() == "":
            raise Exception("Password cannot be an empty string")
        if len(password) < 8 or len(password) > 128:
            raise Exception("Password must be between 8 and 128 characters")
        fields = {
            "updated_at": datetime.now(),
            "password": bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
        }

        result = self.mongo.update_one({
            "identifier": identifier,
        }, {
            "$set": fields
        })

        if result.matched_count == 0:
            raise Exception(f"Admin {identifier} not found")

        return {
            "identifier": identifier,
        }

    def add(self, identifier: str, creator: str) -> dict:
        if not identifier.isalnum():
            raise Exception("Identifier must be alphanumeric")
        if len(identifier) < 3 or len(identifier) > 32:
            raise Exception("Identifier must be between 3 and 32 characters")

        password = self.password_generator.generate()

        if self.mongo.count_documents({
            "identifier": identifier,
        }) != 0:
            raise Exception(f"Admin {identifier} already exists")

        admin_id = self.mongo.insert_one({
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            "creator": creator,
            "created_at": datetime.now(),
            "updated_at": datetime.now(),
        }).inserted_id

        return {
            "id": str(admin_id),
            "identifier": identifier,
            "password": password,
        }

    def delete(self):
        pass

    def reset_password(self):
        pass

    @staticmethod
    def to_dict(self):
        return {
            "id": str(self["_id"]),
            "identifier": self.get("identifier"),
            "created_at": self.get("created_at"),
            "updated_at": self.get("updated_at"),
            "creator": self.get("creator"),
        }