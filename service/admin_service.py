from mongita.collection import Collection
from .config_service import ConfigService
import bcrypt
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
        