import plyvel

from utils.secret_utils import generate_secret


class ConfigManager:
    def __init__(self, db: plyvel.DB):
        self.db = db

    def set(self, key: str, value: str):
        self.db.put(key.encode("utf-8"), value.encode("utf-8"))

    def get(self, key: str, default: str = "") -> str:
        return self.db.get(key.encode("utf-8"), default=default).decode("utf-8")

    def init(self, vertex_endpoint: str, vertex_display_name: str, vertex_description: str):
        self.set("CONFIG:JWT_SIGNING_KEY", generate_secret(128))
        self.set("CONFIG:VERTEX_ENDPOINT", vertex_endpoint)
        self.set("CONFIG:VERTEX_DISPLAY_NAME", vertex_display_name)
        self.set("CONFIG:VERTEX_DESCRIPTION", vertex_description)

    def get_jwt_signing_key(self) -> str:
        return self.get("CONFIG:JWT_SIGNING_KEY", "secret")

    def get_vertex_endpoint(self) -> str:
        return self.get("CONFIG:VERTEX_ENDPOINT", "localhost:5000")

    def get_vertex_display_name(self) -> str:
        return self.get("CONFIG:VERTEX_DISPLAY_NAME", "Vertex")

    def get_vertex_description(self) -> str:
        return self.get("CONFIG:VERTEX_DESCRIPTION", "Vertex")
