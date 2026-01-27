import time
import uuid

import bcrypt
import jwt
from pymongo.collection import Collection

from exception.types import AuthorizationException, AuthenticationException, ClientException, NotFoundException
from service.config_service import ConfigService
from utils.secret import generate_secret
from utils.time import current_datetime


class AdminService:
    def __init__(self, mongo: Collection, config_service: ConfigService):
        self.mongo = mongo
        self.config_service = config_service

    def init(self, identifier: str, password: str, vertex_endpoint: str, vertex_display_name: str, vertex_description: str) -> dict:
        if self.mongo.count_documents({}) > 0:
            raise AuthorizationException("You are not allowed to perform this action")

        self.mongo.insert_one({
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
            "creator": identifier,
            "created_at": current_datetime(),
            "updated_at": current_datetime(),
        })

        self.config_service.init(
            vertex_endpoint,
            vertex_display_name,
            vertex_description
        )

        return {
            "identifier": identifier
        }

    def get_token(self, identifier: str, password: str) -> dict:
        admin = self.mongo.find_one({"identifier": identifier})

        if not admin or not bcrypt.checkpw(password.encode("utf-8"), admin["password"].encode("utf-8")):
            raise AuthenticationException("Invalid identifier and password combination")

        vertex_endpoint = self.config_service.get_vertex_endpoint()
        jwt_signing_key = self.config_service.get_jwt_signing_key()

        token = jwt.encode({
            "sub": identifier,
            "type": "admin",
            "iss": vertex_endpoint,
            "aud": vertex_endpoint,
            "iat": int(time.time()),
            "exp": int(time.time()) + 3600 * 24,
            "jti": str(uuid.uuid4()),
        }, key=jwt_signing_key, algorithm="HS256")

        return {
            "token": token
        }

    def get(self, identifier: str) -> dict:
        admin = self.mongo.find_one({"identifier": identifier})

        if not admin:
            raise NotFoundException(f"Admin {identifier} not found")

        return self.to_dict(admin)

    def change_password(self, identifier: str, password: str) -> dict:
        result = self.mongo.update_one({
            "identifier": identifier
        }, {
            "$set": {
                "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
                "updated_at": current_datetime(),
            }
        })

        if result.matched_count == 0:
            raise NotFoundException(f"Admin {identifier} not found")

        return {
            "identifier": identifier
        }

    def add(self, identifier: str, creator: str) -> dict:
        if self.mongo.count_documents({
            "identifier": identifier
        }) > 0:
            raise ClientException(f"Admin {identifier} already exists")

        password = generate_secret(16)

        self.mongo.insert_one({
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime(),
        })

        return {
            "identifier": identifier,
            "password": password
        }

    def fetch(self, page: int = 0, size: int = 50) -> list[dict]:
        admins = self.mongo.find().skip(page * size).limit(size)
        return [self.to_dict(admin) for admin in admins]

    def delete(self, identifier: str) -> dict:
        result = self.mongo.delete_one({
            "identifier": identifier
        })

        if result.deleted_count == 0:
            raise NotFoundException(f"Admin {identifier} not found")

        return {
            "identifier": identifier
        }

    def reset_password(self, identifier: str):
        password = generate_secret(16)
        self.change_password(identifier, password)
        return {
            "identifier": identifier,
            "password": password
        }

    @staticmethod
    def to_dict(self):
        return {
            'id': str(self.get('_id')),
            'identifier': self.get('identifier'),
            'creator': self.get('creator'),
            'created_at': self.get('created_at'),
            'updated_at': self.get('updated_at')
        }