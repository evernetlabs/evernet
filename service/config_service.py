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
        return self.__get_key("jwt_signing_key")

    def get_vertex_endpoint(self):
        return self.__get_key("vertex_endpoint")

    def get_federation_protocol(self):
        return self.__get_key("federation_protocol")

    def get_vertex_display_name(self):
        return self.__get_key("vertex_display_name")

    def get_vertex_description(self):
        return self.__get_key("vertex_description")

    def __set_if_missing(self, key: str, value: str):
        if self.mongo.count_documents({"key": key}) > 0:
            return
        
        self.mongo.insert_one({
            "key": key,
            "value": value,
            "created_at": current_datetime(),
            "updated_at": current_datetime()
        })
 
 
    def __get_key(self, key: str) -> str:
        return self.mongo.find_one({
            "key": key
        }).get("value", None)
