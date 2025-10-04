import json
import time
import uuid

import bcrypt
import jwt
import plyvel

from utils.time_utils import now
from vertex.config.config_manager import ConfigManager


class AdminManager:
    def __init__(self, db: plyvel.DB, config_manager: ConfigManager):
        self.db = db
        self.config_manager = config_manager

    def init(self, identifier: str, password: str, vertex_endpoint: str, vertex_display_name: str, vertex_description: str):
        exists = next(self.db.iterator(prefix=b"admin:"), None) is not None

        if exists:
            raise Exception("You are not allowed to perform this action")

        admin = {
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            "creator": identifier,
            "created_at": now(),
            "updated_at": now(),
        }

        admin_json_str = json.dumps(admin)
        self.db.put(f"admin:{identifier}".encode("utf-8"), admin_json_str.encode("utf-8"))

        self.config_manager.init(
            vertex_endpoint,
            vertex_display_name,
            vertex_description
        )

        return {
            "identifier": identifier,
        }

    def get_token(self, identifier: str, password: str) -> dict:
        admin_json = self.db.get(f'admin:{identifier}'.encode('utf-8'))
        if not admin_json:
            raise Exception("Invalid identifier and password combination")

        admin = json.loads(admin_json)

        if not bcrypt.checkpw(password.encode('utf-8'), admin["password"].encode('utf-8')):
            raise Exception("Invalid identifier and password combination")

        jwt_signing_key = self.config_manager.get_jwt_signing_key()
        vertex_endpoint = self.config_manager.get_vertex_endpoint()

        token = jwt.encode({
            'sub': admin["identifier"],
            'iss': vertex_endpoint,
            'aud': vertex_endpoint,
            'iat': int(time.time()),
            'exp': int(time.time()) + 60 * 60 * 24,
            'type': 'admin',
            'jti': str(uuid.uuid4()),
        }, algorithm='HS256', key=jwt_signing_key)

        return {
            'token': token
        }

    def get(self, identifier: str) -> dict:
        admin_json = self.db.get(f'admin:{identifier}'.encode('utf-8'))

        if not admin_json:
            raise Exception(f"Admin {identifier} not found")

        admin = json.loads(admin_json)

        return {
            "identifier": admin["identifier"],
            "creator": admin["creator"],
            "created_at": admin["created_at"],
            "updated_at": admin["updated_at"],
        }
