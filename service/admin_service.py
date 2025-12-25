import uuid
import jwt
from mongita.collection import Collection
from .config_service import ConfigService
import bcrypt
import time
from libs.time_utils import current_datetime
from libs.secret_utils import generate_secret


class AdminService:
    def __init__(self, mongo: Collection, config_service: ConfigService) -> None:
        self.mongo = mongo
        self.config_service = config_service

    def init(self, identifier: str, password: str, vertex_endpoint: str, vertex_display_name: str, vertex_description: str) -> dict:
        if self.mongo.count_documents({}) > 0:
            raise Exception("You are not allowed to perform this action")

        self.mongo.insert_one({
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
            "creator": identifier,
            "created_at": current_datetime(),
            "updated_at": current_datetime()
        })

        self.config_service.init(vertex_endpoint, vertex_display_name, vertex_description, generate_secret(128), "http")
        
        return {
            "identifier": identifier
        }
    
    def get_token(self, identifier: str, password: str) -> dict:
        admin = self.mongo.find_one({
            "identifier": identifier
        })

        if not admin:
            raise Exception("Invalid identifier and password combination")

        if not bcrypt.checkpw(password.encode("utf-8"), admin.get("password").encode("utf-8")):
            raise Exception("Invalid identifier and password combination")
        
        vertex_endpoint = self.config_service.get_vertex_endpoint()
        jwt_signing_key = self.config_service.get_jwt_signing_key()

        token = jwt.encode({
            "sub": admin.get("identifier"),
            "type": "admin",
            "iss": vertex_endpoint,
            "aud": vertex_endpoint,
            "iat": int(time.time()),
            "exp": int(time.time()) + 60 * 60,
            "jti": str(uuid.uuid4())
        }, algorithm="HS256", key=jwt_signing_key)
        
        return {
            "token": token
        }
