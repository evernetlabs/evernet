import uuid
from pymongo.collection import Collection
import bcrypt
import jwt
import time

from service.config_service import ConfigService
from util.date import current_datetime
from util.secret import generate_secret

class AdminService:
    
    def __init__(self, mongo: Collection, config_service: ConfigService) -> None:
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
            "updated_at": current_datetime()
        })

        self.config_service.init(vertex_endpoint, vertex_display_name, vertex_description, "http", generate_secret(128))
        
        return {
            "identifier": identifier
        }

    def get_token(self, identifier: str, password: str) -> dict:
        admin = self.mongo.find_one({"identifier": identifier})

        if not admin or not bcrypt.checkpw(password.encode(), admin.get("password").encode()):
            raise Exception("Invalid identifier and password combination")

        vertex_endpoint = self.config_service.get_vertex_endpoint()
        token = jwt.encode({
            "sub": admin.get("identifier"),
            "jti": str(uuid.uuid4()),
            "iat": int(time.time()),
            "exp": int(time.time()) + 3600,
            "type": "admin",
            "iss": vertex_endpoint,
            "aud": vertex_endpoint
        }, key=self.config_service.get_jwt_signing_key(), algorithm="HS256")

        return {"token": token}

    def get(self, identifier: str) -> dict:
        admin = self.mongo.find_one({"identifier": identifier})
        
        if not admin:
            raise Exception(f"Admin {identifier} not found")

        return self.to_dict(admin)

    @staticmethod
    def to_dict(admin) -> dict:
        return {
            "identifier": admin.get("identifier"),
            "creator": admin.get("creator"),
            "created_at": admin.get("created_at"),
            "updated_at": admin.get("updated_at")
        }
