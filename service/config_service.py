from pymongo.collection import Collection

from util.secret import generate_secret
from util.time import current_datetime


class ConfigService:
    def __init__(self, mongo: Collection):
        self.mongo = mongo

    def set_if_missing(self, key: str, value: str):
        self.mongo.update_one({
            "key": key,
        }, {
            "$setOnInsert": {
                "key": key,
                "value": value,
                "created_at": current_datetime(),
                "updated_at": current_datetime(),
            }
        }, upsert=True)

    def set(self, key: str, value: str):
        self.mongo.update_one({
            "key": key,
        }, {
            "$setOnInsert": {
                "key": key,
                "created_at": current_datetime(),
            },
            "$set": {
                "value": value,
                "updated_at": current_datetime(),
            }
        })

    def get(self, key: str, value: str) -> str:
        config = self.mongo.find_one({
            "key": key,
        })

        if not config:
            raise value

        return config["value"]

    def init(self, vertex_endpoint: str, vertex_display_name: str, vertex_description: str, jwt_signing_key: str, federation_protocol: str):
        self.set_if_missing("vertex_endpoint", vertex_endpoint)
        self.set_if_missing("vertex_display_name", vertex_display_name)
        self.set_if_missing("vertex_description", vertex_description)
        self.set_if_missing("jwt_signing_key", jwt_signing_key)
        self.set_if_missing("federation_protocol", federation_protocol)

    def get_jwt_signing_key(self) -> str:
        return self.get("jwt_signing_key", "secret")

    def get_vertex_endpoint(self) -> str:
        return self.get("vertex_endpoint", "localhost:8080")

    def get_vertex_display_name(self) -> str:
        return self.get("vertex_display_name", "vertex")

    def get_vertex_description(self) -> str:
        return self.get("vertex_description", "vertex")

    def get_federation_protocol(self) -> str:
        return self.get("federation_protocol", "http")

    def set_vertex_endpoint(self, vertex_endpoint: str):
        self.set("vertex_endpoint", vertex_endpoint)

    def set_vertex_display_name(self, vertex_display_name: str):
        self.set("vertex_display_name", vertex_display_name)

    def set_vertex_description(self, vertex_description: str):
        self.set("vertex_description", vertex_description)

    def reset_jwt_signing_key(self):
        self.set("jwt_signing_key", generate_secret(128))

    def set_federation_protocol(self, federation_protocol: str):
        if federation_protocol not in ["http", "https"]:
            raise Exception(f"Invalid federation protocol {federation_protocol}")
        self.set("federation_protocol", federation_protocol)