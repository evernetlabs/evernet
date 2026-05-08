import bcrypt
from pymongo.collection import Collection
from service.node_service import NodeService
from util.auth import generate_user_token
from util.secret import generate_secret
from util.time import current_datetime


class UserService:
    def __init__(self, mongo: Collection, node_service: NodeService):
        self.mongo = mongo
        self.node_service = node_service

    def sign_up(self, node_identifier: str, username: str, password: str, display_name: str) -> dict:
        node = self.node_service.get(node_identifier)
        if not node["open"]:
            raise Exception("You are not allowed to perform this action")

        if self.mongo.count_documents({
            "node_identifier": node_identifier,
            "username": username,
        }) > 0:
            raise Exception(f"Username {username} is already taken on node {node_identifier}")

        self.mongo.insert_one({
            "node_identifier": node_identifier,
            "username": username,
            "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
            "display_name": display_name,
            "creator": None,
            "created_at": current_datetime(),
            "updated_at": current_datetime(),
        })

        return {
            "username": username,
            "node_identifier": node_identifier,
        }

    def get_token(self, node_identifier: str, username: str, password: str, target_node_address: str | None = None) -> dict:
        user = self.mongo.find_one({
            "node_identifier": node_identifier,
            "username": username
        })

        if not user or not bcrypt.checkpw(password.encode("utf-8"), user["password"].encode("utf-8")):
            raise Exception("Invalid username or password")

        token = generate_user_token(user["node_identifier"], user["username"], target_node_address)

        return {
            "token": token,
        }

    def get(self, node_identifier: str, username: str) -> dict:
        user = self.mongo.find_one({
            "node_identifier": node_identifier,
            "username": username
        })

        if not user:
            raise Exception(f"User {username} not found on node {node_identifier}")

        return self.to_dict(user)

    def update(self, node_identifier: str, username: str, display_name: str) -> dict:
        fields = {
            "updated_at": current_datetime(),
        }

        if display_name:
            fields["display_name"] = display_name

        result = self.mongo.update_one({
            "node_identifier": node_identifier,
            "username": username
        }, {
            "$set": fields
        })

        if result.matched_count == 0:
            raise Exception(f"User {username} not found on node {node_identifier}")

        return {
            "username": username,
            "node_identifier": node_identifier,
        }

    def change_password(self, node_identifier: str, username: str, password: str) -> dict:
        result = self.mongo.update_one({
            "node_identifier": node_identifier,
            "username": username
        }, {
            "$set": {
                "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
                "updated_at": current_datetime(),
            }
        })

        if result.matched_count == 0:
            raise Exception(f"User {username} not found on node {node_identifier}")

        return {
            "username": username,
            "node_identifier": node_identifier,
        }

    def delete(self, node_identifier: str, username: str) -> dict:
        result = self.mongo.delete_one({
            "node_identifier": node_identifier,
            "username": username
        })

        if result.deleted_count == 0:
            raise Exception(f"User {username} not found on node {node_identifier}")

        return {
            "username": username,
            "node_identifier": node_identifier,
        }

    def add(self, node_identifier: str, username: str, display_name: str, creator: str) -> dict:
        node = self.node_service.get(node_identifier)

        if self.mongo.count_documents({
            "node_identifier": node_identifier,
            "username": username,
        }) > 0:
            raise Exception(f"User {username} is already taken on node {node_identifier}")

        password = generate_secret(16)

        self.mongo.insert_one({
            "node_identifier": node_identifier,
            "username": username,
            "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
            "display_name": display_name,
            "creator": creator,
            "created_at": current_datetime(),
            "updated_at": current_datetime(),
        })

        return {
            "username": username,
            "node_identifier": node_identifier,
            "password": password,
        }

    def fetch(self, node_identifier: str, page: int = 0, size: int = 50) -> list[dict]:
        users = self.mongo.find({
            "node_identifier": node_identifier,
        }).skip(page * size).limit(size)

        return [self.to_dict(user) for user in users]

    def reset_password(self, node_identifier: str, username: str) -> dict:
        password = generate_secret(16)
        result = self.mongo.update_one({
            "node_identifier": node_identifier,
            "username": username,
        }, {
            "$set": {
                "password": bcrypt.hashpw(password.encode("utf-8"), bcrypt.gensalt()).decode("utf-8"),
                "updated_at": current_datetime(),
            }
        })

        if result.matched_count == 0:
            raise Exception(f"User {username} not found on node {node_identifier}")

        return {
            "username": username,
            "node_identifier": node_identifier,
            "password": password,
        }

    @staticmethod
    def to_dict(self):
        return {
            "username": self["username"],
            "display_name": self["display_name"],
            "node_identifier": self["node_identifier"],
            "creator": self["creator"],
            "created_at": self["created_at"],
            "updated_at": self["updated_at"],
        }