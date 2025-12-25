from mongita.collection import Collection
from libs.time_utils import current_datetime


class ConfigService:
    def __init__(self, mongo: Collection) -> None:
        self.mongo = mongo
    
    def set_if_absent(self, key: str, value: str) -> None:
        if self.mongo.count_documents({
            "key": key
        }) > 0:
            return

        self.mongo.insert_one({
            "key": key,
            "value": value,
            "created_at": current_datetime(),
            "updated_at": current_datetime()
        })

    def set(self, key: str, value: str) -> None:
        if self.mongo.count_documents({
            "key": key
        }) > 0:
            self.mongo.update_one({
                "key": key
            }, {
                "$set": {
                    "value": value,
                    "updated_at": current_datetime()
                }
            })
        else:
            self.mongo.insert_one({
                "key": key,
                "value": value,
                "created_at": current_datetime(),
                "updated_at": current_datetime()
            })

    def get(self, key: str, default_value: str = "") -> str:
        config = self.mongo.find_one({
            "key": key
        })
        if not config:
            return default_value
        return config.get("value")

    def init(self, vertex_endpoint: str, vertex_display_name: str, vertex_description: str, jwt_signing_key: str, federation_protocol: str) -> None:
        self.set_if_absent("vertex_endpoint", vertex_endpoint)
        self.set_if_absent("vertex_display_name", vertex_display_name)
        self.set_if_absent("vertex_description", vertex_description)
        self.set_if_absent("jwt_signing_key", jwt_signing_key)
        self.set_if_absent("federation_protocol", federation_protocol)
    
    def get_vertex_endpoint(self) -> str:
        return self.get("vertex_endpoint")
    
    def get_vertex_display_name(self) -> str:
        return self.get("vertex_display_name")

    def get_vertex_description(self) -> str:
        return self.get("vertex_description")

    def get_jwt_signing_key(self) -> str:
        return self.get("jwt_signing_key")

    def get_federation_protocol(self) -> str:
        return self.get("federation_protocol")

    def set_vertex_endpoint(self, vertex_endpoint: str) -> None:
        self.set("vertex_endpoint", vertex_endpoint)
    
    def set_vertex_display_name(self, vertex_display_name: str) -> None:
        self.set("vertex_display_name", vertex_display_name)
    
    def set_vertex_description(self, vertex_description: str) -> None:
        self.set("vertex_description", vertex_description)
    
    def set_jwt_signing_key(self, jwt_signing_key: str) -> None:
        self.set("jwt_signing_key", jwt_signing_key)
    
    def set_federation_protocol(self, federation_protocol: str) -> None:
        self.set("federation_protocol", federation_protocol)
