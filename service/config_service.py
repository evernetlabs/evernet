from pymongo.collection import Collection

from utils.secret import generate_secret
from utils.time import current_datetime


class ConfigService:
    def __init__(self, mongo: Collection):
        self.mongo = mongo

    def set_if_absent(self, key: str, value: str):
        self.mongo.update_one({
            "key": key
        }, {
            "$setOnInsert": {
                "key": key,
                "value": value,
                "created_at": current_datetime(),
                "updated_at": current_datetime()
            }
        }, upsert=True)

    def set(self, key: str, value: str):
        self.mongo.update_one({
            "key": key
        }, {
            "$set": {
                "value": value,
                "updated_at": current_datetime()
            },
            "$setOnInsert": {
                "key": key,
                "created_at": current_datetime()
            }
        }, upsert=True)

    def get(self, key: str, default_value: str) -> str:
        record = self.mongo.find_one({"key": key})
        if record:
            return record["value"]
        return default_value

    def init(self, vertex_endpoint: str, vertex_display_name: str, vertex_description: str):
        self.set_if_absent("vertex_endpoint", vertex_endpoint)
        self.set_if_absent("vertex_display_name", vertex_display_name)
        self.set_if_absent("vertex_description", vertex_description)
        self.set_if_absent("federation_protocol", "http")
        self.set_if_absent("jwt_signing_key", generate_secret(128))

    def get_federation_protocol(self) -> str:
        return self.get("federation_protocol", "http")

    def set_federation_protocol(self, protocol: str):
        self.set("federation_protocol", protocol)

    def get_jwt_signing_key(self) -> str:
        return self.get("jwt_signing_key", "secret")

    def set_jwt_signing_key(self, key: str):
        self.set("jwt_signing_key", key)

    def get_vertex_endpoint(self) -> str:
        return self.get("vertex_endpoint", "localhost:8080")

    def set_vertex_endpoint(self, endpoint: str):
        self.set("vertex_endpoint", endpoint)

    def get_vertex_display_name(self) -> str:
        return self.get("vertex_display_name", "Evernet Vertex")

    def set_vertex_display_name(self, display_name: str):
        self.set("vertex_display_name", display_name)

    def get_vertex_description(self) -> str:
        return self.get("vertex_description", "An Evernet Vertex Instance")

    def set_vertex_description(self, description: str):
        self.set("vertex_description", description)