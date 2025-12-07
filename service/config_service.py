from mongita.collection import Collection
from utils.time_utils import current_datetime

class ConfigService:
    def __init__(self, mongo: Collection):
        self.mongo = mongo

    def init(self, vertex_endpoint: str, vertex_display_name: str, vertex_description: str, jwt_sigining_key: str, federation_protocol: str):
        self.__set_if_missing("vertex_endpoint", vertex_endpoint)
        self.__set_if_missing("vertex_display_name", vertex_display_name)
        self.__set_if_missing("vertex_description", vertex_description)
        self.__set_if_missing("jwt_signing_key", jwt_sigining_key)
        self.__set_if_missing("federation_protocol", federation_protocol)

    def get_jwt_signing_key(self):
        return self.__get("jwt_signing_key")

    def set_jwt_signing_key(self, jwt_sigining_key):
        self.__set("jwt_signing_key", jwt_sigining_key)

    def get_vertex_endpoint(self):
        return self.__get("vertex_endpoint")

    def set_vertex_endpoint(self, vertex_endpoint):
        self.__set("vertex_endpoint", vertex_endpoint)

    def get_federation_protocol(self):
        return self.__get("federation_protocol")

    def set_federation_protocol(self, federation_protocol):
        if federation_protocol not in ["http", "https"]:
            raise Exception(f"Invalid federation protocol {federation_protocol}")
        self.__set("federation_protocol", federation_protocol)

    def get_vertex_display_name(self):
        return self.__get("vertex_display_name")

    def set_vertex_display_name(self, vertex_display_name):
        self.__set("vertex_display_name", vertex_display_name)

    def get_vertex_description(self):
        return self.__get("vertex_description")

    def set_vertex_description(self, vertex_description):
        self.__set("vertex_description", vertex_description)

    def __set_if_missing(self, key: str, value: str):
        if self.mongo.count_documents({"key": key}) > 0:
            return
        
        self.mongo.insert_one({
            "key": key,
            "value": value,
            "created_at": current_datetime(),
            "updated_at": current_datetime()
        })

    def __set(self, key: str, value: str):
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
 
 
    def __get(self, key: str) -> str:
        return self.mongo.find_one({
            "key": key
        }).get("value", None)
