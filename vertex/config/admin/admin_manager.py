import json

import bcrypt
import plyvel

from utils.time_utils import now
from vertex.config.config_manager import ConfigManager


class AdminManager:
    def __init__(self, db: plyvel.DB, config_manager: ConfigManager):
        self.db = db
        self.config_manager = config_manager

    def init(self, identifier: str, password: str, vertex_endpoint: str, vertex_display_name: str, vertex_description: str):
        exists = next(self.db.iterator(prefix=b"admin:"), None) is not None

        if exists:
            raise Exception("You are not allowed to perform this action")

        admin = {
            "identifier": identifier,
            "password": bcrypt.hashpw(password.encode(), bcrypt.gensalt()).decode(),
            "creator": identifier,
            "created_at": now(),
            "updated_at": now(),
        }

        admin_json_str = json.dumps(admin)
        self.db.put(f"admin:{identifier}".encode("utf-8"), admin_json_str.encode("utf-8"))

        self.config_manager.init(
            vertex_endpoint,
            vertex_display_name,
            vertex_description
        )

        return {
            "identifier": identifier,
        }
