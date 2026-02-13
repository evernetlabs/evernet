from montydb.collection import MontyCollection

from exception.errors import ClientError
from util.time import current_datetime


class ConfigService:
    def __init__(self, collection: MontyCollection):
        self.collection = collection

    def set(self, key: str, value: str):
        self.collection.update_one({
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
        result = self.collection.find_one({
            "key": key
        })

        if not result:
            return default_value

        return result.get("value")

    def set_if_absent(self, key: str, value: str):
        self.collection.update_one({
            "key": key
        }, {
            "$setOnInsert": {
                "key": key,
                "value": value,
                "created_at": current_datetime(),
                "updated_at": current_datetime()
            }
        }, upsert=True)

    def init(self, vertex_endpoint: str, vertex_display_name: str, vertex_description: str, jwt_signing_key: str, federation_protocol: str):
        if federation_protocol not in ["http", "https"]:
            raise ClientError(f"Invalid federation protocol {federation_protocol}")
        self.set_if_absent("vertex_endpoint", vertex_endpoint)
        self.set_if_absent("vertex_display_name", vertex_display_name)
        self.set_if_absent("vertex_description", vertex_description)
        self.set_if_absent("jwt_signing_key", jwt_signing_key)
        self.set_if_absent("federation_protocol", federation_protocol)

    def get_vertex_endpoint(self):
        return self.get("vertex_endpoint", "localhost:8080")

    def get_vertex_display_name(self):
        return self.get("vertex_display_name", "Vertex")

    def get_vertex_description(self):
        return self.get("vertex_description", "Vertex")

    def get_federation_protocol(self):
        return self.get("federation_protocol", "http")

    def get_jwt_signing_key(self):
        return self.get("jwt_signing_key", "secret")