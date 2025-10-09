import time
import uuid

import bcrypt
import jwt
from pymongo.collection import Collection

from config.config_service import ConfigService
from utils.secret_utils import generate_secret
from utils.time_utils import current_datetime


class AdminService:
    def __init__(self, mongo: Collection, config_service: ConfigService):
        self.mongo = mongo
        self.config_service = config_service

    def init(self, identifier: str, password: str, vertex_endpoint: str, vertex_display_name: str, vertex_description: str) -> dict:
        if self.mongo.count_documents({}) > 0:
            raise Exception("You are not allowed to perform this action")

        self.mongo.insert_one({
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            "creator": identifier,
            "created_at": current_datetime(),
            "updated_at": current_datetime(),
        })

        self.config_service.init(vertex_endpoint, vertex_display_name, vertex_description)

        return {
            "identifier": identifier,
        }

    def get_token(self, identifier: str, password: str) -> dict:
        admin = self.mongo.find_one({"identifier": identifier})

        if not admin or not bcrypt.checkpw(password.encode(), admin["password"].encode()):
            raise Exception("Invalid identifier and password combination")

        vertex_endpoint = self.config_service.get_vertex_endpoint()

        token = jwt.encode({
            "sub": admin["identifier"],
            "iat": int(time.time()),
            "exp": int(time.time()) + 3600,
            "type": "admin",
            "jti": str(uuid.uuid4()),
            "iss": vertex_endpoint,
            "aud": vertex_endpoint,
        }, key=self.config_service.get_jwt_signing_key(), algorithm="HS256")

        return {
            "token": token
        }

    def get(self, identifier: str) -> dict:
        admin = self.mongo.find_one({
            'identifier': identifier
        })

        if not admin:
            raise Exception(f'Admin {identifier} not found')

        return self.to_dict(admin)

    def change_password(self, identifier: str, password: str) -> dict:
        fields = {
            'password': bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            'updated_at': current_datetime(),
        }

        result = self.mongo.update_one({
            'identifier': identifier,
        }, {
            '$set': fields
        })

        if result.matched_count == 0:
            raise Exception(f'Admin {identifier} not found')

        return {
            'identifier': identifier,
        }

    def add(self, identifier: str, creator: str) -> dict:
        if self.mongo.count_documents({
            'identifier': identifier
        }) > 0:
            raise Exception(f'Admin {identifier} already exists')

        password = generate_secret(16)

        self.mongo.insert_one({
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime(),
        })

        return {
            'identifier': identifier,
            'password': password,
        }

    def fetch(self, page: int = 0, size: int = 50) -> list[dict]:
        admins = self.mongo.find({}).skip(page * size).limit(size)

        result = []

        for admin in admins:
            result.append(self.to_dict(admin))

        return result

    def reset_password(self, identifier: str) -> dict:
        password = generate_secret(16)
        result = self.change_password(identifier, password)
        result["password"] = password
        return result

    def delete(self):
        pass

    @staticmethod
    def to_dict(self):
        return {
            'id': str(self.get('_id')),
            'identifier': self.get('identifier'),
            'created_at': self.get('created_at'),
            'updated_at': self.get('updated_at'),
            'creator': self.get('creator'),
        }
