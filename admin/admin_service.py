import bcrypt
from pymongo.collection import Collection

from config.config_service import ConfigService
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
