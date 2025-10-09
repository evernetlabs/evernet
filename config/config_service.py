from pymongo.collection import Collection

from utils.secret_utils import generate_secret
from utils.time_utils import current_datetime


class ConfigService:

    def __init__(self, mongo: Collection):
        self.mongo = mongo

    def init(self, vertex_endpoint: str, vertex_display_name: str, vertex_description: str):
        self.upsert("jwt_signing_key", generate_secret(128))
        self.upsert("vertex_endpoint", vertex_endpoint)
        self.upsert("vertex_display_name", vertex_display_name)
        self.upsert("vertex_description", vertex_description)
        self.upsert("federation_protocol", "http")

    def upsert(self, key: str, value: str):
        self.mongo.update_one({
            "key": key
        }, {
            "$set": {
                "value": value,
                "updated_at": current_datetime()
            },
            "$setOnInsert": {
                "created_at": current_datetime()
            }
        }, upsert=True)

    def get(self, key: str, default_value: str) -> str:
        value = self.mongo.find_one({
            "key": key
        })

        if not value:
            return default_value

        return value["value"]

    def set(self, key: str, value: str):
        result = self.mongo.update_one({
            "key": key
        }, {
            "$set": {
                "value": value,
                "updated_at": current_datetime()
            }
        })

        if result.matched_count == 0:
            raise Exception(f"Config {key} not found")

    def get_jwt_signing_key(self):
        return self.get("jwt_signing_key", "secret")

    def get_vertex_endpoint(self):
        return self.get("vertex_endpoint", "localhost:5000")

    def get_vertex_display_name(self):
        return self.get("vertex_display_name", "Vertex")

    def get_vertex_description(self):
        return self.get("vertex_description", "Vertex")
