from pymongo.collection import Collection
from util.date import current_datetime



class ConfigService:
    
    def __init__(self, mongo: Collection) -> None:
        self.mongo = mongo

    def set_if_missing(self, key: str, value):
        self.mongo.update_one({
            "key": key
        }, 
        {
            "$setOnInsert": {
                "key": key,
                "value": value,
                "created_at": current_datetime(),
                "updated_at": current_datetime()
            }
        }, upsert=True)

    def set(self, key: str, value):
        self.mongo.find_one_and_update({
            "key": key
        }, {
            "$setOnInsert": {
                "key": key,
                "created_at": current_datetime()
            },
            "$set": {
                "value": value,
                "updated_at": current_datetime()
            }
        })
    
    def get(self, key: str, default_value: str = None) -> str:
        config = self.mongo.find_one({"key": key})
        return config.get("value") if config else default_value

    def init(self, vertex_endpoint: str, vertex_display_name: str, vertex_description: str, federation_protocol: str, jwt_signing_key: str):
        self.set_if_missing("vertex_endpoint", vertex_endpoint)
        self.set_if_missing("vertex_display_name", vertex_display_name)
        self.set_if_missing("vertex_description", vertex_description)
        self.set_if_missing("federation_protocol", federation_protocol)
        self.set_if_missing("jwt_signing_key", jwt_signing_key)
    
    def set_vertex_endpoint(self, endpoint: str):
        self.set("vertex_endpoint", endpoint)
    
    def get_vertex_endpoint(self) -> str:
        return self.get("vertex_endpoint", "localhost:5000")

    def set_vertex_display_name(self, name: str):
        self.set("vertex_display_name", name)
    
    def get_vertex_display_name(self) -> str:
        return self.get("vertex_display_name", "Evernet Vertex")

    def set_vertex_description(self, description: str):
        self.set("vertex_description", description)
    
    def get_vertex_description(self) -> str:
        return self.get("vertex_description", "A decentralized vertex for Evernet.")

    def set_federation_protocol(self, protocol: str):
        self.set("federation_protocol", protocol)
    
    def get_federation_protocol(self) -> str:
        return self.get("federation_protocol", "http")

    def set_jwt_signing_key(self, key: str):
        self.set("jwt_signing_key", key)
    
    def get_jwt_signing_key(self) -> str:
        return self.get("jwt_signing_key", "secret")
